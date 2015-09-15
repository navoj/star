/**
 *
 */
package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.type.Dict;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.util.Pair;

import java.util.*;
import java.util.Map.Entry;


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

public abstract class ExpressionTransformer {
  protected final Dictionary cxt;

  public ExpressionTransformer(Dictionary cxt) {
    if (cxt == null)
      this.cxt = Dict.baseDict();
    else
      this.cxt = cxt;

    // Expressions
    install(new SubstituteTrans());
    install(new ApplicationTrans());
    install(new CaseExpressionTrans());
    install(new CastExpTrans());
    install(new ConditionalExpTrans());
    install(new ContentConditionTrans());
    install(new FieldAccessTrans());
    install(new FunctionLiteralTrans());
    install(new LetTermTrans());
    install(new MethodVariableTrans());
    install(new MemoTransform());
    install(new NullTransform());
    install(new OverloadedTrans());
    install(new OverloadedVariableTrans());
    install(new OverloadedFieldAccessTrans());
    install(new PatternAbstractionTrans());
    install(new RecordTrans());
    install(new RaiseExpTrans());
    install(new ReferenceTrans());
    install(new ResolvedTrans());
    install(new ScalarTrans());
    install(new TupleTrans());
    install(new ValofExpTrans());
    install(new VariableTrans());
    install(new VoidExpTrans());

    // Patterns
    install(new AggregatePtnTrans());
    install(new CastPtnTrans());
    install(new LiteralPtnTrans());
    install(new MatchingPtnTrans());
    install(new OverloadVariablePtnTrans());
    install(new PatternAbstractionApplicationTrans());
    install(new RegExpPtnTrans());
    install(new ScalarPtnTrans());
    install(new TuplePtnTrans());
    install(new VariablePtnTrans());
    install(new WherePtnTrans());

    // Conditions
    install(new ConditionConditionTrans());
    install(new ConjunctionTrans());
    install(new DisjunctionTrans());
    install(new FalseConditionTrans());
    install(new ImpliesTrans());
    install(new IsTrueTrans());
    install(new ListSearchTrans());
    install(new MatchesTrans());
    install(new NegationTrans());
    install(new OtherwiseTrans());
    install(new PredicationTrans());
    install(new TrueConditionTrans());

    // Actions
    install(new AssertTransform());
    install(new Assignmentransform());
    install(new CaseActionTransform());
    install(new ConditionalActionTransform());
    install(new ExceptionHandlerTransform());
    install(new IgnoreTransform());
    install(new LetActionTransform());
    install(new LoopActionTransform());
    install(new NullActionTransform());
    install(new ProcedureCallTransform());
    install(new RaiseActionTransform());
    install(new SequenceTransform());
    install(new ValisTransform());
    install(new VarDeclarationTransform());

    // Statements
    install(new ContractTrans());
    install(new ContractImplementationTrans());
    install(new ImportTrans());
    install(new JavaTrans());
    install(new VarEntryTrans());
    install(new TypeDefinitionTrans());
    install(new TypeAliasTrans());
    install(new TypeWitnessTrans());
  }

  public IContentAction[] transformActions(IContentAction actions[]) {
    IContentAction[] nActions = new IContentAction[actions.length];
    for (int ix = 0; ix < actions.length; ix++)
      nActions[ix] = transform(actions[ix]);
    return nActions;
  }

  public IContentExpression[] transformExpressions(IContentExpression[] args) {
    boolean modified = false;
    IContentExpression nArgs[] = new IContentExpression[args.length];
    for (int ix = 0; ix < args.length; ix++) {
      nArgs[ix] = transform(args[ix]);
      modified |= nArgs[ix] != args[ix];
    }
    if (modified)
      return nArgs;
    else
      return args;
  }

  public Variable[] transformVariable(Variable[] args) {
    Variable nArgs[] = new Variable[args.length];
    for (int ix = 0; ix < args.length; ix++)
      nArgs[ix] = (Variable) transform((IContentExpression) args[ix]);
    return nArgs;
  }

