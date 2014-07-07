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

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
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