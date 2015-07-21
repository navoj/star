package org.star_lang.star.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.star_lang.star.compiler.canonical.Canonical;
import org.star_lang.star.compiler.canonical.CanonicalVisitor;
import org.star_lang.star.compiler.canonical.CaseAction;
import org.star_lang.star.compiler.canonical.CaseExpression;
import org.star_lang.star.compiler.canonical.DefaultVisitor;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.MemoExp;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.PatternAbstraction;
import org.star_lang.star.compiler.canonical.PatternApplication;
import org.star_lang.star.compiler.canonical.ProcedureCallAction;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.WherePattern;
import org.star_lang.star.compiler.type.BuiltinInfo;
import org.star_lang.star.compiler.type.DictInfo;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.Wrapper;

/**
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
    for (IContentExpression arg : call.getArgs())
      arg.accept(this);
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

  public static List<Variable> findAllVars(Canonical term, Dictionary cxt)
  {
    List<Variable> freeVars = new ArrayList<>();
    FreeVariables visitor = new FreeVariables(freeVars, null) {

      @Override
      public void visitMethodVariable(MethodVariable mtd)
      {
        visitVariable(mtd);
      }

      @Override
      public void visitOverloadedVariable(OverloadedVariable var)
      {
        visitVariable(var);
      }

      @Override
      public void visitVariable(Variable var)
      {
        if (!cxt.isDefinedVar(var.getName()))
          if (!freeVars.contains(var) && excluder.isNotExcluded(var.getName()))
            freeVars.add(var);
      }
    };

    term.accept(visitor);

    return freeVars;
  }

  public static List<Variable> freeTermVars(ICondition term, final Dictionary cxt)
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