  private class RecordTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return RecordTerm.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      RecordTerm record = (RecordTerm) exp;
      SortedMap<String, IContentExpression> args = new TreeMap<>();
      IContentExpression fun = transform(record.getFun());
      boolean clean = fun == record.getFun();
      for (Entry<String, IContentExpression> entry : record.getArguments().entrySet()) {
        IContentExpression nArg = transform(entry.getValue());
        clean &= nArg == entry.getValue();
        args.put(entry.getKey(), nArg);
      }
      if (clean)
        return record;
      else
        return new RecordTerm(record.getLoc(), record.getType(), fun, args, record.getTypes());
    }
  }

  private class SubstituteTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return RecordSubstitute.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      RecordSubstitute update = (RecordSubstitute) exp;

      return new RecordSubstitute(update.getLoc(), update.getType(), transform(update.getRoute()), transform(update
              .getReplace()));
    }
  }

  private class ApplicationTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return Application.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      Application app = (Application) exp;
      return new Application(exp.getLoc(), exp.getType(), transform(app.getFunction()), transform(app.getArgs()));
    }
  }

  private class CaseExpressionTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return CaseExpression.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      CaseExpression caseExp = (CaseExpression) exp;
      List<Pair<IContentPattern, IContentExpression>> newCases = new ArrayList<>();

      for (Entry<IContentPattern, IContentExpression> entry : caseExp.getCases()) {
        newCases.add(Pair.pair(transform(entry.getKey()), transform(entry.getValue())));
      }

      return new CaseExpression(caseExp.getLoc(), caseExp.getType(), transform(caseExp.getSelector()), newCases,
              transform(caseExp.getDeflt()));
    }
  }

  private class ConditionalExpTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return ConditionalExp.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      ConditionalExp cond = (ConditionalExp) exp;
      ICondition nCnd = transform(cond.getCnd());
      IContentExpression nTh = transform(cond.getThExp());
      IContentExpression nEl = transform(cond.getElExp());
      return new ConditionalExp(exp.getLoc(), exp.getType(), nCnd, nTh, nEl);
    }
  }

  private class ContentConditionTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return ContentCondition.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      ContentCondition cond = (ContentCondition) exp;
      return new ContentCondition(exp.getLoc(), transform(cond.getCondition()));
    }
  }

  private class FunctionLiteralTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return FunctionLiteral.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      FunctionLiteral fun = (FunctionLiteral) exp;

      Variable[] funFree = fun.getFreeVars();
      Variable tVars[] = new Variable[funFree.length];
      for (int ix = 0; ix < tVars.length; ix++)
        tVars[ix] = (Variable) transform((IContentExpression) funFree[ix]);
      return new FunctionLiteral(exp.getLoc(), fun.getName(), exp.getType(), transformPatterns(fun.getArgs()),
              transform(fun.getBody()), tVars);
    }
  }

  private class MemoTransform implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return MemoExp.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      MemoExp memo = (MemoExp) exp;
      IContentExpression[] memoFree = memo.getFreeVars();
      IContentExpression freeVars[] = new IContentExpression[memoFree.length];
      for (int ix = 0; ix < freeVars.length; ix++) {
        freeVars[ix] = transform(memoFree[ix]);
      }
      return new MemoExp(exp.getLoc(), transform(memo.getMemo()), freeVars);
    }
  }

  private class NullTransform implements TransformExpression {

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      return exp;
    }

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return NullExp.class;
    }
  }

  private class LetTermTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return LetTerm.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      LetTerm let = (LetTerm) exp;
      List<IStatement> newEnv = new ArrayList<>();
      for (IStatement entry : let.getEnvironment())
        newEnv.add(transform(entry));

      return new LetTerm(exp.getLoc(), transform(let.getBoundExp()), newEnv);
    }
  }

  private class MethodVariableTrans implements TransformExpression {
    @Override
    public Class<? extends IContentExpression> transformClass() {
      return MethodVariable.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      return exp;
    }
  }

  private class PatternAbstractionTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return PatternAbstraction.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      PatternAbstraction ptn = (PatternAbstraction) exp;
      Variable[] funFree = ptn.getFreeVars();
      Variable tVars[] = new Variable[funFree.length];
      for (int ix = 0; ix < tVars.length; ix++)
        tVars[ix] = (Variable) transform((IContentExpression) funFree[ix]);
      return new PatternAbstraction(exp.getLoc(), ptn.getName(), exp.getType(), transform(ptn.getMatch()),
              transform(ptn.getResult()), tVars);
    }
  }

  private class RaiseExpTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return RaiseExpression.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      RaiseExpression raise = (RaiseExpression) exp;

      return new RaiseExpression(exp.getLoc(), exp.getType(), transform(raise.getRaise()));
    }
  }

  private class ReferenceTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return Shriek.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      Shriek ref = (Shriek) exp;
      return new Shriek(ref.getLoc(), transform(ref.getReference()));
    }
  }

  private class ResolvedTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return Resolved.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      Resolved res = (Resolved) exp;
      IContentExpression transOp = transform(res.getOver());
      IContentExpression[] transArgs = transformExpressions(res.getDicts());
      if (transOp == res.getOver() && transArgs == res.getDicts())
        return exp;
      else
        return new Resolved(exp.getLoc(), exp.getType(), res.getDictType(), transOp, transArgs);
    }
  }

  private class ScalarTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return Scalar.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      return exp;
    }
  }

  private class ScalarPtnTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return ScalarPtn.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      return ptn;
    }
  }

  private class TupleTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return ConstructorTerm.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      ConstructorTerm tpl = (ConstructorTerm) exp;
      List<IContentExpression> els = new ArrayList<>();
      List<IContentExpression> tplEls = tpl.getElements();
      boolean dirty = false;
      for (IContentExpression el : tplEls) {
        IContentExpression transformed = transform(el);
        els.add(transformed);
        dirty |= transformed != el;
      }
      if (dirty)
        return new ConstructorTerm(exp.getLoc(), tpl.getLabel(), exp.getType(), els);
      else
        return exp;
    }
  }

  private class CastExpTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return CastExpression.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      CastExpression caste = (CastExpression) exp;

      return new CastExpression(exp.getLoc(), exp.getType(), transform(caste.getInner()));
    }
  }

  private class ValofExpTrans implements TransformExpression {

    @Override
    public Class<? extends IContentExpression> transformClass() {
      return ValofExp.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      ValofExp val = (ValofExp) exp;
      return new ValofExp(exp.getLoc(), exp.getType(), transform(val.getAction()));
    }
  }

  private class VariableTrans implements TransformExpression {
    @Override
    public Class<? extends IContentExpression> transformClass() {
      return Variable.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      return exp;
    }
  }

  private class OverloadedTrans implements TransformExpression {
    @Override
    public Class<? extends IContentExpression> transformClass() {
      return Overloaded.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      Overloaded var = (Overloaded) exp;
      IContentExpression inner = transform(var.getInner());
      if (inner != var.getInner())
        return new Overloaded(exp.getLoc(), exp.getType(), var.getDictType(), inner);
      else
        return exp;
    }
  }

  private class OverloadedVariableTrans implements TransformExpression {
    @Override
    public Class<? extends IContentExpression> transformClass() {
      return OverloadedVariable.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      return exp;
    }
  }

  private class FieldAccessTrans implements TransformExpression {
    @Override
    public Class<? extends IContentExpression> transformClass() {
      return FieldAccess.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      FieldAccess dot = (FieldAccess) exp;
      IContentExpression transRecord = transform(dot.getRecord());
      if (transRecord != dot.getRecord())
        return new FieldAccess(exp.getLoc(), exp.getType(), transRecord, dot.getField());
      else
        return exp;
    }
  }

  private class OverloadedFieldAccessTrans implements TransformExpression {
    @Override
    public Class<? extends IContentExpression> transformClass() {
      return OverloadedFieldAccess.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      OverloadedFieldAccess dot = (OverloadedFieldAccess) exp;
      IContentExpression transRecord = transform(dot.getRecord());
      if (transRecord != dot.getRecord())
        return new OverloadedFieldAccess(exp.getLoc(), exp.getType(), dot.getDictType(), transRecord, dot.getField());
      else
        return exp;
    }
  }

  private class VoidExpTrans implements TransformExpression {
    @Override
    public Class<? extends IContentExpression> transformClass() {
      return VoidExp.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp) {
      return exp;
    }
  }

  public IContentExpression transform(IContentExpression exp) {
    if (exp == null)
      return null;

    TransformExpression transformer = expTransformers.get(exp.getClass());
    assert transformer != null : "missing case for expression transformer";

    return transformer.transformExp(exp);
  }

  private class ConditionConditionTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return ConditionCondition.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      ConditionCondition cnd = (ConditionCondition) cond;
      return new ConditionCondition(cond.getLoc(), transform(cnd.getTest()), transform(cnd.getLhs()), transform(cnd
              .getRhs()));
    }
  }

  private class ConjunctionTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return Conjunction.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      Conjunction con = (Conjunction) cond;
      return new Conjunction(cond.getLoc(), transform(con.getLhs()), transform(con.getRhs()));
    }
  }

  private class DisjunctionTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return Disjunction.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      Disjunction dis = (Disjunction) cond;
      return new Disjunction(cond.getLoc(), transform(dis.getLhs()), transform(dis.getRhs()));
    }
  }

  private class FalseConditionTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return FalseCondition.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      return cond;
    }
  }

  private class IsTrueTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return IsTrue.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      return new IsTrue(cond.getLoc(), transform(((IsTrue) cond).getExp()));
    }
  }

  private class ListSearchTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return ListSearch.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      ListSearch pr = (ListSearch) cond;
      return new ListSearch(cond.getLoc(), transform(pr.getPtn()), transform(pr.getIx()), transform(pr.getSource()));
    }
  }

  private class MatchesTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return Matches.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      Matches m = (Matches) cond;
      return new Matches(cond.getLoc(), transform(m.getExp()), transform(m.getPtn()));
    }
  }

  private class NegationTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return Negation.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      return new Negation(cond.getLoc(), transform(((Negation) cond).getNegated()));
    }
  }

  private class OtherwiseTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return Otherwise.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      Otherwise oth = (Otherwise) cond;
      return new Otherwise(cond.getLoc(), transform(oth.getLhs()), transform(oth.getRhs()));
    }
  }

  private class PredicationTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return Search.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      Search pr = (Search) cond;
      return new Search(cond.getLoc(), transform(pr.getPtn()), transform(pr.getSource()));
    }
  }

  private class TrueConditionTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return TrueCondition.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      return cond;
    }
  }

  private class ImpliesTrans implements TransformCondition {
    @Override
    public Class<? extends ICondition> transformClass() {
      return Implies.class;
    }

    @Override
    public ICondition transformCond(ICondition cond) {
      Implies when = (Implies) cond;
      return new Implies(cond.getLoc(), transform(when.getTest()), transform(when.getGenerate()));
    }
  }

  public final ICondition transform(ICondition cond) {
    if (cond == null)
      return null;

    TransformCondition trans = condTransformers.get(cond.getClass());

    assert trans != null : "could not transform " + cond;
    return trans.transformCond(cond);
  }

  private class AssertTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      return new AssertAction(act.getLoc(), transform(((AssertAction) act).getAssertion()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return AssertAction.class;
    }
  }

  private class Assignmentransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      Assignment ass = (Assignment) act;
      return new Assignment(act.getLoc(), transform(ass.getLValue()), transform(ass.getValue()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return Assignment.class;
    }
  }

  private class CaseActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      CaseAction cA = (CaseAction) act;
      List<Pair<IContentPattern, IContentAction>> cases = new ArrayList<>();
      for (Pair<IContentPattern, IContentAction> entry : cA.getCases()) {
        cases.add(Pair.pair(transform(entry.getKey()), transform(entry.getValue())));
      }
      return new CaseAction(act.getLoc(), transform(cA.getSelector()), cases, transform(cA.getDeflt()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return CaseAction.class;
    }
  }

  private class ConditionalActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      ConditionalAction cnd = (ConditionalAction) act;
      return new ConditionalAction(act.getLoc(), transform(cnd.getCond()), transform(cnd.getThPart()), transform(cnd
              .getElPart()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return ConditionalAction.class;
    }
  }

  private class ExceptionHandlerTransform implements TransformAction {

    @Override
    public IContentAction transformAction(IContentAction act) {
      ExceptionHandler except = (ExceptionHandler) act;

      return new ExceptionHandler(act.getLoc(), transform(except.getBody()), transform(except.getHandler()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return ExceptionHandler.class;
    }
  }

  private class IgnoreTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      Ignore ignore = (Ignore) act;
      IContentExpression trans = transform(ignore.getIgnored());
      if (trans != ignore.getIgnored())
        return new Ignore(ignore.getLoc(), trans);
      else
        return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return Ignore.class;
    }
  }

  private class LetActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      LetAction let = (LetAction) act;
      List<IStatement> newEnv = new ArrayList<>();
      for (IStatement entry : let.getEnvironment())
        newEnv.add(transform(entry));

      return new LetAction(act.getLoc(), newEnv, transform(let.getBoundAction()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return LetAction.class;
    }
  }

  private class LoopActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      WhileAction loop = (WhileAction) act;
      return new WhileAction(act.getLoc(), transform(loop.getControl()), transform(loop.getBody()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return WhileAction.class;
    }
  }

  private class NullActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return NullAction.class;
    }
  }

  private class ProcedureCallTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      ProcedureCallAction call = (ProcedureCallAction) act;

      return new ProcedureCallAction(call.getLoc(), transform(call.getProc()), transform(call.getArgs()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return ProcedureCallAction.class;
    }
  }

  private class RaiseActionTransform implements TransformAction {

    @Override
    public IContentAction transformAction(IContentAction act) {
      RaiseAction raise = (RaiseAction) act;
      return new RaiseAction(raise.getLoc(), transform(raise.getRaised()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return RaiseAction.class;
    }

  }

  private class SequenceTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      Sequence seq = (Sequence) act;
      List<IContentAction> lst = new ArrayList<>();
      for (IContentAction acts : seq.getActions())
        lst.add(transform(acts));
      return new Sequence(act.getLoc(), act.getType(), lst);
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return Sequence.class;
    }
  }

  private class ValisTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      ValisAction valis = (ValisAction) act;
      IContentExpression txAction = transform(valis.getValue());
      return new ValisAction(act.getLoc(), txAction);
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return ValisAction.class;
    }
  }

  private class VarDeclarationTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      VarDeclaration decl = (VarDeclaration) act;
      return new VarDeclaration(act.getLoc(), decl.getPattern(), decl.isReadOnly(), transform(decl.getValue()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return VarDeclaration.class;
    }
  }

  public final IContentAction transform(IContentAction act) {
    TransformAction trans = actTransformers.get(act.getClass());

    assert trans != null : " could not transform action " + act;
    return trans.transformAction(act);
  }

  private class AggregatePtnTrans implements TransformPattern {

    @Override
    public Class<? extends IContentPattern> transformClass() {
      return RecordPtn.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      RecordPtn rec = (RecordPtn) ptn;
      Map<String, IContentPattern> nEls = new TreeMap<>();
      for (Entry<String, IContentPattern> entry : rec.getElements().entrySet()) {
        nEls.put(entry.getKey(), transform(entry.getValue()));
      }
      return new RecordPtn(ptn.getLoc(), ptn.getType(), transform(rec.getFun()), nEls, rec.getIndex());
    }
  }

  private class CastPtnTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return CastPtn.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      CastPtn cast = (CastPtn) ptn;
      return new CastPtn(ptn.getLoc(), ptn.getType(), transform(cast.getInner()));
    }
  }

  private class LiteralPtnTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return ScalarPtn.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      return ptn;
    }
  }

  private class MatchingPtnTrans implements TransformPattern {

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      MatchingPattern matching = (MatchingPattern) ptn;
      return new MatchingPattern(ptn.getLoc(), matching.getVar(), transform(matching.getPtn()));
    }

    @Override
    public Class<? extends IContentPattern> transformClass() {
      return MatchingPattern.class;
    }

  }

  private class OverloadVariablePtnTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return OverloadedVariable.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      return ptn;
    }
  }

  private class PatternAbstractionApplicationTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return PatternApplication.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      PatternApplication apply = (PatternApplication) ptn;
      return new PatternApplication(apply.getLoc(), apply.getType(), transform(apply.getAbstraction()), transform(apply
              .getArg()));
    }
  }

  private class RegExpPtnTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return RegExpPattern.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      RegExpPattern reg = (RegExpPattern) ptn;
      return new RegExpPattern(ptn.getLoc(), reg.getRegexpPtn(), reg.getNfa(), transformPatterns(reg.getGroups()));
    }
  }

  private class TuplePtnTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return ConstructorPtn.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      ConstructorPtn con = (ConstructorPtn) ptn;
      List<IContentPattern> nEls = new ArrayList<>();
      for (IContentPattern el : con.getElements())
        nEls.add(transform(el));
      return new ConstructorPtn(ptn.getLoc(), con.getLabel(), ptn.getType(), nEls);
    }
  }

  private class VariablePtnTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return Variable.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      return ptn;
    }
  }

  private class WherePtnTrans implements TransformPattern {
    @Override
    public Class<? extends IContentPattern> transformClass() {
      return WherePattern.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern ptn) {
      WherePattern where = (WherePattern) ptn;
      return new WherePattern(ptn.getLoc(), transform(where.getPtn()), transform(where.getCond()));
    }
  }

  public IContentPattern transform(IContentPattern ptn) {
    if (ptn == null)
      return null;

    TransformPattern trans = ptnTransformers.get(ptn.getClass());
    assert trans != null : "no pattern translator for " + ptn;

    return trans.transformPtn(ptn);
  }

  public IContentPattern[] transformPatterns(IContentPattern[] args) {
    IContentPattern nArgs[] = new IContentPattern[args.length];
    for (int ix = 0; ix < args.length; ix++)
      nArgs[ix] = transform(args[ix]);
    return nArgs;
  }

  private class ContractTrans implements TransformStatement {
    @Override
    public Class<? extends IStatement> transformClass() {
      return ContractEntry.class;
    }

    @Override
    public IStatement transformStmt(IStatement stmt) {
      return stmt;
    }
  }

  private class ContractImplementationTrans implements TransformStatement {
    @Override
    public Class<? extends IStatement> transformClass() {
      return ImplementationEntry.class;
    }

    @Override
    public IStatement transformStmt(IStatement stmt) {
      return stmt;
    }
  }

  private class ImportTrans implements TransformStatement {
    @Override
    public Class<? extends IStatement> transformClass() {
      return ImportEntry.class;
    }

    @Override
    public IStatement transformStmt(IStatement stmt) {
      ImportEntry imp = (ImportEntry) stmt;
      return new ImportEntry(imp.getLoc(), imp.getPkgName(), imp.getPkgType(), imp.getUri(), imp.getVisibility());
    }
  }

  private class JavaTrans implements TransformStatement {
    @Override
    public Class<? extends IStatement> transformClass() {
      return JavaEntry.class;
    }

    @Override
    public IStatement transformStmt(IStatement stmt) {
      return stmt;
    }
  }

  private class VarEntryTrans implements TransformStatement {
    @Override
    public Class<? extends IStatement> transformClass() {
      return VarEntry.class;
    }

    @Override
    public IStatement transformStmt(IStatement stmt) {
      VarEntry var = (VarEntry) stmt;

      final IContentExpression transformedValue = transform(var.getValue());

      return new VarEntry(var.getDefined(), var.getLoc(), transform(var.getVarPattern()), transformedValue, var
              .isReadOnly(), stmt.getVisibility());
    }
  }

  private class TypeDefinitionTrans implements TransformStatement {
    @Override
    public Class<? extends IStatement> transformClass() {
      return TypeDefinition.class;
    }

    @Override
    public IStatement transformStmt(IStatement stmt) {
      return stmt;
    }
  }

  private class TypeAliasTrans implements TransformStatement {
    @Override
    public Class<? extends IStatement> transformClass() {
      return TypeAliasEntry.class;
    }

    @Override
    public IStatement transformStmt(IStatement stmt) {
      return stmt;
    }
  }

  private class TypeWitnessTrans implements TransformStatement {

    @Override
    public IStatement transformStmt(IStatement stmt) {
      return stmt;
    }

    @Override
    public Class<? extends IStatement> transformClass() {
      return TypeWitness.class;
    }

  }

  public IStatement transform(IStatement stmt) {
    TransformStatement trans = stmtTransformers.get(stmt.getClass());

    assert trans != null : "cannot transform statement " + stmt;
    return trans.transformStmt(stmt);
  }

  protected interface TransformExpression {
    IContentExpression transformExp(IContentExpression exp);

    Class<? extends IContentExpression> transformClass();
  }

  protected interface TransformPattern {
    IContentPattern transformPtn(IContentPattern ptn);

    Class<? extends IContentPattern> transformClass();
  }

  protected interface TransformCondition {
    ICondition transformCond(ICondition cond);

    Class<? extends ICondition> transformClass();
  }

  protected interface TransformAction {
    IContentAction transformAction(IContentAction act);

    Class<? extends IContentAction> transformClass();
  }

  protected interface TransformStatement {
    IStatement transformStmt(IStatement stmt);

    Class<? extends IStatement> transformClass();
  }

  private final Map<Class<? extends IContentExpression>, TransformExpression> expTransformers = new HashMap<>();
  private final Map<Class<? extends IContentPattern>, TransformPattern> ptnTransformers = new HashMap<>();
  private final Map<Class<? extends IContentAction>, TransformAction> actTransformers = new HashMap<>();
  private final Map<Class<? extends ICondition>, TransformCondition> condTransformers = new HashMap<>();
  private final Map<Class<? extends IStatement>, TransformStatement> stmtTransformers = new HashMap<>();

  protected void install(TransformExpression tr) {
    expTransformers.put(tr.transformClass(), tr);
  }

  protected void install(TransformPattern tr) {
    ptnTransformers.put(tr.transformClass(), tr);
  }

  protected void install(TransformAction tr) {
    actTransformers.put(tr.transformClass(), tr);
  }

  protected void install(TransformCondition tr) {
    condTransformers.put(tr.transformClass(), tr);
  }

  protected void install(TransformStatement tr) {
    stmtTransformers.put(tr.transformClass(), tr);
  }
}
