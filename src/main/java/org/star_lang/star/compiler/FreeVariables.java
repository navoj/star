package org.star_lang.star.compiler;

import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.type.BuiltinInfo;
import org.star_lang.star.compiler.type.DictInfo;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.Wrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

public class FreeVariables extends DefaultVisitor
{
  final Collection<Variable> freeVars;
  final Dictionary cxt;
  final Excluder excluder = new Excluder();

  FreeVariables(Collection<Variable> freeVars, Dictionary cxt)
  {
    super(true);
    this.freeVars = freeVars;
    this.cxt = cxt;
  }

  @Override
  public void visitFunctionLiteral(FunctionLiteral f)
  {
    for (Variable var : f.getFreeVars())
      var.accept(this);
  }

  @Override
  public void visitMemo(MemoExp memo)
  {
    for (IContentExpression var : memo.getFreeVars())
      var.accept(this);
  }

  @Override
  public void visitOverloadedVariable(OverloadedVariable var)
  {
    if (cxt.isDefinedVar(var.getName())) {
      if (!freeVars.contains(var) && excluder.isNotExcluded(var.getName())) {
        DictInfo info = cxt.getVar(var.getName());
        if (!(info instanceof BuiltinInfo))
          freeVars.add(var);
      }
    }
  }

  @Override
  public void visitPatternAbstraction(PatternAbstraction pattern)
  {
    for (Variable var : pattern.getFreeVars())
      var.accept(this);
  }

  @Override
  public void visitPatternApplication(PatternApplication apply)
  {
    apply.getAbstraction().accept(this);
    apply.getArg().accept(this);
  }

  @Override
  public void visitVariable(Variable var)
  {
    if (cxt.isDefinedVar(var.getName())) {
      if (!freeVars.contains(var) && excluder.isNotExcluded(var.getName())) {
        DictInfo info = cxt.getVar(var.getName());
        if (!(info instanceof BuiltinInfo))
          freeVars.add(var);
      }
    }
  }

  @Override
  public void visitMatches(Matches matches)
  {
    matches.getExp().accept(this);
    matches.getPtn().accept(this);
  }

  @Override
  public void visitPredication(Search pred)
  {
    pred.getSource().accept(this);
    pred.getPtn().accept(this);
  }

  @Override
  public void visitCaseAction(CaseAction caseAction)
  {
    for (Pair<IContentPattern, IContentAction> entry : caseAction.getCases()) {
      int mark = mark();
      entry.getKey().accept(this);
      entry.getValue().accept(this);
      reset(mark);
    }
    caseAction.getSelector().accept(this);
    caseAction.getDeflt().accept(this);
  }

  @Override
  public void visitCaseExpression(CaseExpression caseExp)
  {
    for (Pair<IContentPattern, IContentExpression> entry : caseExp.getCases()) {
      int mark = mark();
      entry.getKey().accept(this);
      entry.getValue().accept(this);
      reset(mark);
    }
    caseExp.getSelector().accept(this);
    caseExp.getDeflt().accept(this);
  }

  @Override
  public void visitVarDeclaration(VarDeclaration var)
  {
    var.getPattern().accept(excluder);
    var.getValue().accept(this);
  }

  @Override
  public void visitVarEntry(VarEntry entry)
  {
    entry.getVarPattern().accept(excluder);
    entry.getValue().accept(this);
  }

  @Override
  public void visitProcedureCallAction(ProcedureCallAction call)
  {
    call.getProc().accept(this);
    call.getArgs().accept(this);
  }

  private class Excluder extends DefaultVisitor
  {
    Excluder()
    {
      super(false);
    }

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
    public void visitVariable(Variable variable)
    {
      exclude(variable.getName());
    }

    @Override
    public void visitWherePattern(WherePattern where)
    {
      where.getPtn().accept(this);
    }
  }

  public static void addFreeVars(Canonical term, Dictionary cxt, Collection<Variable> free)
  {
    FreeVariables visitor = new FreeVariables(free, cxt);
    term.accept(visitor);
  }

  public static Variable[] findFreeVars(Canonical term, final Dictionary cxt)
  {
    final List<Variable> freeVars = new ArrayList<>();

    FreeVariables visitor = new FreeVariables(freeVars, cxt);

    term.accept(visitor);

    return freeVars.toArray(new Variable[freeVars.size()]);
  }

  public static Variable[] freeFreeVars(Variable[] exclude, Canonical term, Dictionary cxt)
  {
    final List<Variable> freeVars = new ArrayList<>();

    FreeVariables visitor = new FreeVariables(freeVars, cxt);

    for (Variable arg : exclude)
      arg.accept(visitor.excluder);

    term.accept(visitor);

    return freeVars.toArray(new Variable[freeVars.size()]);
  }

  public static Variable[] freeFreeVars(IContentPattern[] exclude, Canonical term, Dictionary cxt)
  {
    final List<Variable> freeVars = new ArrayList<>();

    FreeVariables visitor = new FreeVariables(freeVars, cxt);

    for (IContentPattern arg : exclude)
      arg.accept(visitor.excluder);

    term.accept(visitor);

    return freeVars.toArray(new Variable[freeVars.size()]);
  }

  public static List<Variable> freeVars(Canonical term, Dictionary cxt)
  {
    final List<Variable> freeVars = new ArrayList<>();

    FreeVariables visitor = new FreeVariables(freeVars, cxt);

    term.accept(visitor);

    return freeVars;
  }

  public static boolean varPresent(final IContentExpression tgt, IContentPattern terms[])
  {
    final Wrapper<Boolean> found = new Wrapper<>(false);

    CanonicalVisitor finder = new DefaultVisitor(true) {

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
        if (var.equals(tgt))
          found.set(true);
      }
    };

    for (IContentPattern term : terms)
      term.accept(finder);

    return found.get();
  }

  public static boolean isFreeIn(final Variable tgt, IContentExpression exp)
  {
    final Wrapper<Boolean> found = new Wrapper<>(false);

    CanonicalVisitor finder = new DefaultVisitor(true) {
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
        if (var.equals(tgt))
          found.set(true);
      }
    };

    exp.accept(finder);

    return found.get();
  }
}
