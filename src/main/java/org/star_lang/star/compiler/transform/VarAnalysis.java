package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.star_lang.star.compiler.canonical.*;
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

public class VarAnalysis
{

  static void findVars(IContentPattern ptn, final List<Variable> foundVars, final VarChecker checker)
  {
    final CanonicalVisitor analyser = new DefaultVisitor(true) {
      @Override
      public void visitMethodVariable(MethodVariable mtd)
      {
        visitVariable(mtd);
      }

      @Override
      public void visitOverloadedVariable(OverloadedVariable over)
      {
        visitVariable(over);
      }

      @Override
      public void visitVariable(Variable var)
      {
        if (checker.isThisOk(var) && !foundVars.contains(var))
          foundVars.add(var);
      }
    };

    ptn.accept(analyser);
  }

  public static void findDefinedVars(ICondition qTerm, List<Variable> foundVars, VarChecker checker)
  {
    if (qTerm instanceof Search)
      findVars(((Search) qTerm).getPtn(), foundVars, checker);
    else if (qTerm instanceof ListSearch) {
      ListSearch search = (ListSearch) qTerm;
      findVars(search.getPtn(), foundVars, checker);
      findVars(search.getIx(), foundVars, checker);
    } else if (qTerm instanceof Matches) {
      Matches test = (Matches) qTerm;

      findVars(test.getPtn(), foundVars, checker);
    } else if (qTerm instanceof Conjunction) {
      Conjunction conj = (Conjunction) qTerm;

      findDefinedVars(conj.getLhs(), foundVars, checker);
      findDefinedVars(conj.getRhs(), foundVars, checker);
    } else if (qTerm instanceof Disjunction) {
      Disjunction disj = (Disjunction) qTerm;

      List<Variable> leftFound = new ArrayList<>();
      List<Variable> rightFound = new ArrayList<>();

      findDefinedVars(disj.getLhs(), leftFound, checker);
      findDefinedVars(disj.getRhs(), rightFound, checker);

      VarAnalysis.mergeFound(leftFound, rightFound, foundVars);
    } else if (qTerm instanceof Negation) {
    } else if (qTerm instanceof Otherwise) {
      Otherwise other = (Otherwise) qTerm;
      List<Variable> leftFound = new ArrayList<>();
      List<Variable> rightFound = new ArrayList<>();

      findDefinedVars(other.getLhs(), leftFound, checker);
      findDefinedVars(other.getRhs(), rightFound, checker);

      VarAnalysis.mergeFound(leftFound, rightFound, foundVars);
    } else if (qTerm instanceof ConditionCondition) {
      ConditionCondition cond = (ConditionCondition) qTerm;

      List<Variable> leftFound = new ArrayList<>();
      List<Variable> rightFound = new ArrayList<>();

      findDefinedVars(cond.getTest(), leftFound, checker);
      findDefinedVars(cond.getLhs(), leftFound, checker);

      findDefinedVars(cond.getRhs(), rightFound, checker);

      VarAnalysis.mergeFound(leftFound, rightFound, foundVars);
    } else if (qTerm instanceof IsTrue)
      ;
    else if (qTerm instanceof TrueCondition)
      ;
    else if (qTerm instanceof FalseCondition)
      ;
    else if (qTerm instanceof Implies) {
    } else
      assert false : "cannot deal with condition";
  }

  static void mergeFound(List<Variable> leftFound, List<Variable> rightFound, List<Variable> found)
  {
    for (Variable var : leftFound) {
      if (rightFound.contains(var)) {
        if (!found.contains(var))
          found.add(var);
      }
    }
  }

  public static List<Variable> findDefinedVars(ICondition cond, VarChecker checker)
  {
    List<Variable> vars = new ArrayList<>();
    findDefinedVars(cond, vars, checker);
    return vars;
  }

  static boolean allDefined(IContentExpression exp, List<Variable> definedVars)
  {
    if (exp instanceof Variable) {
      if (definedVars.contains(exp))
        return true;
      else {
        for (Variable fr : definedVars)
          if (fr.equals(exp))
            return true;
        return false;
      }
    } else if (exp instanceof FieldAccess)
      return allDefined(((FieldAccess) exp).getRecord(), definedVars);
    else if (exp instanceof Scalar)
      return true;
    else if (exp instanceof ConstructorTerm) {
      ConstructorTerm posCon = (ConstructorTerm) exp;
      for (int ix = 0; ix < posCon.arity(); ix++)
        if (!allDefined(posCon.getArg(ix), definedVars))
          return false;
      return true;
    } else
      return false;
  }

  static boolean isDefined(Variable var, List<Variable> definedVars)
  {
    return definedVars.contains(var);
  }

  static void findDefinedVars(IContentPattern ptn, List<Variable> definedVars)
  {
    if (ptn instanceof Variable) {
      Variable var = (Variable) ptn;
      if (!definedVars.contains(var))
        definedVars.add(var);
    } else if (ptn instanceof RecordPtn) {
      RecordPtn aggCon = (RecordPtn) ptn;
      for (Entry<String, IContentPattern> entry : aggCon.getElements().entrySet()) {
        findDefinedVars(entry.getValue(), definedVars);
      }
    } else if (ptn instanceof ConstructorPtn) {
      ConstructorPtn posCon = (ConstructorPtn) ptn;
      for (int ix = 0; ix < posCon.arity(); ix++)
        findDefinedVars(posCon.getArg(ix), definedVars);
    } else if (ptn instanceof TuplePtn) {
      TuplePtn tpl = (TuplePtn) ptn;
      for (int ix = 0; ix < tpl.arity(); ix++)
        findDefinedVars(tpl.getArg(ix), definedVars);
    }
  }

  public interface VarChecker
  {
    boolean isThisOk(Variable var);
  }

}
