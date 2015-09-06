package org.star_lang.star.compiler.type;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.Shriek;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeChecker.PtnVarHandler;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class RuleVarHandler implements PtnVarHandler
{
  private final AccessMode access;
  private final Visibility visibility;
  private final Dictionary outer;
  private final ErrorReport errors;

  public RuleVarHandler(Dictionary outer, ErrorReport errors)
  {
    this.access = AccessMode.readOnly;
    this.visibility = Visibility.pUblic;
    this.outer = outer;
    this.errors = errors;
  }

  public RuleVarHandler(AccessMode access, Visibility visibility, Dictionary outer, ErrorReport errors)
  {
    this.access = access;
    this.visibility = visibility;
    this.outer = outer;
    this.errors = errors;
  }

  @Override
  public Variable typeOfVariable(IAbstract ptn, IType expectedType, Wrapper<ICondition> condition,
      Permission duplicates, Dictionary dict)
  {
    String vrName = Abstract.getId(ptn);
    Location loc = ptn.getLoc();

    if (isLocalVariable(vrName, dict, outer)) {
      // Duplicate occurrence of a variable
      DictInfo info = dict.getVar(vrName);
      Variable var = info.getVariable();

      IType varType = info.getType();
      if (TypeUtils.isOverloadedType(varType))
        varType = TypeUtils.getOverloadedType(varType);

      if (TypeUtils.isReferenceType(varType)) {
        IType refType = TypeUtils.referencedType(varType);
        try {
          Subsume.same(expectedType, refType, loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError("variable " + vrName + ":" + refType + " not consistent with expected type: "
              + expectedType, Location.merge(loc, e.getLocs()));
        }

        Variable nVar = new Variable(loc, refType, GenSym.genSym("__"));

        try {
          IType trial = new TypeVar();
          ((TypeVar) trial).addContractRequirement((TypeExp) TypeUtils.typeExp(StandardNames.EQUALITY, trial), loc,
              dict);
          Subsume.same(refType, trial, loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError(
              "cannot have multiple occurrences of variables whose type does not support equality\nbecause "
                  + e.getMessage(), Location.merge(loc, e.getLocs()));
        }

        CompilerUtils.extendCondition(condition, CompilerUtils.equals(loc, new Shriek(loc, var), nVar));
        return nVar;
      } else {
        try {
          Subsume.same(expectedType, varType, loc, dict);
        } catch (TypeConstraintException e) {
          errors.reportError("type of variable " + vrName + ":" + varType
              + " not consistent with expected type\nbecause " + e.getMessage(), loc);
        }

        if (!info.isInitialized() && info instanceof VarInfo) {
          VarInfo varInfo = (VarInfo) info;
          varInfo.setInitialized(true);
          varInfo.setAccess(access);
          return info.getVariable();
        } else {
          Variable nVar = new Variable(loc, varType, GenSym.genSym("__"));

          try {
            TypeVar trial = new TypeVar();
            trial.addContractRequirement((TypeExp) TypeUtils.typeExp(StandardNames.EQUALITY, trial), loc, dict);
            Subsume.same(varType, trial, loc, dict);
          } catch (TypeConstraintException e) {
            errors.reportError(
                "cannot have multiple occurrences of variables whose type does not support equality\nbecause "
                    + e.getMessage(), Location.merge(loc, e.getLocs()));
          }

          CompilerUtils.extendCondition(condition, CompilerUtils.equals(loc, var, nVar));
          return nVar;
        }
      }
    } else if (duplicates != Permission.allowed && dict.isDefinedVar(vrName)) {
      DictInfo info = dict.getVar(vrName);
      errors.reportError(StringUtils.msg("variable ", vrName,
          " is not permitted in this pattern because it is declared at ", info.getLoc()), loc, info.getLoc());
    } else
      dict.declareVar(vrName, new Variable(loc, expectedType, vrName), access, visibility, true);

    return new Variable(loc, expectedType, vrName);
  }

  private boolean isLocalVariable(String name, Dictionary cxt, Dictionary outer)
  {
    return cxt.isLocallyDeclared(name, outer) || cxt.isConstructor(name);
  }
}