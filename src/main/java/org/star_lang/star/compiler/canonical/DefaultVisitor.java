package org.star_lang.star.compiler.canonical;

import java.util.Map.Entry;
import java.util.Stack;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ContractEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImplementationEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImportEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.TypeAliasEntry;
import org.star_lang.star.compiler.util.Pair;

/**
 * Prototype visitor that visits everything.
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
public class DefaultVisitor implements CanonicalVisitor
{
  final private Stack<String> exclusions = new Stack<>();
  final private boolean exclude;
  final Excluder excluder;

  protected DefaultVisitor(boolean exclude)
  {
    this.exclude = exclude;
    if (exclude)
      excluder = new Excluder();
    else
      excluder = null;
  }

  @Override
  public void visitRecord(RecordTerm record)
  {
    record.getFun().accept(this);
    for (IContentExpression exp : record.getArguments().values()) {
      exp.accept(this);
    }
  }

  @Override
  public void visitRecordPtn(RecordPtn record)
  {
    record.getFun().accept(this);

    for (IContentPattern ptn : record.getElements().values()) {
      ptn.accept(this);
    }
  }

  @Override
  public void visitApplication(Application appl)
  {
    appl.getFunction().accept(this);
    appl.getArgs().accept(this);
  }

  @Override
  public void visitRecordSubstitute(RecordSubstitute update)
  {
    update.getRoute().accept(this);
    update.getReplace().accept(this);
  }

  @Override
  public void visitConjunction(Conjunction conjunction)
  {
    conjunction.getLhs().accept(this);
    conjunction.getRhs().accept(this);
  }

  @Override
  public void visitDisjunction(Disjunction disjunction)
  {
    disjunction.getLhs().accept(this);
    disjunction.getRhs().accept(this);
  }

  @Override
  public void visitMemo(MemoExp memo)
  {
    memo.getMemo().accept(this);
  }

  @Override
  public void visitMethodVariable(MethodVariable mtd)
  {
  }

  @Override
  public void visitNegation(Negation negation)
  {
    negation.getNegated().accept(this);
  }

  @Override
  public void visitOtherwise(Otherwise otherwise)
  {
    otherwise.getLhs().accept(this);
    otherwise.getRhs().accept(this);
  }

  @Override
  public void visitOverloaded(Overloaded over)
  {
    over.getInner().accept(this);
  }

  @Override
  public void visitOverloadedVariable(OverloadedVariable over)
  {

  }

  @Override
  public void visitOverloadedFieldAccess(OverloadedFieldAccess over)
  {
    over.getRecord().accept(this);
  }

  @Override
  public void visitPatternAbstraction(PatternAbstraction pattern)
  {
    if (exclude) {
      for (IContentPattern arg : pattern.getArgs())
        if (arg != null)
          arg.accept(excluder);
      pattern.getMatch().accept(excluder);
    } else {
      for (IContentPattern arg : pattern.getArgs())
        if (arg != null)
          arg.accept(this);
    }
    pattern.getResult().accept(this);
  }

  @Override
  public void visitPatternApplication(PatternApplication apply)
  {
    apply.getAbstraction().accept(this);
    apply.getArg().accept(this);
  }

  @Override
  public void visitPredication(Search predication)
  {
    predication.getPtn().accept(this);
    predication.getSource().accept(this);
  }

  @Override
  public void visitFieldAccess(FieldAccess dot)
  {
    dot.getRecord().accept(this);
  }

  @Override
  public void visitSequence(Sequence sequence)
  {
    for (IContentAction action : sequence.getActions()) {
      action.accept(this);
    }
  }

  @Override
  public void visitSyncAction(SyncAction sync)
  {
    sync.getSel().accept(this);
    for (Entry<ICondition, IContentAction> entry : sync.getBody().entrySet()) {
      entry.getKey().accept(this);
      entry.getValue().accept(this);
    }
  }

  @Override
  public void visitTuple(ConstructorTerm tuple)
  {
    for (IContentExpression expr : tuple.getElements()) {
      expr.accept(this);
    }
  }

  @Override
  public void visitTuplePtn(ConstructorPtn tuplePtn)
  {
    for (IContentPattern ptn : tuplePtn.getElements()) {
      ptn.accept(this);
    }
    if (!CompilerUtils.isTuplePattern(tuplePtn))
      tuplePtn.getFun().accept(this);
  }

  @Override
  public void visitVariable(Variable variable)
  {
  }

  @Override
  public void visitImplies(Implies implies)
  {
    implies.getGenerate().accept(this);
    implies.getTest().accept(this);
  }

  @Override
  public void visitWherePattern(WherePattern wherePattern)
  {
    wherePattern.getPtn().accept(this);
    wherePattern.getCond().accept(this);
  }

  @Override
  public void visitConditionCondition(ConditionCondition conditionCondition)
  {
    conditionCondition.getTest().accept(this);
    conditionCondition.getLhs().accept(this);
    conditionCondition.getRhs().accept(this);
  }

  @Override
  public void visitFalseCondition(FalseCondition falseCondition)
  {
  }

  @Override
  public void visitResolved(Resolved f)
  {
    f.getOver().accept(this);
    for (IContentExpression arg : f.getDicts())
      if (arg != null)
        arg.accept(this);
  }

  @Override
  public void visitScalar(Scalar scalar)
  {
  }

  @Override
  public void visitScalarPtn(ScalarPtn scalar)
  {
  }

  @Override
  public void visitMatches(Matches matches)
  {
    matches.getExp().accept(this);
    matches.getPtn().accept(this);
  }

  @Override
  public void visitMatching(MatchingPattern matching)
  {
    matching.getVar().accept(this);
    matching.getPtn().accept(this);
  }

  @Override
  public void visitReference(Shriek reference)
  {
    reference.getReference().accept(this);
  }

  @Override
  public void visitTrueCondition(TrueCondition trueCondition)
  {
  }

  @Override
  public void visitAssertAction(AssertAction act)
  {
    act.getAssertion().accept(this);
  }

  @Override
  public void visitAssignment(Assignment act)
  {
    act.getLValue().accept(this);
    act.getValue().accept(this);
  }

  @Override
  public void visitCaseAction(CaseAction exp)
  {
    exp.getSelector().accept(this);
    exp.getDeflt().accept(this);
    for (Pair<IContentPattern, IContentAction> entry : exp.getCases()) {
      entry.getKey().accept(this);
      entry.getValue().accept(this);
    }
  }

  @Override
  public void visitCaseExpression(CaseExpression exp)
  {
    exp.getSelector().accept(this);
    exp.getDeflt().accept(this);
    for (Pair<IContentPattern, IContentExpression> entry : exp.getCases()) {
      entry.getKey().accept(this);
      entry.getValue().accept(this);
    }
  }

  @Override
  public void visitCastExpression(CastExpression exp)
  {
    exp.getInner().accept(this);
  }

  @Override
  public void visitCastPtn(CastPtn ptn)
  {
    ptn.getInner().accept(this);
  }

  @Override
  public void visitConditionalAction(ConditionalAction act)
  {
    act.getCond().accept(this);
    act.getThPart().accept(this);
    act.getElPart().accept(this);
  }

  @Override
  public void visitConditionalExp(ConditionalExp act)
  {
    act.getCnd().accept(this);
    act.getThExp().accept(this);
    act.getElExp().accept(this);
  }

  @Override
  public void visitContentCondition(ContentCondition cond)
  {
    cond.getCondition().accept(this);
  }

  @Override
  public void visitContractEntry(ContractEntry entry)
  {
  }

  @Override
  public void visitContractImplementation(ImplementationEntry entry)
  {

  }

  @Override
  public void visitExceptionHandler(ExceptionHandler except)
  {
    except.getBody().accept(this);
    except.getHandler().accept(this);
  }

  @Override
  public void visitFunctionLiteral(FunctionLiteral f)
  {
    if (exclude) {
      for (IContentPattern arg : f.getArgs())
        if (arg != null)
          arg.accept(excluder);
    } else {
      for (IContentPattern arg : f.getArgs())
        if (arg != null)
          arg.accept(this);
    }
    f.getBody().accept(this);
  }

  @Override
  public void visitIgnored(Ignore ignore)
  {
    ignore.getIgnored().accept(this);
  }

  @Override
  public void visitYield(Yield act)
  {
    act.getYielded().accept(this);
  }

  @Override
  public void visitImportEntry(ImportEntry entry)
  {
  }

  @Override
  public void visitIsTrue(IsTrue i)
  {
    i.getExp().accept(this);
  }

  @Override
  public void visitJavaEntry(JavaEntry entry)
  {
  }

  @Override
  public void visitLetAction(LetAction let)
  {
    if (exclude) {
      int mark = mark();

      for (IStatement entry : let.getEnvironment()) {
        if (entry instanceof VarEntry) {
          for (Variable v : ((VarEntry) entry).getDefined())
            exclude(v.getName());
        }
      }
      for (IStatement stmt : let.getEnvironment())
        stmt.accept(this);

      let.getBoundAction().accept(this);
      reset(mark);
    } else {
      for (IStatement stmt : let.getEnvironment()) {

        stmt.accept(this);
      }
      let.getBoundAction().accept(this);
    }
  }

  @Override
  public void visitRaiseAction(RaiseAction exp)
  {
    exp.getRaised().accept(this);
  }

  protected int mark()
  {
    return exclusions.size();
  }

  protected void reset(int mark)
  {
    assert mark <= exclusions.size();
    exclusions.setSize(mark);
  }

  public void exclude(String name)
  {
    exclusions.push(name);
  }

  public boolean isNotExcluded(String name)
  {
    return !exclusions.contains(name);
  }

  @Override
  public void visitLetTerm(LetTerm let)
  {
    if (exclude) {
      int mark = mark();

      for (IStatement entry : let.getEnvironment()) {
        if (entry instanceof VarEntry) {
          for (Variable v : ((VarEntry) entry).getDefined())
            exclude(v.getName());
        }
      }
      for (IStatement stmt : let.getEnvironment())
        stmt.accept(this);

      let.getBoundExp().accept(this);
      reset(mark);
    } else {
      for (IStatement stmt : let.getEnvironment())
        stmt.accept(this);

      let.getBoundExp().accept(this);
    }
  }

  @Override
  public void visitListSearch(ListSearch ptn)
  {
    ptn.getPtn().accept(this);
    ptn.getIx().accept(this);
    ptn.getSource().accept(this);
  }

  @Override
  public void visitForLoopAction(ForLoopAction loop)
  {
    loop.getControl().accept(this);
    loop.getBody().accept(this);
  }

  @Override
  public void visitWhileAction(WhileAction act)
  {
    act.getControl().accept(this);
    act.getBody().accept(this);
  }

  @Override
  public void visitNullAction(NullAction act)
  {
  }

  @Override
  public void visitNullExp(NullExp nil)
  {
  }

  @Override
  public void visitProcedureCallAction(ProcedureCallAction call)
  {
    call.getProc().accept(this);
    for (IContentExpression arg : call.getArgs())
      arg.accept(this);
  }

  @Override
  public void visitRaiseExpression(RaiseExpression exp)
  {
    exp.getRaise().accept(this);
  }

  @Override
  public void visitRegexpPtn(RegExpPattern ptn)
  {
    for (IContentPattern group : ptn.getGroups())
      group.accept(this);
  }

  @Override
  public void visitTypeAliasEntry(TypeAliasEntry entry)
  {
  }

  @Override
  public void visitTypeEntry(TypeDefinition entry)
  {
  }

  @Override
  public void visitTypeWitness(TypeWitness witness)
  {
  }

  @Override
  public void visitValisAction(ValisAction act)
  {
    act.getValue().accept(this);
  }

  @Override
  public void visitValofExp(ValofExp val)
  {
    val.getAction().accept(this);
  }

  @Override
  public void visitVarDeclaration(VarDeclaration var)
  {
    var.getValue().accept(this);
  }

  @Override
  public void visitVarEntry(VarEntry entry)
  {
    entry.getVarPattern().accept(this);
    entry.getValue().accept(this);
  }

  @Override
  public void visitOpenStatement(OpenStatement open)
  {
    open.getRecord().accept(this);
  }

  @Override
  public void visitVoidExp(VoidExp exp)
  {
  }

  private static class Excluder extends DefaultVisitor
  {

    Excluder()
    {
      super(false);
    }

    @Override
    public void visitVariable(Variable variable)
    {
      exclude(variable.getName());
    }

    @Override
    public void visitMethodVariable(MethodVariable mtd)
    {
      exclude(mtd.getName());
    }

    @Override
    public void visitOverloadedVariable(OverloadedVariable over)
    {
      exclude(over.getName());
    }

    @Override
    public void visitWherePattern(WherePattern where)
    {
      where.getPtn().accept(this);
    }
  }
}
