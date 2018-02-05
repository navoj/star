package org.star_lang.star.compiler.transform;

import org.star_lang.star.StarCompiler;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.FreeVariables;
import org.star_lang.star.compiler.canonical.AbortAction;
import org.star_lang.star.compiler.canonical.AbortExpression;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.AssertAction;
import org.star_lang.star.compiler.canonical.Assignment;
import org.star_lang.star.compiler.canonical.CaseAction;
import org.star_lang.star.compiler.canonical.CaseExpression;
import org.star_lang.star.compiler.canonical.CastExpression;
import org.star_lang.star.compiler.canonical.CastPtn;
import org.star_lang.star.compiler.canonical.ConditionCondition;
import org.star_lang.star.compiler.canonical.ConditionalAction;
import org.star_lang.star.compiler.canonical.ConditionalExp;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.ConstructorPtn;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.ContentCondition;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.ExceptionHandler;
import org.star_lang.star.compiler.canonical.FalseCondition;
import org.star_lang.star.compiler.canonical.FieldAccess;
import org.star_lang.star.compiler.canonical.ForLoopAction;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.Ignore;
import org.star_lang.star.compiler.canonical.Implies;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.ListSearch;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.MatchingPattern;
import org.star_lang.star.compiler.canonical.MemoExp;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.Negation;
import org.star_lang.star.compiler.canonical.NullAction;
import org.star_lang.star.compiler.canonical.NullExp;
import org.star_lang.star.compiler.canonical.Otherwise;
import org.star_lang.star.compiler.canonical.Overloaded;
import org.star_lang.star.compiler.canonical.OverloadedFieldAccess;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.PatternAbstraction;
import org.star_lang.star.compiler.canonical.PatternApplication;
import org.star_lang.star.compiler.canonical.ProcedureCallAction;
import org.star_lang.star.compiler.canonical.RecordPtn;
import org.star_lang.star.compiler.canonical.RecordSubstitute;
import org.star_lang.star.compiler.canonical.RecordTerm;
import org.star_lang.star.compiler.canonical.RegExpPattern;
import org.star_lang.star.compiler.canonical.Resolved;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.ScalarPtn;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.Sequence;
import org.star_lang.star.compiler.canonical.Shriek;
import org.star_lang.star.compiler.canonical.TransformAction;
import org.star_lang.star.compiler.canonical.TransformCondition;
import org.star_lang.star.compiler.canonical.TransformExpression;
import org.star_lang.star.compiler.canonical.TransformPattern;
import org.star_lang.star.compiler.canonical.TrueCondition;
import org.star_lang.star.compiler.canonical.TuplePtn;
import org.star_lang.star.compiler.canonical.TupleTerm;
import org.star_lang.star.compiler.canonical.ValisAction;
import org.star_lang.star.compiler.canonical.ValofExp;
import org.star_lang.star.compiler.canonical.ValuePtn;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.VoidExp;
import org.star_lang.star.compiler.canonical.WherePattern;
import org.star_lang.star.compiler.canonical.WhileAction;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeChecker;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.Triple;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.operators.general.runtime.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import java.util.SortedMap;
import java.util.TreeMap;

