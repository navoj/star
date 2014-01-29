package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.star_lang.star.compiler.canonical.CanonicalVisitor;
import org.star_lang.star.compiler.canonical.ConditionCondition;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.ConstructorPtn;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.DefaultVisitor;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.FalseCondition;
import org.star_lang.star.compiler.canonical.FieldAccess;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.Implies;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.ListSearch;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.Negation;
import org.star_lang.star.compiler.canonical.Otherwise;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.RecordPtn;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.TrueCondition;
import org.star_lang.star.compiler.canonical.Variable;

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

      List<Variable> leftFound = new ArrayList<Variable>();
      List<Variable> rightFound = new ArrayList<Variable>();

      findDefinedVars(disj.getLhs(), leftFound, checker);
      findDefinedVars(disj.getRhs(), rightFound, checker);

      VarAnalysis.mergeFound(leftFound, rightFound, foundVars);
    } else if (qTerm instanceof Negation) {
    } else if (qTerm instanceof Otherwise) {
      Otherwise other = (Otherwise) qTerm;
      List<Variable> leftFound = new ArrayList<Variable>();
      List<Variable> rightFound = new ArrayList<Variable>();

      findDefinedVars(other.getLhs(), leftFound, checker);
      findDefinedVars(other.getRhs(), rightFound, checker);

      VarAnalysis.mergeFound(leftFound, rightFound, foundVars);
    } else if (qTerm instanceof ConditionCondition) {
      ConditionCondition cond = (ConditionCondition) qTerm;

      List<Variable> leftFound = new ArrayList<Variable>();
      List<Variable> rightFound = new ArrayList<Variable>();

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
    List<Variable> vars = new ArrayList<Variable>();
    findDefinedVars(cond, vars, checker);
    return vars;
  }

  static boolean allDefined(IContentExpression exp, List<Variable> definedVars)
  {
    if (exp instanceof Variable) {
      if (definedVars.contains((Variable) exp))
        return true;
      else {
        for (Variable fr : definedVars)
          if (fr.equals((Variable) exp))
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
    }
  }

  public static interface VarChecker
  {
    boolean isThisOk(Variable var);
  }

}
