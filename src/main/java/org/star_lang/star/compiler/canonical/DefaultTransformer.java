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
package org.star_lang.star.compiler.canonical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.canonical.EnvironmentEntry.ContractEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImplementationEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImportEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.TypeAliasEntry;
import org.star_lang.star.compiler.util.Pair;

public class DefaultTransformer<T> implements
    TransformExpression<IContentAction, IContentExpression, IContentPattern, ICondition, IStatement, T>,
    TransformAction<IContentAction, IContentExpression, IContentPattern, ICondition, IStatement, T>,
    TransformPattern<IContentAction, IContentExpression, IContentPattern, ICondition, IStatement, T>,
    TransformStatement<IContentAction, IContentExpression, IContentPattern, ICondition, IStatement, T>,
    TransformCondition<IContentAction, IContentExpression, IContentPattern, ICondition, IStatement, T>
{

  @Override
  public IContentExpression transformApplication(Application appl, T context)
  {
    IContentExpression fun = appl.getFunction().transform(this, context);
    IContentExpression args = appl.getArgs().transform(this, context);
    if (fun == appl.getFunction() && args == appl.getArgs())
      return appl;
    else
      return new Application(appl.getLoc(), appl.getType(), fun, args);
  }

  @Override
  public IContentExpression transformRecord(RecordTerm record, T context)
  {
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
  public IContentExpression transformRecordSubstitute(RecordSubstitute update, T context)
  {
    IContentExpression rec = update.getRoute().transform(this, context);
    IContentExpression rep = update.getReplace().transform(this, context);
    if (rec == update.getRoute() && rep == update.getReplace())
      return update;
    else
      return new RecordSubstitute(update.getLoc(), update.getType(), rec, rep);
  }

  @Override
  public IContentExpression transformCaseExpression(CaseExpression exp, T context)
  {
    IContentExpression sel = exp.getSelector().transform(this, context);
    boolean clean = sel == exp.getSelector();
    List<Pair<IContentPattern, IContentExpression>> cases = new ArrayList<>();
    for (Pair<IContentPattern, IContentExpression> pair : exp.getCases()) {
      IContentPattern ptn = pair.left().transformPattern(this, context);
      IContentExpression val = pair.right().transform(this, context);
      clean &= ptn == pair.left() && val == pair.right();
      cases.add(Pair.pair(ptn, val));
    }
    IContentExpression defl = exp.getDeflt().transform(this, context);
    clean &= defl == exp.getDeflt();
    if (clean)
      return exp;
    else
      return new CaseExpression(exp.getLoc(), exp.getType(), sel, cases, defl);
  }

  @Override
  public IContentExpression transformCastExpression(CastExpression exp, T context)
  {
    IContentExpression val = exp.getInner().transform(this, context);
    if (val == exp.getInner())
      return exp;
    else
      return new CastExpression(exp.getLoc(), exp.getType(), val);
  }

  @Override
  public IContentExpression transformConditionalExp(ConditionalExp act, T context)
  {
    ICondition tst = act.getCnd().transform(this, context);
    IContentExpression thn = act.getThExp().transform(this, context);
    IContentExpression els = act.getElExp().transform(this, context);
    if (tst == act.getCnd() && thn == act.getThExp() && els == act.getElExp())
      return act;
    else
      return new ConditionalExp(act.getLoc(), act.getType(), tst, thn, els);
  }

  @Override
  public IContentExpression transformContentCondition(ContentCondition cond, T context)
  {
    ICondition test = cond.getCondition().transform(this, context);
    if (test == cond.getCondition())
      return cond;
    else
      return new ContentCondition(cond.getLoc(), test);
  }

  @Override
  public IContentExpression transformMemo(MemoExp memo, T context)
  {
    IContentExpression exp = memo.getMemo().transform(this, context);
    IContentExpression[] memoFree = memo.getFreeVars();
    IContentExpression free[] = new IContentExpression[memoFree.length];
    boolean clean = exp == memo.getMemo();
    for (int ix = 0; ix < free.length; ix++) {
      free[ix] = memoFree[ix].transform(this, context);
      clean &= free[ix] == memoFree[ix];
    }
    if (clean)
      return memo;
    else
      return new MemoExp(memo.getLoc(), exp, free);
  }

  @Override
  public IContentExpression transformMethodVariable(MethodVariable var, T context)
  {
    return var;
  }

  @Override
  public IContentExpression transformNullExp(NullExp nil, T context)
  {
    return nil;
  }

  @Override
  public IContentExpression transformFunctionLiteral(FunctionLiteral f, T context)
  {
    IContentExpression exp = f.getBody().transform(this, context);
    IContentExpression[] fFree = f.getFreeVars();
    Variable free[] = new Variable[fFree.length];
    boolean clean = exp == f.getBody();
    for (int ix = 0; ix < free.length; ix++) {
      free[ix] = (Variable) fFree[ix].transform(this, context);
      clean &= free[ix] == fFree[ix];
    }
    IContentPattern fArgs[] = f.getArgs();
    IContentPattern args[] = new IContentPattern[fArgs.length];
    for (int ix = 0; ix < args.length; ix++) {
      args[ix] = fArgs[ix].transformPattern(this, context);
      clean &= args[ix] == fArgs[ix];
    }
    if (clean)
      return f;
    else
      return new FunctionLiteral(f.getLoc(), f.getName(), f.getType(), args, exp, free);
  }

  @Override
  public IContentExpression transformLetTerm(LetTerm let, T context)
  {
    List<IStatement> env = new ArrayList<>();
    boolean clean = true;
    for (IStatement stmt : let.getEnvironment()) {
      IStatement nStmt = stmt.transform(this, context);
      clean &= stmt == nStmt;
      env.add(nStmt);
    }
    IContentExpression bound = let.getBoundExp().transform(this, context);
    clean &= bound == let.getBoundExp();
    if (clean)
      return let;
    else
      return new LetTerm(let.getLoc(), bound, env);
  }

  @Override
  public IContentExpression transformOverloaded(Overloaded over, T context)
  {
    IContentExpression inner = over.getInner().transform(this, context);
    if (inner == over.getInner())
      return over;
    else
      return new Overloaded(over.getLoc(), over.getType(), over.getDictType(), inner);
  }

  @Override
  public IContentExpression transformOverloadedFieldAccess(OverloadedFieldAccess dot, T context)
  {
    IContentExpression record = dot.getRecord().transform(this, context);
    if (record == dot.getRecord())
      return dot;
    else
      return new OverloadedFieldAccess(dot.getLoc(), dot.getType(), dot.getDictType(), record, dot.getField());
  }

  @Override
  public IContentExpression transformOverloadVariable(OverloadedVariable var, T context)
  {
    return var;
  }

  @Override
  public IContentExpression transformPatternAbstraction(PatternAbstraction pattern, T context)
  {
    IContentExpression reslt = pattern.getResult().transform(this, context);
    IContentPattern ptn = pattern.getMatch().transformPattern(this, context);
    boolean clean = reslt == pattern.getResult() && ptn == pattern.getMatch();
    Variable[] free = pattern.getFreeVars();
    Variable[] nFree = new Variable[free.length];
    for (int ix = 0; ix < free.length; ix++) {
      nFree[ix] = (Variable) free[ix].transform(this, context);
      clean &= free[ix] == nFree[ix];
    }
    if (clean)
      return pattern;
    else
      return new PatternAbstraction(pattern.getLoc(), pattern.getName(), pattern.getType(), ptn, reslt, nFree);
  }

  @Override
  public IContentExpression transformFieldAccess(FieldAccess dot, T context)
  {
    IContentExpression record = dot.getRecord().transform(this, context);
    if (record == dot.getRecord())
      return dot;
    else
      return new FieldAccess(dot.getLoc(), dot.getType(), record, dot.getField());
  }

  @Override
  public IContentExpression transformRaiseExpression(RaiseExpression exp, T context)
  {
    IContentExpression raised = exp.getRaise().transform(this, context);
    if (raised == exp.getRaise())
      return exp;
    else
      return new RaiseExpression(exp.getLoc(), exp.getType(), raised);
  }

  @Override
  public IContentExpression transformReference(Shriek reference, T context)
  {
    IContentExpression ref = reference.getReference().transform(this, context);
    if (ref == reference.getReference())
      return reference;
    else
      return new Shriek(reference.getLoc(), ref);
  }

  @Override
  public IContentExpression transformResolved(Resolved res, T context)
  {
    IContentExpression over = res.getOver().transform(this, context);
    boolean clean = over == res.getOver();
    IContentExpression[] dicts = res.getDicts();
    IContentExpression[] nDicts = new IContentExpression[dicts.length];
    for (int ix = 0; ix < dicts.length; ix++) {
      nDicts[ix] = dicts[ix].transform(this, context);
      clean &= dicts[ix] == nDicts[ix];
    }
    if (clean)
      return res;
    else
      return new Resolved(res.getLoc(), res.getType(), res.getDictType(), over, nDicts);

  }

  @Override
  public IContentExpression transformScalar(Scalar scalar, T context)
  {
    return scalar;
  }

  @Override
  public IContentExpression transformConstructor(ConstructorTerm tuple, T context)
  {
    boolean clean = true;
    List<IContentExpression> els = new ArrayList<>();
    for (IContentExpression el : tuple.getElements()) {
      IContentExpression nEl = el.transform(this, context);
      els.add(nEl);
      clean &= nEl == el;
    }
    if (clean)
      return tuple;
    else
      return new ConstructorTerm(tuple.getLoc(), tuple.getLabel(), tuple.getType(), els);
  }

  @Override
  public IContentExpression transformValofExp(ValofExp val, T context)
  {
    IContentAction act = val.getAction().transform(this, context);
    if (act == val.getAction())
      return val;
    else
      return new ValofExp(val.getLoc(), val.getType(), act);
  }

  @Override
  public IContentExpression transformVariable(Variable var, T context)
  {
    return var;
  }

  @Override
  public IContentExpression transformVoidExp(VoidExp exp, T context)
  {
    return exp;
  }

  @Override
  public IContentAction transformAssertAction(AssertAction act, T context)
  {
    IContentExpression test = act.getAssertion().transform(this, context);
    if (test == act.getAssertion())
      return act;
    else
      return new AssertAction(act.getLoc(), test);
  }

  @Override
  public IContentAction transformAssignment(Assignment act, T context)
  {
    IContentExpression lv = act.getLValue().transform(this, context);
    IContentExpression rv = act.getValue().transform(this, context);
    if (lv == act.getLValue() && rv == act.getValue())
      return act;
    else
      return new Assignment(act.getLoc(), lv, rv);
  }

  @Override
  public IContentAction transformCaseAction(CaseAction act, T context)
  {
    IContentExpression sel = act.getSelector().transform(this, context);
    boolean clean = sel == act.getSelector();
    List<Pair<IContentPattern, IContentAction>> cases = new ArrayList<>();
    for (Pair<IContentPattern, IContentAction> cse : act.getCases()) {
      Pair<IContentPattern, IContentAction> nc = Pair.pair(cse.left().transformPattern(this, context), cse.right()
          .transform(this, context));
      clean &= nc.left() == cse.left() && nc.right() == cse.right();
      cases.add(nc);
    }
    IContentAction deflt = act.getDeflt().transform(this, context);
    clean &= deflt == act.getDeflt();
    if (clean)
      return act;
    else
      return new CaseAction(act.getLoc(), sel, cases, deflt);
  }

  @Override
  public IContentAction transformConditionalAction(ConditionalAction act, T context)
  {
    ICondition test = act.getCond().transform(this, context);
    IContentAction th = act.getThPart().transform(this, context);
    IContentAction el = act.getElPart().transform(this, context);
    if (test == act.getCond() && th == act.getThPart() && el == act.getElPart())
      return act;
    else
      return new ConditionalAction(act.getLoc(), test, th, el);
  }

  @Override
  public IContentAction transformExceptionHandler(ExceptionHandler except, T context)
  {
    IContentAction body = except.getBody().transform(this, context);

    boolean clean = body == except.getBody();

    IContentAction handler = except.getHandler().transform(this, context);
    clean &= handler == except.getHandler();

    if (clean)
      return except;
    else
      return new ExceptionHandler(except.getLoc(), body, handler);
  }

  @Override
  public IContentAction transformForLoop(ForLoopAction loop, T context)
  {
    ICondition cond = loop.getControl().transform(this, context);
    IContentAction body = loop.getBody().transform(this, context);
    if (cond == loop.getControl() && body == loop.getBody())
      return loop;
    else
      return new ForLoopAction(loop.getLoc(), cond, loop.getFree(), loop.getDefined(), body);
  }

  @Override
  public IContentAction transformIgnored(Ignore act, T context)
  {
    IContentExpression ignored = act.getIgnored().transform(this, context);
    if (ignored == act.getIgnored())
      return act;
    else
      return new Ignore(act.getLoc(), ignored);
  }

  @Override
  public IContentAction transformLetAction(LetAction let, T context)
  {
    List<IStatement> defs = new ArrayList<>();
    boolean clean = true;
    for (IStatement st : let.getEnvironment()) {
      IStatement def = st.transform(this, context);
      clean &= def == st;
      defs.add(def);
    }
    IContentAction bound = let.getBoundAction().transform(this, context);
    clean &= bound == let.getBoundAction();
    if (clean)
      return let;
    else
      return new LetAction(let.getLoc(), defs, bound);
  }

  @Override
  public IContentAction transformRaiseAction(RaiseAction raise, T context)
  {
    IContentExpression raised = raise.getRaised().transform(this, context);
    if (raised == raise.getRaised())
      return raise;
    else
      return new RaiseAction(raise.getLoc(), raised);
  }

  @Override
  public IContentAction transformWhileLoop(WhileAction act, T context)
  {
    ICondition cont = act.getControl().transform(this, context);
    IContentAction body = act.getBody().transform(this, context);
    if (cont == act.getControl() && body == act.getBody())
      return act;
    else
      return new WhileAction(act.getLoc(), cont, body);
  }

  @Override
  public IContentAction transformNullAction(NullAction act, T context)
  {
    return act;
  }

  @Override
  public IContentAction transformProcedureCallAction(ProcedureCallAction call, T context)
  {
    IContentExpression proc = call.getProc().transform(this, context);
    boolean clean = proc == call.getProc();
    IContentExpression[] args = call.getArgs();
    IContentExpression[] nArgs = new IContentExpression[args.length];
    for (int ix = 0; ix < args.length; ix++) {
      nArgs[ix] = args[ix].transform(this, context);
      clean &= nArgs[ix] == args[ix];
    }
    if (clean)
      return call;
    else
      return new ProcedureCallAction(call.getLoc(), proc, nArgs);
  }

  @Override
  public IContentAction transformSequence(Sequence sequence, T context)
  {
    boolean clean = true;
    List<IContentAction> acts = new ArrayList<>();
    for (IContentAction act : sequence.getActions()) {
      IContentAction a = act.transform(this, context);
      acts.add(a);
      clean &= a == act;
    }
    if (clean)
      return sequence;
    else
      return new Sequence(sequence.getLoc(), sequence.getType(), acts);
  }

  @Override
  public IContentAction transformSyncAction(SyncAction sync, T context)
  {
    IContentExpression sel = sync.getSel().transform(this, context);
    boolean clean = sel == sync.getSel();
    Map<ICondition, IContentAction> cases = new HashMap<>();
    for (Entry<ICondition, IContentAction> entry : sync.getBody().entrySet()) {
      ICondition test = entry.getKey().transform(this, context);
      IContentAction act = entry.getValue().transform(this, context);
      clean &= test == entry.getKey() && act == entry.getValue();
      cases.put(test, act);
    }
    if (clean)
      return sync;
    else
      return new SyncAction(sync.getLoc(), sync.getType(), sel, cases);
  }

  @Override
  public IContentAction transformValisAction(ValisAction act, T context)
  {

    IContentExpression exp = act.getValue().transform(this, context);
    if (exp == act.getValue())
      return act;
    else
      return new ValisAction(act.getLoc(), exp);
  }

  @Override
  public IContentAction transformVarDeclaration(VarDeclaration var, T context)
  {
    IContentPattern ptn = var.getPattern().transformPattern(this, context);
    IContentExpression val = var.getValue().transform(this, context);
    if (ptn == var.getPattern() && val == var.getValue())
      return var;
    else
      return new VarDeclaration(var.getLoc(), ptn, var.isReadOnly(), val);
  }

  @Override
  public IContentAction transformYield(Yield act, T context)
  {
    IContentAction yield = act.getYielded().transform(this, context);
    if (yield == act.getYielded())
      return act;
    else
      return new Yield(act.getLoc(), yield);
  }

  @Override
  public ICondition transformConditionCondition(ConditionCondition cond, T context)
  {
    ICondition test = cond.getTest().transform(this, context);
    ICondition th = cond.getLhs().transform(this, context);
    ICondition el = cond.getRhs().transform(this, context);
    if (test == cond.getTest() && th == cond.getLhs() && el == cond.getRhs())
      return cond;
    else
      return new ConditionCondition(cond.getLoc(), test, th, el);
  }

  @Override
  public ICondition transformConjunction(Conjunction conj, T context)
  {
    ICondition lhs = conj.getLhs().transform(this, context);
    ICondition rhs = conj.getRhs().transform(this, context);
    if (lhs == conj.getLhs() && rhs == conj.getRhs())
      return conj;
    else
      return new Conjunction(conj.getLoc(), lhs, rhs);
  }

  @Override
  public ICondition transformDisjunction(Disjunction disj, T context)
  {
    ICondition lhs = disj.getLhs().transform(this, context);
    ICondition rhs = disj.getRhs().transform(this, context);
    if (lhs == disj.getLhs() && rhs == disj.getRhs())
      return disj;
    else
      return new Disjunction(disj.getLoc(), lhs, rhs);
  }

  @Override
  public ICondition transformFalseCondition(FalseCondition falseCondition, T context)
  {
    return falseCondition;
  }

  @Override
  public ICondition transformImplies(Implies implies, T context)
  {
    ICondition lhs = implies.getGenerate().transform(this, context);
    ICondition rhs = implies.getTest().transform(this, context);
    if (lhs == implies.getGenerate() && rhs == implies.getTest())
      return implies;
    else
      return new Implies(implies.getLoc(), lhs, rhs);
  }

  @Override
  public ICondition transformIsTrue(IsTrue i, T context)
  {
    return new IsTrue(i.getLoc(), i.getExp().transform(this, context));
  }

  @Override
  public ICondition transformListSearch(ListSearch search, T context)
  {
    IContentPattern ptn = search.getPtn().transformPattern(this, context);
    IContentPattern ix = search.getIx().transformPattern(this, context);
    IContentExpression src = search.getSource().transform(this, context);
    if (ptn == search.getPtn() && ix == search.getIx() && src == search.getSource())
      return search;
    else
      return new ListSearch(search.getLoc(), ptn, ix, src);
  }

  @Override
  public ICondition transformMatches(Matches matches, T context)
  {
    IContentExpression exp = matches.getExp().transform(this, context);
    IContentPattern ptn = matches.getPtn().transformPattern(this, context);
    if (exp == matches.getExp() && ptn == matches.getPtn())
      return matches;
    else
      return new Matches(matches.getLoc(), exp, ptn);
  }

  @Override
  public ICondition transformNegation(Negation negation, T context)
  {
    ICondition neg = negation.getNegated().transform(this, context);
    if (neg == negation.getNegated())
      return negation;
    else
      return new Negation(negation.getLoc(), neg);
  }

  @Override
  public ICondition transformOtherwise(Otherwise oth, T context)
  {
    ICondition lhs = oth.getLhs().transform(this, context);
    ICondition rhs = oth.getRhs().transform(this, context);
    if (lhs == oth.getLhs() && rhs == oth.getRhs())
      return oth;
    else
      return new Otherwise(oth.getLoc(), lhs, rhs);
  }

  @Override
  public ICondition transformSearch(Search search, T context)
  {
    IContentPattern ptn = search.getPtn().transformPattern(this, context);
    IContentExpression src = search.getSource().transform(this, context);
    if (ptn == search.getPtn() && src == search.getSource())
      return search;
    else
      return new Search(search.getLoc(), ptn, src);
  }

  @Override
  public ICondition transformTrueCondition(TrueCondition trueCondition, T context)
  {
    return trueCondition;
  }

  @Override
  public IStatement transformContractDefn(ContractEntry con, T context)
  {
    return con;
  }

  @Override
  public IStatement transformContractImplementation(ImplementationEntry entry, T context)
  {
    return entry;
  }

  @Override
  public IStatement transformImportEntry(ImportEntry entry, T context)
  {
    return entry;
  }

  @Override
  public IStatement transformJavaEntry(JavaEntry entry, T context)
  {
    return entry;
  }

  @Override
  public IStatement transformTypeAliasEntry(TypeAliasEntry entry, T context)
  {
    return entry;
  }

  @Override
  public IStatement transformTypeEntry(TypeDefinition entry, T context)
  {
    return entry;
  }

  @Override
  public IStatement transformVarEntry(VarEntry entry, T context)
  {
    IContentPattern ptn = entry.getVarPattern().transformPattern(this, context);
    IContentExpression val = entry.getValue().transform(this, context);
    if (ptn == entry.getVarPattern() && val == entry.getValue())
      return entry;
    else
      return new VarEntry(entry.getLoc(), ptn, val, entry.isReadOnly(), entry.getVisibility());
  }

  @Override
  public IStatement transformOpenStatement(OpenStatement open, T context)
  {
    IContentExpression record = open.getRecord().transform(this, context);
    if (record == open.getRecord())
      return open;
    else
      return new OpenStatement(open.getLoc(), record, open.getFace(), open.getVisibility());
  }

  @Override
  public IStatement transformWitness(TypeWitness stmt, T context)
  {
    return stmt;
  }

  @Override
  public IContentPattern transformRecordPtn(RecordPtn rec, T context)
  {
    Map<String, IContentPattern> els = new TreeMap<>();
    boolean clean = true;
    for (Entry<String, IContentPattern> entry : rec.getElements().entrySet()) {
      IContentPattern arg = entry.getValue().transformPattern(this, context);
      clean &= arg == entry.getValue();
      els.put(entry.getKey(), arg);
    }
    if (clean)
      return rec;
    else
      return new RecordPtn(rec.getLoc(), rec.getType(), els, rec.getIndex());
  }

  @Override
  public IContentPattern transformCastPtn(CastPtn cast, T context)
  {
    IContentPattern ptn = cast.getInner().transformPattern(this, context);
    if (ptn == cast.getInner())
      return cast;
    else
      return new CastPtn(cast.getLoc(), cast.getType(), ptn);
  }

  @Override
  public IContentPattern transformMatchingPtn(MatchingPattern matches, T context)
  {
    IContentPattern ptn = matches.getPtn().transformPattern(this, context);
    IContentPattern var = matches.getVar().transformPattern(this, context);
    if (ptn == matches.getPtn() && var == matches.getVar())
      return matches;
    else
      return new MatchingPattern(matches.getLoc(), (Variable) var, ptn);
  }

  @Override
  public IContentPattern transformPatternApplication(PatternApplication apply, T context)
  {
    IContentExpression ptn = apply.getAbstraction().transform(this, context);
    IContentPattern arg = apply.getArg().transformPattern(this, context);
    if (ptn == apply.getAbstraction() && arg == apply.getArg())
      return apply;
    else
      return new PatternApplication(apply.getLoc(), apply.getType(), ptn, arg);
  }

  @Override
  public IContentPattern transformRegexpPtn(RegExpPattern ptn, T context)
  {
    IContentPattern[] groups = ptn.getGroups();
    IContentPattern[] nGps = new IContentPattern[groups.length];
    boolean clean = true;
    for (int ix = 0; ix < groups.length; ix++) {
      nGps[ix] = groups[ix].transformPattern(this, context);
      clean &= nGps[ix] == groups[ix];
    }
    if (clean)
      return ptn;
    else
      return new RegExpPattern(ptn.getLoc(), ptn.getRegexpPtn(), ptn.getNfa(), nGps);
  }

  @Override
  public IContentPattern transformScalarPtn(ScalarPtn scalar, T context)
  {
    return scalar;
  }

  @Override
  public IContentPattern transformConstructorPtn(ConstructorPtn con, T context)
  {
    List<IContentPattern> els = new ArrayList<>();
    boolean clean = true;
    for (IContentPattern el : con.getElements()) {
      IContentPattern arg = el.transformPattern(this, context);
      els.add(arg);
      clean &= arg == el;
    }
    if (clean)
      return con;
    else
      return new ConstructorPtn(con.getLoc(), con.getLabel(), con.getType(), els);
  }

  @Override
  public IContentPattern transformVariablePtn(Variable variable, T context)
  {
    return variable;
  }

  @Override
  public IContentPattern transformWherePattern(WherePattern where, T context)
  {
    IContentPattern ptn = where.getPtn().transformPattern(this, context);
    ICondition cond = where.getCond().transform(this, context);
    if (ptn == where.getPtn() && cond == where.getCond())
      return where;
    else
      return new WherePattern(where.getLoc(), ptn, cond);
  }
}