public class Computations implements
    TransformAction<IContentExpression, IContentExpression, IContentPattern, ICondition, IStatement, ComputationContext>,
    TransformExpression<IContentExpression, IContentExpression, IContentPattern, ICondition, IStatement, ComputationContext>,
    TransformPattern<IContentExpression, IContentExpression, IContentPattern, ICondition, IStatement, ComputationContext>,
    TransformCondition<IContentExpression, IContentExpression, IContentPattern, ICondition, IStatement, ComputationContext> {
  public static final String ENCAPSULATE = "_encapsulate";
  public static final String COMBINE = "_combine";
  public static final String ABORT = "_abort";
  public static final String PERFORM = "_perform";
  public static final String HANDLE = "_handle";
  public static final String DELAY = "_delay";

  public static final String COMPUTATION = "computation"; // the monad contract
  public static final String EXECUTION = "execution"; // the execution contract
  public static final String INJECTION = "injection"; // the monad injection
                                                      // contract

  public static final String INJECT = "_inject";

  public static final IType unitType = StandardTypes.unitType;

  public static IContentExpression monasticate(IContentAction act, IType mType, IType eType, ErrorReport errors,
      Dictionary dict, Dictionary outer) {
    ComputationContext cxt = new ComputationContext(mType, eType, dict, outer, errors);
    Computations trans = new Computations();
    IContentExpression res = delay(act.getLoc(), collectBindings(act.transform(trans, cxt), cxt), cxt);
    if (StarCompiler.TRACE_MONASTICATION)
      System.out.println("Monasticate:\n" + act + "\nas\n" + res);

    return res;
  }

  @Override
  public IContentExpression transformAssertAction(AssertAction act, ComputationContext context) {
    Location loc = act.getLoc();
    IContentExpression testFun = exp2fun(loc, act.getAssertion().transform(this, context), context);
    IContentExpression tester = new Variable(loc, Assert.type(), Assert.name);
    IContentExpression cond = encapsulate(loc, Application.apply(loc, unitType, tester, testFun), context);
    Variable anon = Variable.anonymous(loc, unitType);
    return bind(loc, anon, cond, context);
  }

  @Override
  public IContentExpression transformAssignment(Assignment ass, ComputationContext context) {
    Location loc = ass.getLoc();

    IContentExpression value = ass.getValue().transform(this, context);
    IContentExpression ptn = ass.getLValue().transform(this, context);
    if (value != ass.getValue() || ptn != ass.getLValue())
      ass = new Assignment(loc, ptn, value);
    IContentExpression voidExp = valofValis(loc, new VoidExp(loc), ass);
    Variable anon = Variable.anonymous(loc, unitType);
    IContentExpression valis = encapsulate(loc, voidExp, context);
    return bind(loc, anon, valis, context);
  }

  @Override
  public IContentExpression transformCaseAction(CaseAction exp, ComputationContext context) {
    Location loc = exp.getLoc();

    IContentExpression cont = context.getExp();

    if (cont != null && !(cont instanceof Variable)) {
      Variable cVar = new Variable(loc, cont.getType(), GenSym.genSym("__cont"));
      IStatement cDef = new VarEntry(loc, cVar, cont, AccessMode.readOnly, Visibility.priVate);
      ComputationContext caseCxt = context.fork(cVar);
      IContentExpression txCase = transformCaseAction(exp, caseCxt);
      return new LetTerm(loc, txCase, cDef);
    } else {
      ComputationContext selCxt = context.fork();
      IContentExpression sel = exp.getSelector().transform(this, selCxt);

      List<Pair<IContentPattern, IContentExpression>> cases = new ArrayList<>();
      for (Pair<IContentPattern, IContentAction> entry : exp.getCases()) {
        ComputationContext cseCxt = context.fork(context.getExp());
        IContentPattern txPtn = entry.left().transformPattern(this, cseCxt);
        IContentExpression branch = entry.right().transform(this, cseCxt);

        cases.add(Pair.pair(txPtn, collectBindings(branch, cseCxt)));
      }
      ComputationContext defCxt = context.fork(context.getExp());
      IContentExpression deflt = collectBindings(exp.getDeflt().transform(this, defCxt), defCxt);

      return collectBindings(new CaseExpression(loc, deflt.getType(), sel, cases, deflt), selCxt);
    }
  }

  @Override
  public IContentExpression transformConditionalAction(ConditionalAction act, ComputationContext context) {
    Location loc = act.getLoc();

    IContentExpression cont = context.getExp();

    ComputationContext condCxt = context.fork();
    ICondition test = act.getCond().transform(this, condCxt);

    ComputationContext thCxt = context.fork(cont);
    IContentExpression thn = collectBindings(act.getThPart().transform(this, thCxt), thCxt);

    ComputationContext elCxt = context.fork(cont);
    IContentExpression els = collectBindings(act.getElPart().transform(this, elCxt), elCxt);

    return collectBindings(new ConditionalExp(loc, thn.getType(), test, thn, els), condCxt);
  }

  @Override
  public IContentExpression transformExceptionHandler(ExceptionHandler except, ComputationContext context) {
    /**
     * Exception handler actions are converted to calls to _handle ...
     */
    Location loc = except.getLoc();
    ComputationContext bodyCxt = context.fork(context.getExp());
    IContentExpression body = collectBindings(except.getBody().transform(this, bodyCxt), bodyCxt);

    ComputationContext handlerCxt = context.fork(context.getExp());
    IContentExpression handleExp = collectBindings(except.getHandler().transform(this, handlerCxt), handlerCxt);
    Variable exVar = new Variable(loc, except.getAbortType(), StandardNames.EXCEPTION);
    IContentExpression handlerFun = exp2fun(loc, exVar, handleExp, handlerCxt);

    return handle(loc, body, handlerFun, context.getmType(), body.getType(), context.getDict(), context.getOuter(),
        context.getErrors());
  }

  @Override
  public IContentExpression transformForLoop(ForLoopAction loop, ComputationContext cxt) {
    /**
     * A for loop such as:
     *
     * <pre>
     * var C := 0;
     * for X in Src do{
     *   if X<0 then
     *     valis C+5
     *   else
     *     C := C+X
     * }
     * valis C+1
     * </pre>
     *
     * is transformed to:
     *
     * <pre>
     * _delay(memo valof{
     *   var C := 0
     *   valis let{
     *     fun check(NoneFound) is _encapsulate(C+1)
     *      |  check(NoMore(XX) is _encapsulate(XX)
     *      |  check(AbortIter(E) is _abort(X)
     *   } in check(_iterate(Src,let{
     *     fun bd(X,St) is valof{
     *       if X<0 then
     *         valis NoMore(C+5)
     *       else
     *         C := C+X
     *       valis St
     *     } in bd,NoneFound)
     * })
     * </pre>
     */

    IContentAction body = loop.getBody();
    Location loc = loop.getLoc();

    IType mType = cxt.getmType();
    IType iterMtype = TypeUtils.typeCon(StandardNames.ITERSTATE, 1);
    Dictionary dict = cxt.getDict();
    Dictionary outer = cxt.getOuter();
    ErrorReport errors = cxt.getErrors();

    List<Variable> freeVars = FreeVariables.freeVars(body, dict);

    Variable[] free = freeVars.toArray(new Variable[freeVars.size()]);

    IType resType = loop.getType();
    IType taskType = TypeUtils.typeExp(mType, resType);

    IType loopResultType = TypeUtils.typeExp(iterMtype, resType);

    // IContentExpression cont = cxt.getExp();

    // ComputationContext bdyCxt = cxt.fork(cont);
    // IContentExpression bodyExp =
    // collectBindings(loop.getBody().transform(this, bdyCxt), bdyCxt);

    // return collectBindings(new ConditionalExp(loc, thn.getType(), test, thn,
    // els), condCxt);

    IContentExpression loopExp = QueryPlanner.transformForLoop(loc, loop.getDefined(), loop.getControl(), body, resType,
        resType, dict, outer, errors);

    // build the check function, which we will bind by hand...

    // If no valis, then the continuation is the continuation in the context.

    List<Triple<IContentPattern[], ICondition, IContentExpression>> eqns = new ArrayList<>();

    Triple<IContentPattern[], ICondition, IContentExpression> eq1 = Triple.create(
        new IContentPattern[] { CompilerUtils.noneFoundPtn(loc, resType) }, CompilerUtils.truth,
        cxt.getExp() != null ? cxt.getExp() : encapsulate(loc, new VoidExp(loc, resType), cxt));
    eqns.add(eq1);

    // If there was a valis, we take the result as our computation
    Variable resVar = new Variable(loc, resType, GenSym.genSym("__res"));
    Triple<IContentPattern[], ICondition, IContentExpression> eq2 = Triple.create(
        new IContentPattern[] { CompilerUtils.noMorePtn(loc, resVar) }, CompilerUtils.truth,
        encapsulate(loc, resVar, cxt));
    eqns.add(eq2);

    Variable exVar = new Variable(loc, StandardTypes.exceptionType, GenSym.genSym("__ex"));
    Triple<IContentPattern[], ICondition, IContentExpression> eq3 = Triple.create(
        new IContentPattern[] { CompilerUtils.abortIterPtn(loc, resType, exVar) }, CompilerUtils.truth,
        abort(loc, exVar, cxt));
    eqns.add(eq3);

    IType checkType = Freshen.generalizeType(TypeUtils.functionType(loopResultType, taskType));
    String checkName = GenSym.genSym("__check");
    IContentExpression check = MatchCompiler.generateFunction(eqns, null, checkType, free, checkName, loc, dict, outer,
        errors);
    Variable checkVar = new Variable(loc, checkType, checkName);

    // The bind function ...
    check = new LetTerm(loc, checkVar, new VarEntry(loc, checkVar, check, AccessMode.readOnly, Visibility.priVate));

    return Application.apply(loc, taskType, check, loopExp);
  }

  @Override
  public IContentExpression transformIgnored(Ignore ignore, ComputationContext context) {
    Location loc = ignore.getLoc();
    IContentExpression ignored = ignore.getIgnored();
    IType getmType = context.getmType();
    Variable anon = Variable.anonymous(loc, ignored.getType());

    if (isPerform(ignored, getmType)) {
      IContentExpression task = performedTask(ignored, getmType);

      if (isVoidCombine(task, context.getmType()))
        task = combinedTask(task);

      return bind(loc, anon, encapsulate(loc, context.declareTempVar(task), context), context);
    } else {
      return bind(loc, anon, encapsulate(loc, ignored.transform(this, context), context), context);
    }
  }

  @Override
  public IContentExpression transformLetAction(LetAction let, ComputationContext context) {
    Location loc = let.getLoc();
    IContentExpression bound = let.getBoundAction().transform(this, context);
    Variable anon = Variable.anonymous(loc, bound.getType());
    return bind(loc, anon, encapsulate(loc, new LetTerm(loc, bound, let.getEnvironment()), context), context);
  }

  @Override
  public IContentExpression transformRaiseAction(AbortAction raise, ComputationContext context) {
    IContentExpression raised = raise.getABort();
    return abort(raise.getLoc(), raised, context);
  }

  @Override
  public IContentExpression transformWhileLoop(WhileAction loop, ComputationContext context) {
    Location loc = loop.getLoc();
    IContentAction body = loop.getBody();
    List<Variable> free = FreeVariables.freeVars(body, context.getDict());

    /**
     * A while loop is converted to a local function:
     *
     * <pre>
     * task{
     *   var C:=0;
     *   while C<10 do {
     *     X is valof T();   -- where T is a task valued fun 
     *     if X<3 then
     *       C := C+X;
     *     else
     *       valis C 
     *   }
     *   valis C
     * }
     * </pre>
     *
     * becomes
     *
     * <pre>
     * _combine(
     *   _encapsulate(0),
     *   function(Cinit) is valof{
     *     var C := init;
     *     valis _combine(
     *       let{
     *         loop() where C<10 is 
     *           _combine(T(),
     *             function(X) is ( X<3 ?
     *                            _combine( _encapsulate(valof{ C := X+C; valis () }), loop)
     *                          | _encapsulate(())));
     *         loop() default is _encapsulate(());
     *        } in loop()),
     *     function(_) is _encapsulate(C)))
     * </pre>
     */

    IType loopTaskType = TypeUtils.typeExp(context.getmType(), loop.getType());
    IType loopFunType = TypeUtils.functionType(unitType, loopTaskType);
    Variable loopVar = new Variable(loc, loopFunType, GenSym.genSym("loop"));
    Application loopCall = new Application(loc, loopTaskType, loopVar, new TupleTerm(loc, new VoidExp(loc)));
    ComputationContext lpCxt = context.fork(loopCall);
    Variable anon = Variable.anonymous(loc, unitType);

    IContentExpression loopBody = collectBindings(body.transform(this, lpCxt), lpCxt);

    ComputationContext gdCxt = lpCxt.fork();
    ICondition loopGuard = loop.getControl().transform(this, gdCxt);
    Variable[] freeVars = free.toArray(new Variable[free.size()]);

    List<Triple<IContentPattern[], ICondition, IContentExpression>> eqns = new ArrayList<>();
    IContentPattern[] loopArgs = new IContentPattern[] { anon };

    IContentExpression lpBody = CompilerUtils.isTrivial(loopGuard) ? loopBody
        : new ConditionalExp(loc, loopTaskType, loopGuard, loopBody,
            bind(loc, anon, encapsulate(loc, new VoidExp(loc), context), context));
    lpBody = collectBindings(lpBody, gdCxt);
    Triple<IContentPattern[], ICondition, IContentExpression> eqn = Triple.create(loopArgs, CompilerUtils.truth,
        lpBody);
    FunctionLiteral loopFun = MatchCompiler.generateFunction(eqns, eqn, loopFunType, freeVars, loopVar.getName(), loc,
        context.getDict(), context.getOuter(), context.getErrors());
    return new LetTerm(loc, loopCall, new VarEntry(loc, loopVar, loopFun, AccessMode.readOnly, Visibility.priVate));
  }

  @Override
  public IContentExpression transformNullAction(NullAction act, ComputationContext context) {
    if (context.getExp() != null)
      return context.getExp();
    else
      return encapsulate(act.getLoc(), new VoidExp(act.getLoc()), context);
  }

  @Override
  public IContentExpression transformProcedureCallAction(ProcedureCallAction call, ComputationContext context) {
    IContentExpression op = call.getProc();
    IContentExpression trOp = op.transform(this, context);
    IContentExpression trArgs = call.getArgs().transform(this, context);

    Location loc = call.getLoc();
    return valofValis(loc, new VoidExp(loc), new ProcedureCallAction(loc, trOp, trArgs));
  }

  @Override
  public IContentExpression transformSequence(Sequence sequence, ComputationContext context) {
    List<IContentAction> acts = sequence.getActions();
    if (acts.isEmpty())
      return transformNullAction(new NullAction(sequence.getLoc(), StandardTypes.unitType), context);
    else if (acts.size() == 1)
      return acts.get(0).transform(this, context);
    else {
      int ix = acts.size() - 1;
      ComputationContext lastCxt = context.fork(context.getExp());
      IContentExpression last = collectBindings(acts.get(ix).transform(this, lastCxt), lastCxt);

      ComputationContext seqCxt = context.fork(last);

      while (--ix >= 0) {
        last = acts.get(ix).transform(this, seqCxt);

        if (seqCxt.anyTemps())
          last = collectBindings(last, seqCxt);

        seqCxt = seqCxt.fork(last);
      }
      return last;
    }
  }

  private static IContentExpression collectBindings(IContentExpression exp, ComputationContext cxt) {
    Dictionary dict = cxt.getDict();
    Dictionary outer = cxt.getOuter();
    ErrorReport errors = cxt.getErrors();
    IType mType = cxt.getmType();

    assert TypeUtils.isType(exp.getType(), mType, 1, dict);

    for (Pair<Variable, IContentExpression> entry : cxt.getTempVars()) {
      exp = bind(entry.left().getLoc(), exp, entry.left(), entry.right(), mType, dict, outer, errors);
    }
    return exp;
  }

  @Override
  public IContentExpression transformValisAction(ValisAction act, ComputationContext context) {
    IContentExpression inner = act.getValue().transform(this, context);
    Location loc = inner.getLoc();

    if (isPerform(inner, context.getmType())) {
      Variable ex = new Variable(loc, context.geteType(), GenSym.genSym("__exception"));
      IContentExpression abort = abort(loc, ex, context);
      IContentExpression handler = exp2fun(loc, ex, abort, context);

      IContentExpression performed = performedTask(inner, context.getmType());
      return handle(loc, performed, handler, context.getmType(), performed.getType(), context.getDict(),
          context.getOuter(), context.getErrors());
    } else if (isInjection(inner))
      return inject(loc, context.getmType(), injectedValue(inner), context.getDict(), context.getErrors());
    else
      return encapsulate(loc, inner, context);
  }

  @Override
  public IContentExpression transformVarDeclaration(VarDeclaration var, ComputationContext context) {
    Location loc = var.getLoc();
    IContentPattern ptn = var.getPattern();
    IContentExpression value = var.getValue();
    IType mType = context.getmType();

    if (isPerform(value, mType)) {
      IContentExpression task = performedTask(value, mType);

      if (isVoidCombine(task, mType))
        task = combinedTask(task);

      return bind(loc, ptn, encapsulate(loc, context.declareTempVar(task), context), context);
    } else {
      return bind(loc, ptn, encapsulate(loc, value.transform(this, context), context), context);
    }
  }

  @Override
  public IContentExpression transformApplication(Application appl, ComputationContext context) {
    if (isPerform(appl, context.getmType())) {
      IContentExpression task = appl.getArg(0);

      if (isVoidCombine(task, context.getmType()))
        task = combinedTask(task);

      return context.declareTempVar(task);
    } else {
      IContentExpression op = appl.getFunction();
      IContentExpression trOp = op.transform(this, context);
      IContentExpression trArgs = appl.getArgs().transform(this, context);
      if (trOp != op || trArgs != appl.getArgs())
        return new Application(appl.getLoc(), appl.getType(), trOp, trArgs);
      else
        return appl;
    }
  }

  @Override
  public IContentExpression transformRecord(RecordTerm record, ComputationContext context) {
    SortedMap<String, IContentExpression> args = new TreeMap<>();
    IContentExpression fun = record.getFun().transform(this, context);
    boolean clean = fun == record.getFun();
    for (Entry<String, IContentExpression> entry : record.getArguments().entrySet()) {
      IContentExpression nArg = entry.getValue().transform(this, context);
      clean &= nArg == entry.getValue();
      args.put(entry.getKey(), nArg);
    }
    if (clean)
      return record;
    else
      return new RecordTerm(record.getLoc(), record.getType(), fun, args, record.getTypes());
  }

  @Override
  public IContentExpression transformRecordSubstitute(RecordSubstitute update, ComputationContext context) {
    IContentExpression rec = update.getRoute().transform(this, context);
    IContentExpression sub = update.getReplace().transform(this, context);
    if (rec != update.getReplace() || sub != update.getReplace())
      return new RecordSubstitute(update.getLoc(), update.getType(), rec, sub);
    else
      return update;
  }

  @Override
  public IContentExpression transformCaseExpression(CaseExpression exp, ComputationContext context) {
    IContentExpression sel = exp.getSelector().transform(this, context);
    boolean clean = sel == exp.getSelector();
    IContentExpression def = exp.getDeflt().transform(this, context);
    List<Pair<IContentPattern, IContentExpression>> cases = new ArrayList<>();
    for (Pair<IContentPattern, IContentExpression> pr : exp.getCases()) {
      IContentPattern ptn = pr.left().transformPattern(this, context);
      clean &= ptn == pr.left();
      IContentExpression rhs = pr.right().transform(this, context);
      clean &= rhs == pr.right();
    }
    if (clean)
      return exp;
    else
      return new CaseExpression(exp.getLoc(), def.getType(), sel, cases, def);
  }

  @Override
  public IContentExpression transformCastExpression(CastExpression exp, ComputationContext context) {
    IContentExpression inner = exp.getInner().transform(this, context);
    if (inner == exp.getInner())
      return exp;
    else
      return new CastExpression(exp.getLoc(), exp.getType(), inner);
  }

  @Override
  public IContentExpression transformConditionalExp(ConditionalExp exp, ComputationContext context) {
    ICondition test = exp.getCnd().transform(this, context);

    if (test != exp.getCnd())
      return new ConditionalExp(exp.getLoc(), exp.getType(), test, exp.getThExp(), exp.getElExp());
    else
      return exp;
  }

  @Override
  public IContentExpression transformContentCondition(ContentCondition cond, ComputationContext context) {
    ICondition test = cond.getCondition().transform(this, context);
    if (test != cond.getCondition())
      return new ContentCondition(cond.getLoc(), test);
    else
      return cond;
  }

  @Override
  public IContentExpression transformMemo(MemoExp memo, ComputationContext context) {
    return memo;
  }

  @Override
  public IContentExpression transformNullExp(NullExp nil, ComputationContext context) {
    return nil;
  }

  @Override
  public IContentExpression transformFunctionLiteral(FunctionLiteral f, ComputationContext context) {
    return f;
  }

  @Override
  public IContentExpression transformLetTerm(LetTerm let, ComputationContext context) {
    Location loc = let.getLoc();
    IContentExpression bound = let.getBoundExp();
    ComputationContext bndCxt = context.fork(context.getExp());

    IContentExpression trBound = bound.transform(this, bndCxt);

    if (trBound == let.getBoundExp())
      return let;
    else {
      final List<IStatement> env = let.getEnvironment();
      for (Pair<Variable, IContentExpression> e : bndCxt.getTempVars()) {
        final IContentExpression performed = e.right();
        env.add(new VarEntry(loc, e.left(),
            perform(loc, bndCxt.getmType(), performed, context.getDict(), context.getErrors()), AccessMode.readOnly,
            Visibility.priVate));
      }
      return new LetTerm(loc, trBound, env);
    }
  }

  @Override
  public IContentExpression transformOverloaded(Overloaded over, ComputationContext context) {
    return over;
  }

  @Override
  public IContentExpression transformOverloadedFieldAccess(OverloadedFieldAccess over, ComputationContext context) {
    return over;
  }

  @Override
  public IContentExpression transformOverloadVariable(OverloadedVariable var, ComputationContext context) {
    return var;
  }

  @Override
  public IContentExpression transformPatternAbstraction(PatternAbstraction ptn, ComputationContext context) {
    return ptn;
  }

  @Override
  public IContentExpression transformFieldAccess(FieldAccess dot, ComputationContext context) {
    IContentExpression rec = dot.getRecord().transform(this, context);
    if (rec == dot.getRecord())
      return dot;
    else
      return new FieldAccess(dot.getLoc(), dot.getType(), rec, dot.getField());
  }

  @Override
  public IContentExpression transformRaiseExpression(AbortExpression exp, ComputationContext context) {
    IContentExpression raised = exp.getAbort();
    return abort(exp.getLoc(), raised, context);
  }

  @Override
  public IContentExpression transformReference(Shriek reference, ComputationContext context) {
    IContentExpression ref = reference.getReference();

    IContentExpression reffed = ref.transform(this, context);
    if (reffed == ref)
      return reference;
    else
      return new Shriek(reference.getLoc(), reffed);
  }

  @Override
  public IContentExpression transformResolved(Resolved res, ComputationContext context) {
    return res;
  }

  @Override
  public IContentExpression transformScalar(Scalar scalar, ComputationContext context) {
    return scalar;
  }

  @Override
  public IContentExpression transformConstructor(ConstructorTerm con, ComputationContext context) {
    List<IContentExpression> els = new ArrayList<>();
    boolean clean = true;
    for (IContentExpression el : con.getElements()) {
      IContentExpression txEl = el.transform(this, context);
      els.add(txEl);
      clean &= txEl == el;
    }
    if (clean)
      return con;
    else
      return new ConstructorTerm(con.getLoc(), con.getLabel(), con.getType(), els);
  }

  @Override
  public IContentExpression transformTuple(TupleTerm con, ComputationContext context) {
    List<IContentExpression> els = new ArrayList<>();
    boolean clean = true;
    for (IContentExpression el : con.getElements()) {
      IContentExpression txEl = el.transform(this, context);
      els.add(txEl);
      clean &= txEl == el;
    }
    if (clean)
      return con;
    else
      return new TupleTerm(con.getLoc(), con.getType(), els);
  }

  @Override
  public IContentExpression transformValofExp(ValofExp val, ComputationContext context) {
    return val;
  }

  @Override
  public IContentExpression transformVariable(Variable variable, ComputationContext context) {
    return variable;
  }

  @Override
  public IContentExpression transformVoidExp(VoidExp exp, ComputationContext context) {
    return exp;
  }

  @Override
  public ICondition transformConditionCondition(ConditionCondition cond, ComputationContext context) {
    ICondition tst = cond.getTest().transform(this, context);
    ICondition then = cond.getLhs().transform(this, context);
    ICondition els = cond.getRhs().transform(this, context);
    if (tst != cond.getTest() || then != cond.getLhs() || els != cond.getRhs())
      return new ConditionCondition(cond.getLoc(), tst, then, els);
    else
      return cond;
  }

  @Override
  public ICondition transformConjunction(Conjunction conj, ComputationContext context) {
    ICondition lhs = conj.getLhs().transform(this, context);
    ICondition rhs = conj.getRhs().transform(this, context);
    if (lhs != conj.getLhs() || rhs != conj.getRhs())
      return new Conjunction(conj.getLoc(), lhs, rhs);
    else
      return conj;
  }

  @Override
  public ICondition transformDisjunction(Disjunction disj, ComputationContext context) {
    ICondition lhs = disj.getLhs().transform(this, context);
    ICondition rhs = disj.getRhs().transform(this, context);
    if (lhs != disj.getLhs() || rhs != disj.getRhs())
      return new Disjunction(disj.getLoc(), lhs, rhs);
    else
      return disj;
  }

  @Override
  public ICondition transformFalseCondition(FalseCondition falseCondition, ComputationContext context) {
    return falseCondition;
  }

  @Override
  public ICondition transformImplies(Implies implies, ComputationContext context) {
    ICondition lhs = implies.getGenerate().transform(this, context);
    ICondition rhs = implies.getTest().transform(this, context);
    if (lhs != implies.getGenerate() || rhs != implies.getTest())
      return new Implies(implies.getLoc(), lhs, rhs);
    else
      return implies;
  }

  @Override
  public ICondition transformIsTrue(IsTrue isTrue, ComputationContext context) {
    IContentExpression test = isTrue.getExp().transform(this, context);
    if (test == isTrue.getExp())
      return isTrue;
    else
      return new IsTrue(isTrue.getLoc(), test);
  }

  @Override
  public ICondition transformListSearch(ListSearch ptn, ComputationContext context) {
    IContentPattern elPtn = ptn.getPtn().transformPattern(this, context);
    IContentPattern ixPtn = ptn.getIx().transformPattern(this, context);
    IContentExpression src = ptn.getSource().transform(this, context);
    if (elPtn == ptn.getPtn() && ixPtn == ptn.getIx() && src == ptn.getSource())
      return ptn;
    else
      return new ListSearch(ptn.getLoc(), elPtn, ixPtn, src);
  }

  @Override
  public ICondition transformMatches(Matches matches, ComputationContext context) {
    IContentExpression matched = matches.getExp().transform(this, context);
    IContentPattern ptn = matches.getPtn().transformPattern(this, context);
    if (matched == matches.getExp() && ptn == matches.getPtn())
      return matches;
    else
      return new Matches(matches.getLoc(), matched, ptn);
  }

  @Override
  public IContentExpression transformMethodVariable(MethodVariable var, ComputationContext context) {
    return var;
  }

  @Override
  public ICondition transformNegation(Negation negation, ComputationContext context) {
    ICondition neg = negation.getNegated().transform(this, context);
    if (neg == negation.getNegated())
      return negation;
    else
      return new Negation(negation.getLoc(), neg);
  }

  @Override
  public ICondition transformOtherwise(Otherwise other, ComputationContext context) {
    ICondition lhs = other.getLhs().transform(this, context);
    ICondition rhs = other.getRhs().transform(this, context);
    if (lhs != other.getLhs() || rhs != other.getRhs())
      return new Otherwise(other.getLoc(), lhs, rhs);
    else
      return other;
  }

  @Override
  public ICondition transformSearch(Search search, ComputationContext context) {
    IContentPattern elPtn = search.getPtn().transformPattern(this, context);
    IContentExpression src = search.getSource().transform(this, context);
    if (elPtn == search.getPtn() && src == search.getSource())
      return search;
    else
      return new Search(search.getLoc(), elPtn, src);
  }

  @Override
  public ICondition transformTrueCondition(TrueCondition trueCondition, ComputationContext context) {
    return trueCondition;
  }

  @Override
  public IContentPattern transformRecordPtn(RecordPtn record, ComputationContext context) {
    Map<String, IContentPattern> els = new TreeMap<>();
    IContentExpression rec = record.getFun().transform(this, context);
    boolean clean = rec == record.getFun();
    for (Entry<String, IContentPattern> entry : record.getElements().entrySet()) {
      IContentPattern arg = entry.getValue().transformPattern(this, context);
      clean &= arg == entry.getValue();
      els.put(entry.getKey(), arg);
    }
    if (clean)
      return record;
    else
      return new RecordPtn(record.getLoc(), record.getType(), rec, els, record.getIndex());
  }

  @Override
  public IContentPattern transformCastPtn(CastPtn ptn, ComputationContext context) {
    IContentPattern inner = ptn.getInner().transformPattern(this, context);
    if (inner == ptn.getInner())
      return ptn;
    else
      return new CastPtn(ptn.getLoc(), ptn.getType(), inner);
  }

  @Override
  public IContentPattern transformMatchingPtn(MatchingPattern matches, ComputationContext context) {
    IContentPattern lhs = matches.getPtn().transformPattern(this, context);
    Variable var = (Variable) matches.getVar().transformPattern(this, context);
    if (lhs != matches.getPtn() || var != matches.getVar())
      return new MatchingPattern(matches.getLoc(), var, lhs);
    else
      return matches;
  }

  @Override
  public IContentPattern transformPatternApplication(PatternApplication apply, ComputationContext context) {
    IContentExpression ptn = apply.getAbstraction().transform(this, context);
    IContentPattern args = apply.getArg().transformPattern(this, context);
    if (ptn == apply.getAbstraction() && args == apply.getArg())
      return apply;
    else
      return new PatternApplication(apply.getLoc(), apply.getType(), ptn, args);
  }

  @Override
  public IContentPattern transformRegexpPtn(RegExpPattern ptn, ComputationContext context) {
    IContentPattern expGroup[] = ptn.getGroups();
    IContentPattern group[] = new IContentPattern[expGroup.length];
    boolean clean = true;
    for (int ix = 0; ix < expGroup.length; ix++) {
      group[ix] = expGroup[ix].transformPattern(this, context);
      clean &= group[ix] == expGroup[ix];
    }
    if (clean)
      return ptn;
    else
      return new RegExpPattern(ptn.getLoc(), ptn.getRegexpPtn(), ptn.getNfa(), group);
  }

  @Override
  public IContentPattern transformScalarPtn(ScalarPtn scalar, ComputationContext context) {
    return scalar;
  }

  @Override
  public IContentPattern transformConstructorPtn(ConstructorPtn tuple, ComputationContext context) {
    List<IContentPattern> els = new ArrayList<>();
    boolean clean = true;
    for (IContentPattern el : tuple.getElements()) {
      IContentPattern txEl = el.transformPattern(this, context);
      els.add(txEl);
      clean &= txEl == el;
    }
    if (clean)
      return tuple;
    else
      return new ConstructorPtn(tuple.getLoc(), tuple.getLabel(), tuple.getType(), els);
  }

  @Override
  public IContentPattern transformTuplePtn(TuplePtn tuple, ComputationContext context) {
    List<IContentPattern> els = new ArrayList<>();
    boolean clean = true;
    for (IContentPattern el : tuple.getElements()) {
      IContentPattern txEl = el.transformPattern(this, context);
      els.add(txEl);
      clean &= txEl == el;
    }
    if (clean)
      return tuple;
    else
      return new TuplePtn(tuple.getLoc(), tuple.getType(), els);
  }

  @Override
  public IContentPattern transformValuePtn(ValuePtn valuePtn, ComputationContext context) {
    IContentExpression val = valuePtn.getValue().transform(this, context);
    if (val == valuePtn.getValue())
      return valuePtn;
    else
      return new ValuePtn(valuePtn.getLoc(), val);
  }

  @Override
  public IContentPattern transformVariablePtn(Variable variable, ComputationContext context) {
    return variable;
  }

  @Override
  public IContentPattern transformWherePattern(WherePattern where, ComputationContext context) {
    IContentPattern ptn = where.getPtn().transformPattern(this, context);
    ICondition cond = where.getCond().transform(this, context);
    if (ptn == where.getPtn() && cond == where.getCond())
      return where;
    else
      return new WherePattern(where.getLoc(), ptn, cond);
  }

  public static IContentExpression exp2fun(Location loc, IContentPattern ptn, IContentExpression exp,
      ComputationContext context) {
    return exp2fun(loc, ptn, exp, context.getDict(), context.getOuter(), context.getErrors());
  }

  public static IContentExpression exp2fun(Location loc, IContentPattern ptn, IContentExpression exp, Dictionary dict,
      Dictionary outer, ErrorReport errors) {
    IType funType = TypeUtils.functionType(ptn.getType(), exp.getType());
    IContentPattern[] args = new IContentPattern[] { ptn };
    Variable[] free = FreeVariables.freeFreeVars(args, exp, dict);

    return MatchCompiler.generateFunction(null, Triple.create(args, CompilerUtils.truth, exp), funType, free,
        GenSym.genSym(StandardNames.LAMBDA), loc, dict, outer, errors);
  }

  private static IContentExpression exp2fun(Location loc, IContentExpression exp, ComputationContext context) {
    IType funType = TypeUtils.functionType(exp.getType());
    IContentPattern[] args = new IContentPattern[] {};
    Variable[] free = FreeVariables.freeFreeVars(args, exp, context.getDict());

    return MatchCompiler.generateFunction(null, Triple.create(args, CompilerUtils.truth, exp), funType, free,
        GenSym.genSym(StandardNames.LAMBDA), loc, context.getDict(), context.getOuter(), context.getErrors());
  }

  // bind has type
  // for all %a, %b, %%M st (%%M of %a,(%a)=>%%M of %b)=>%%M of %b

  private static IContentExpression bind(Location loc, IContentPattern ptn, IContentExpression exp,
      ComputationContext context) {
    return bind(loc, context.getExp(), ptn, exp, context.getmType(), context.getDict(), context.getOuter(),
        context.getErrors());
  }

  public static IContentExpression bind(Location loc, IContentExpression cont, IContentPattern ptn,
      IContentExpression exp, IType mType, Dictionary dict, Dictionary outer, ErrorReport errors) {
    assert TypeUtils.isType(exp.getType(), mType, 1, dict);

    if (cont == null)
      return exp;
    else if (isNullCombine(ptn, cont, mType))
      return exp;
    else if (isCombineEncapsulated(ptn, exp, mType)) {
      IContentExpression encapExp = encapsulated(exp);
      if (encapExp instanceof VoidExp && !isFreeInExp((Variable) ptn, cont))
        return cont;
      else {
        VarDeclaration decl = new VarDeclaration(loc, ptn, AccessMode.readOnly, encapExp);
        return valofValis(loc, cont, decl);
      }
    } else {
      IType aMonad = exp.getType();
      IType bMonad = cont.getType();

      IContentExpression nxtFun = exp2fun(loc, ptn, cont, dict, outer, errors);

      return combine(loc, exp, nxtFun, aMonad, bMonad, dict, errors);
    }
  }

  private static IContentExpression valofValis(Location loc, IContentExpression exp, IContentAction... actions) {
    List<IContentAction> acts = new ArrayList<>();
    Collections.addAll(acts, actions);

    if (exp instanceof ValofExp) {
      for (IContentAction sub : ((ValofExp) exp))
        acts.add(sub);
      return new ValofExp(loc, exp.getType(), acts);
    } else {
      acts.add(new ValisAction(loc, exp));
      return new ValofExp(loc, exp.getType(), acts);
    }
  }

  public static IContentExpression handle(Location loc, IContentExpression body, IContentExpression handler,
      IType mType, IType bMonad, Dictionary dict, Dictionary outer, ErrorReport errors) {
    IType exType = new TypeVar();
    IType handleType = TypeUtils.functionType(bMonad, TypeUtils.functionType(exType, bMonad), bMonad);

    IContentExpression handle = TypeChecker.typeOfName(loc, HANDLE, handleType, dict, errors);

    return Application.apply(loc, bMonad, handle, body, handler);
  }

  private static IContentExpression combine(Location loc, IContentExpression val, IContentExpression cont, IType aMonad,
      IType bMonad, Dictionary dict, ErrorReport errors) {
    IType bindType = TypeUtils.functionType(aMonad, cont.getType(), bMonad);

    IContentExpression combine = TypeChecker.typeOfName(loc, COMBINE, bindType, dict, errors);

    return Application.apply(loc, bMonad, combine, val, cont);
  }

  private IContentExpression encapsulate(Location loc, IContentExpression exp, ComputationContext context) {
    IType aType = exp.getType();
    IType mType = context.getmType();
    TypeExp aMonad = new TypeExp(mType, aType);
    IType encapType = TypeUtils.functionType(aType, aMonad);

    IContentExpression encapsulate = TypeChecker.typeOfName(loc, ENCAPSULATE, encapType, context.getDict(),
        context.getErrors());

    return Application.apply(loc, aMonad, encapsulate, exp);
  }

  private static IContentExpression abort(Location loc, IContentExpression exp, ComputationContext context) {
    IType exType = new TypeVar();
    IType mType = context.getmType();
    TypeExp aMonad = new TypeExp(mType, new TypeVar());
    IType abortType = TypeUtils.functionType(exType, aMonad);

    IContentExpression abort = TypeChecker.typeOfName(loc, ABORT, abortType, context.getDict(), context.getErrors());

    return Application.apply(loc, aMonad, abort, exp);
  }

  // construct an outer 'delay' to prevent the task being performed until valof
  // performed
  private static IContentExpression delay(Location loc, IContentExpression cont, ComputationContext context) {
    IContentExpression delayFun = exp2fun(loc, cont, context);

    IType aMonad = cont.getType();
    IType delayType = TypeUtils.functionType(delayFun.getType(), aMonad);

    IContentExpression delay = TypeChecker.typeOfName(loc, DELAY, delayType, context.getDict(), context.getErrors());

    return Application.apply(loc, aMonad, delay, delayFun);
  }

  public static IContentExpression perform(Location loc, IType mType, IContentExpression exp, Dictionary dict,
      ErrorReport errors) {
    IType aMonad = exp.getType();
    IType aType = TypeUtils.isType(aMonad, mType, 1, dict) ? TypeUtils.getTypeArg(aMonad, 0) : new TypeVar();
    IType performType = TypeUtils.functionType(aMonad, aType);

    IContentExpression perform = TypeChecker.typeOfName(loc, PERFORM, performType, dict, errors);

    return Application.apply(loc, aType, perform, exp);
  }

  private static boolean isPerform(IContentExpression exp, IType mType) {
    if (exp instanceof Application) {
      Application appl = (Application) exp;
      IContentExpression op = appl.getFunction();

      if (op instanceof MethodVariable && appl.arity() == 1) {
        MethodVariable method = (MethodVariable) op;
        if (method.getName().equals(PERFORM)
            && mType.typeLabel().equals(TypeUtils.getTypeArg(method.getContract(), 0).typeLabel())) {
          return true;
        }
      }
    }
    return false;
  }

  private static IContentExpression performedTask(IContentExpression exp, IType mType) {
    assert isPerform(exp, mType);
    return ((Application) exp).getArg(0);
  }

  private static boolean isInjection(IContentExpression exp) {
    if (exp instanceof Application) {
      Application appl = (Application) exp;
      IContentExpression op = appl.getFunction();

      if (op instanceof MethodVariable && appl.arity() == 1) {
        MethodVariable method = (MethodVariable) op;
        return method.getName().equals(PERFORM);
      }
    }
    return false;
  }

  @SuppressWarnings("unused")
  private static IType injectedMonad(IContentExpression exp) {
    assert isInjection(exp);
    Application app = (Application) exp;
    MethodVariable method = (MethodVariable) app.getFunction();
    return TypeUtils.getTypeArg(method.getContract(), 0);
  }

  private static IContentExpression injectedValue(IContentExpression exp) {
    assert isInjection(exp);
    Application app = (Application) exp;
    return app.getArg(0);
  }

  /**
   * Construct a call to the injection contract
   *
   * @param loc
   * @param nType
   *          destination monad
   * @param exp
   *          expression to inject
   * @param dict
   *          dictionary to access contracts etc.
   * @param errors
   *          error reporter
   * @return
   */
  public static IContentExpression inject(Location loc, IType nType, IContentExpression exp, Dictionary dict,
      ErrorReport errors) {
    IType srcType = exp.getType();
    IType aType = TypeUtils.getTypeArg(srcType, 0);
    IType retType = TypeUtils.typeExp(nType, aType);

    IType injectType = TypeUtils.functionType(srcType, retType);

    IContentExpression inject = TypeChecker.typeOfName(loc, INJECT, injectType, dict, errors);

    return Application.apply(loc, retType, inject, exp);
  }

  private static boolean isVoidCombine(IContentExpression exp, IType mType) {
    if (exp instanceof Application) {
      Application appl = (Application) exp;
      IContentExpression op = appl.getFunction();

      if (op instanceof MethodVariable && appl.arity() == 2) {
        MethodVariable method = (MethodVariable) op;
        if (method.getName().equals(COMBINE)
            && mType.typeLabel().equals(TypeUtils.getTypeArg(method.getContract(), 0).typeLabel())) {
          IContentExpression combTask = appl.getArg(0);

          if (isEncapsulated(combTask, mType)) {
            IContentExpression encapsulated = encapsulated(combTask);
            return encapsulated instanceof VoidExp;
          }

          return true;
        }
      }
    }
    return false;
  }

  private static IContentExpression combinedTask(IContentExpression exp) {
    IContentExpression f = ((Application) exp).getArg(1);
    assert f instanceof FunctionLiteral;
    FunctionLiteral fun = (FunctionLiteral) f;
    return fun.getBody();
  }

  private static boolean isEncapsulated(IContentExpression exp, IType mType) {
    if (exp instanceof Application) {
      Application appl = (Application) exp;
      IContentExpression op = appl.getFunction();

      if (op instanceof MethodVariable && appl.arity() == 1) {
        MethodVariable method = (MethodVariable) op;
        return method.getName().equals(ENCAPSULATE)
            && mType.typeLabel().equals(TypeUtils.getTypeArg(method.getContract(), 0).typeLabel());
      }
    }
    return false;
  }

  private static IContentExpression encapsulated(IContentExpression exp) {
    return ((Application) exp).getArg(0);
  }

  private static boolean isNullCombine(IContentPattern ptn, IContentExpression cont, IType mType) {
    if (ptn instanceof Variable) {
      if (isEncapsulated(cont, mType) && encapsulated(cont).equals(ptn))
        return true;
    }
    return false;
  }

  private static boolean isCombineEncapsulated(IContentPattern ptn, IContentExpression cont, IType mType) {
    return ptn instanceof Variable && isEncapsulated(cont, mType);
  }

  private static boolean isFreeInExp(Variable var, IContentExpression exp) {
    return FreeVariables.isFreeIn(var, exp);
  }
}
