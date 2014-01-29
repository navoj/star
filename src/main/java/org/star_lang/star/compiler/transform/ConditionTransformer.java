package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.FreeVariables;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.ConditionCondition;
import org.star_lang.star.compiler.canonical.ConditionalAction;
import org.star_lang.star.compiler.canonical.ConditionalExp;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.ConstructorPtn;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.ExpressionTransformer;
import org.star_lang.star.compiler.canonical.FalseCondition;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.Implies;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.ListSearch;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.Negation;
import org.star_lang.star.compiler.canonical.Otherwise;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.Sequence;
import org.star_lang.star.compiler.canonical.TrueCondition;
import org.star_lang.star.compiler.canonical.ValisAction;
import org.star_lang.star.compiler.canonical.ValofExp;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.WherePattern;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DictionaryChecker;
import org.star_lang.star.compiler.type.TypeChecker;
import org.star_lang.star.compiler.type.TypeContracts;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.ListUtils;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.Triple;
import org.star_lang.star.compiler.util.Wrapper;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;

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
public class ConditionTransformer
{
  public static IContentExpression transformConstraint(ICondition query, List<Variable> definedVars, WrapState succAdd,
      IContentExpression initState, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    query = FlowAnalysis.analyseFlow(query, definedVars, new DictionaryChecker(cxt, definedVars));

    if (query instanceof Search)
      return searchQuery((Search) query, CompilerUtils.truth, definedVars, succAdd, initState, cxt, outer, errors);
    else if (query instanceof ListSearch)
      return indexingSearchQuery((ListSearch) query, CompilerUtils.truth, definedVars, succAdd, initState, cxt, outer,
          errors);
    else if (query instanceof Disjunction)
      return disjunctiveQuery((Disjunction) query, definedVars, succAdd, initState, cxt, outer, errors);
    else if (query instanceof Conjunction)
      return conjunctiveQuery((Conjunction) query, definedVars, succAdd, initState, cxt, outer, errors);
    else if (query instanceof Negation)
      return negationQuery((Negation) query, definedVars, succAdd, initState, cxt, outer, errors);
    else if (query instanceof Otherwise)
      return otherwiseQuery((Otherwise) query, definedVars, succAdd, initState, cxt, outer, errors);
    else if (query instanceof Implies)
      return impliesQuery((Implies) query, definedVars, succAdd, initState, cxt, outer, errors);
    else if (query instanceof IsTrue)
      return isTrueQuery((IsTrue) query, definedVars, succAdd, initState, cxt, errors);
    else if (query instanceof TrueCondition)
      return succAdd.addToFront(initState);
    else if (query instanceof FalseCondition)
      return initState;
    else if (query instanceof ConditionCondition)
      return conditionalQuery((ConditionCondition) query, definedVars, succAdd, initState, cxt, outer, errors);
    else if (query instanceof Matches)
      return matchesQuery((Matches) query, definedVars, succAdd, initState, cxt, errors);
    else {
      errors.reportError("unknown form of condition: " + query, query.getLoc());
      return initState;
    }
  }

  private static IContentExpression negationQuery(Negation neg, List<Variable> definedVars, WrapState succAdd,
      IContentExpression initState, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    final Location loc = neg.getLoc();
    final IType stType = initState.getType();

    WrapState trueVal = new WrapState() {

      @Override
      public IContentExpression addToFront(IContentExpression state)
      {
        return CompilerUtils.noMore(loc, CompilerUtils.trueLiteral(loc));
      }
    };

    IContentExpression negated = transformConstraint(neg.getNegated(), definedVars, trueVal, CompilerUtils
        .continueWith(loc, CompilerUtils.falseLiteral(loc)), cxt, outer, errors);

    String negFunName = GenSym.genSym("_F");
    IType negFunType = TypeUtils.functionType(stType);
    Variable[] failFree = FreeVariables.findFreeVars(initState, cxt);
    FunctionLiteral negFun = new FunctionLiteral(loc, negFunName, negFunType, new IContentPattern[] {}, initState,
        failFree);
    Variable negVar = Variable.create(loc, negFunType, negFunName);

    IStatement negDef = VarEntry.createVarEntry(loc, negVar, negFun, AccessMode.readOnly, Visibility.priVate);

    IContentExpression nF = new LetTerm(loc, negVar, negDef);

    String sucFunName = GenSym.genSym("_T");
    IContentExpression succ = succAdd.addToFront(initState);
    Variable[] succFree = FreeVariables.findFreeVars(succ, cxt);
    FunctionLiteral succFn = new FunctionLiteral(loc, sucFunName, negFunType, new IContentPattern[] {}, succ, succFree);

    Variable succVar = Variable.create(loc, negFunType, sucFunName);

    IStatement sucDef = VarEntry.createVarEntry(loc, succVar, succFn, AccessMode.readOnly, Visibility.priVate);

    IContentExpression sF = new LetTerm(loc, succVar, sucDef);

    IContentExpression negate = TypeChecker.typeOfName(loc, StandardNames.NEGATE, new TypeVar(), cxt, errors);

    return Application.apply(loc, stType, negate, negated, nF, sF);
  }

  private static IContentExpression impliesQuery(Implies implies, List<Variable> definedVars, WrapState succAdd,
      IContentExpression initState, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    Location loc = implies.getLoc();
    return transformConstraint(new Negation(loc, new Conjunction(loc, implies.getGenerate(), new Negation(loc, implies
        .getTest()))), definedVars, succAdd, initState, cxt, outer, errors);
  }

  private static IContentExpression otherwiseQuery(Otherwise other, List<Variable> definedVars, WrapState succAdd,
      IContentExpression initState, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    /**
     * An otherwise query is equivalent to a left join:
     * 
     * L otherwise R == (Vars) in (L leftjoin R)
     * 
     * Convert to
     * 
     * <pre>
     * let{
     *   addTpl(Vars,ContinueWith(R)) is ContinueWith(_cons(Vars,R));
     *   
     *   LHS = <transform>(lhs,<addTpl>,ContinueWith(_nil()));
     *   RHS() is <transform>(rhs,<addTpl>,ContinueWith(_nil()));
     *   extract(Vars,St) is <addEl>()
     * } in iterate(_otherWise(LHS,RHS),extract,<initState>)
     */

    List<IStatement> defs = new ArrayList<IStatement>();

    Location loc = other.getLoc();

    List<Variable> cDefined = new ArrayList<Variable>();
    List<Variable> cReq = new ArrayList<Variable>();

    // Find the variables that are defined by the condition
    FlowAnalysis.analyseCondition(other, definedVars, cDefined, cReq, VarAnalysis.findDefinedVars(other,
        new DictionaryChecker(cxt, definedVars)));

    ConstructorTerm tmpAnswer = new ConstructorTerm(loc, cDefined.toArray(new IContentExpression[cDefined.size()]));
    ConstructorPtn tmpPttrn = new ConstructorPtn(loc, cDefined.toArray(new IContentPattern[cDefined.size()]));

    cDefined = ListUtils.mergeLists(cDefined, definedVars);

    IType ansType = tmpAnswer.getType();
    IType tmpType = TypeUtils.relationType(ansType);
    IncrementState increment = new IncrementState(tmpAnswer, tmpType);

    IType tmpStateType = TypeUtils.iterstateType(tmpType);
    IType createType = TypeUtils.functionType(tmpType);
    IType tmpStateFunType = TypeUtils.functionType(tmpStateType);

    IType sequenceType = TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.determinedContractType(
        StandardNames.SEQUENCE, tmpType, ansType)), createType);

    IContentExpression nil = Application.apply(loc, tmpType, new MethodVariable(loc, StandardNames.NIL, createType,
        StandardNames.SEQUENCE, sequenceType));
    final IContentExpression initial = CompilerUtils.continueWith(loc, nil);

    IContentExpression otherLhs = transformConstraint(other.getLhs(), definedVars, increment, initial, cxt, outer,
        errors);
    IContentExpression otherRhs = transformConstraint(other.getRhs(), cDefined, increment, initial, cxt, outer, errors);

    // create rhs function
    String rhsName = GenSym.genSym("_R");
    Triple<IContentPattern[], ICondition, IContentExpression> eqn = Triple.create(new IContentPattern[] {},
        CompilerUtils.truth, otherRhs);

    Variable[] rhsFree = FreeVariables.findFreeVars(otherRhs, cxt);

    FunctionLiteral rhsFun = (FunctionLiteral) Over.resolve(cxt, errors, MatchCompiler.generateFunction(null,
        eqn, tmpStateFunType, rhsFree, rhsName, loc, cxt, outer, errors));

    Variable rhsVar = Variable.create(loc, tmpStateFunType, rhsName);

    IStatement rhsDef = VarEntry.createVarEntry(loc, rhsVar, rhsFun, AccessMode.readOnly, Visibility.priVate);
    defs.add(rhsDef);

    // Create the extraction search function
    String extName = GenSym.genSym("_E");
    IType stateType = initState.getType();
    IType resltType = TypeUtils.getTypeArg(stateType, 0);
    Variable stVar = Variable.create(loc, resltType, GenSym.genSym("sT"));
    IContentPattern statePtn = CompilerUtils.continuePtn(loc, stVar);

    IContentExpression succState = succAdd.addToFront(CompilerUtils.continueWith(loc, stVar));
    IType extType = TypeUtils.functionType(tmpPttrn.getType(), stateType, succState.getType());

    Triple<IContentPattern[], ICondition, IContentExpression> extEqn = Triple.create(new IContentPattern[] { tmpPttrn,
        statePtn }, CompilerUtils.truth, succState);

    Variable[] extFree = FreeVariables.findFreeVars(succState, cxt);

    List<Triple<IContentPattern[], ICondition, IContentExpression>> extEqns = new ArrayList<Triple<IContentPattern[], ICondition, IContentExpression>>();
    extEqns.add(extEqn);
    Variable defVar = Variable.create(loc, statePtn.getType(), GenSym.genSym("sT"));

    extEqns.add(Triple.create(new IContentPattern[] { Variable.anonymous(loc, tmpPttrn.getType()), defVar },
        CompilerUtils.truth, (IContentExpression) defVar));

    FunctionLiteral extFun = (FunctionLiteral) Over.resolve(cxt, errors, MatchCompiler.generateFunction(extEqns,
        null, extType, extFree, extName, loc, cxt, outer, errors));

    Variable extVar = Variable.create(loc, extType, extName);

    IStatement extDef = VarEntry.createVarEntry(loc, extVar, extFun, AccessMode.readOnly, Visibility.priVate);
    defs.add(extDef);

    // The _otherwise call
    IType otherFunType = TypeUtils.functionType(tmpStateType, TypeUtils.functionType(tmpStateType), tmpStateType);
    IContentExpression otherSelVar = TypeChecker.typeOfName(loc, StandardNames.OTHER, otherFunType, outer, errors);
    IContentExpression otherSelCall = Application.apply(loc, stateType, otherSelVar, otherLhs, rhsVar);

    IContentExpression checkIter = TypeChecker.typeOfName(loc, StandardNames.CHECKITERFUNC, new TypeVar(), outer,
        errors);

    // unwrap the result of the left join
    IContentExpression unwrap = Application.apply(loc, tmpType, checkIter, otherSelCall, new MethodVariable(loc,
        StandardNames.NIL, createType, StandardNames.SEQUENCE, sequenceType));

    // The outermost iterate call
    IType outerIterType = TypeUtils.functionType(tmpType, extType, initState.getType(), succState.getType());

    IType itConType = TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.typeExp(TypeContracts
        .contractImplTypeName(StandardNames.ITERABLE), tmpType, TypeUtils.determinedType(ansType))), outerIterType);
    Variable outerIter = new MethodVariable(loc, StandardNames.ITERATE, outerIterType, StandardNames.ITERABLE,
        itConType);

    IContentExpression outerCall = Application.apply(loc, stateType, outerIter, unwrap, extVar, initState);
    return new LetTerm(loc, outerCall, defs);
  }

  private static IContentExpression conditionalQuery(ConditionCondition query, List<Variable> definedVars,
      WrapState succAdd, IContentExpression initState, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    final Location loc = query.getLoc();
    final IType stType = initState.getType();

    WrapState trueVal = new WrapState() {

      @Override
      public IContentExpression addToFront(IContentExpression state)
      {
        return CompilerUtils.noMore(loc, CompilerUtils.trueLiteral(loc));
      }
    };

    IContentExpression test = transformConstraint(query.getTest(), definedVars, trueVal, CompilerUtils.continueWith(
        loc, CompilerUtils.falseLiteral(loc)), cxt, outer, errors);

    String tstFunName = GenSym.genSym("_F");
    IType tstFunType = TypeUtils.functionType(stType);

    Variable[] failFree = FreeVariables.findFreeVars(initState, cxt);
    FunctionLiteral negFun = new FunctionLiteral(loc, tstFunName, tstFunType, new IContentPattern[] {},
        transformConstraint(query.getRhs(), definedVars, succAdd, initState, cxt, outer, errors), failFree);
    Variable negVar = Variable.create(loc, tstFunType, tstFunName);

    IStatement negDef = VarEntry.createVarEntry(loc, negVar, negFun, AccessMode.readOnly, Visibility.priVate);

    IContentExpression nF = new LetTerm(loc, negVar, negDef);

    String sucFunName = GenSym.genSym("_T");
    IContentExpression succ = transformConstraint(query.getLhs(), definedVars, succAdd, initState, cxt, outer, errors);
    Variable[] succFree = FreeVariables.findFreeVars(succ, cxt);
    FunctionLiteral succFn = new FunctionLiteral(loc, sucFunName, tstFunType, new IContentPattern[] {}, succ, succFree);

    Variable succVar = Variable.create(loc, tstFunType, sucFunName);

    IStatement sucDef = VarEntry.createVarEntry(loc, succVar, succFn, AccessMode.readOnly, Visibility.priVate);

    IContentExpression sF = new LetTerm(loc, succVar, sucDef);

    IContentExpression negate = TypeChecker.typeOfName(loc, StandardNames.NEGATE, new TypeVar(), cxt, errors);

    return Application.apply(loc, stType, negate, test, sF, nF);
  }

  private static IContentExpression isTrueQuery(IsTrue query, List<Variable> definedVars, WrapState succAdd,
      IContentExpression initState, Dictionary cxt, ErrorReport errors)
  {
    Location loc = query.getLoc();
    IContentExpression lhs = succAdd.addToFront(initState);
    IContentExpression rhs = initState;
    return new ConditionalExp(loc, lhs.getType(), query, lhs, rhs);
  }

  private static IContentExpression matchesQuery(Matches query, List<Variable> definedVars, WrapState succAdd,
      IContentExpression initState, Dictionary cxt, ErrorReport errors)
  {
    Location loc = query.getLoc();
    IContentExpression lhs = succAdd.addToFront(initState);
    IContentExpression rhs = initState;
    return new ConditionalExp(loc, lhs.getType(), query, lhs, rhs);
  }

  private static IContentExpression conjunctiveQuery(final Conjunction conj, List<Variable> definedVars,
      final WrapState succAdd, final IContentExpression initState, final Dictionary cxt, final Dictionary outer,
      final ErrorReport errors)
  {
    final List<Variable> lhsDefined = new ArrayList<Variable>(definedVars);
    List<Variable> lhsRequired = new ArrayList<Variable>();
    List<Variable> lhsCandidates = new ArrayList<Variable>();

    ICondition lhs = conj.getLhs();

    if (lhs instanceof Search) // special case when lead is a search condition
      return searchQuery((Search) lhs, conj.getRhs(), definedVars, succAdd, initState, cxt, outer, errors);
    else {
      FlowAnalysis.analyseCondition(lhs, lhsDefined, definedVars, lhsRequired, lhsCandidates);

      WrapState lhsFront = new WrapState() {
        @Override
        public IContentExpression addToFront(IContentExpression state)
        {
          return transformConstraint(conj.getRhs(), lhsDefined, succAdd, state, cxt, outer, errors);
        }
      };

      return transformConstraint(lhs, definedVars, lhsFront, initState, cxt, outer, errors);
    }
  }

  private static IContentExpression disjunctiveQuery(Disjunction disj, List<Variable> definedVars, WrapState succAdd,
      IContentExpression initState, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    ICondition lhs = disj.getLhs();
    ICondition rhs = disj.getRhs();
    Location loc = disj.getLoc();

    if (lhs instanceof IsTrue) {
      if (rhs instanceof IsTrue) {
        IContentExpression newState = succAdd.addToFront(initState);

        if (newState instanceof ConditionalExp) {
          ConditionalExp newCond = (ConditionalExp) newState;
          return new ConditionalExp(loc, newState.getType(), new Conjunction(loc, new Disjunction(loc, lhs, rhs),
              newCond.getCnd()), newCond.getThExp(), newCond.getElExp());
        }

        return new ConditionalExp(loc, newState.getType(), new Disjunction(loc, lhs, rhs), newState, initState);
      } else {
        IContentExpression newLhs = succAdd.addToFront(initState);
        IContentExpression newRhs = transformConstraint(rhs, definedVars, succAdd, initState, cxt, outer, errors);
        return new ConditionalExp(loc, newLhs.getType(), lhs, newLhs, newRhs);
      }
    } else {
      IContentExpression leftState = transformConstraint(lhs, definedVars, succAdd, initState, cxt, outer, errors);
      return transformConstraint(rhs, definedVars, succAdd, leftState, cxt, outer, errors);
    }
  }

  private static IContentExpression searchQuery(Search search, ICondition rhs, List<Variable> definedVars,
      WrapState succAdd, IContentExpression initState, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    Location loc = search.getLoc();

    IContentPattern prdPtn = search.getPtn();
    IContentExpression source = search.getSource();
    IType sourceType = source.getType();

    String sfName = GenSym.genSym("sF");
    IType stateType = initState.getType();

    assert TypeUtils.isIterstateType(stateType);

    Wrapper<ICondition> cond = new Wrapper<ICondition>(CompilerUtils.truth);
    final List<Variable> lhsDefined = new ArrayList<Variable>(definedVars);
    List<Variable> lhsRequired = new ArrayList<Variable>();

    FlowAnalysis.analyseCondition(search, lhsDefined, lhsDefined, lhsRequired, VarAnalysis.findDefinedVars(search,
        new DictionaryChecker(cxt, lhsDefined)));

    rhs = QueryPlanner.pullupEqualities(rhs, cond, lhsDefined);
    // Pair<IContentExpression, IContentExpression> keys = Indexing.pullOutIndexTerms(prdPtn,
    // CompilerUtils.truth,
    // definedVars);

    /**
     * Build the function that is equivalent to:
     * 
     * <pre>
     * let{
     *   sF(Ptn,St) is addElement(X,St);
     *   sF(_,S) default is S;
     * } in sF
     * </pre>
     * 
     * May be more complex in the case that the rhs is more complex.
     */

    Variable stVar = Variable.create(loc, stateType, GenSym.genSym("sT"));
    IType prType = prdPtn.getType();
    IType stFunType = TypeUtils.functionType(prType, stateType, stateType);

    if (prdPtn instanceof WherePattern) {
      WherePattern where = (WherePattern) prdPtn;
      CompilerUtils.extendCondition(cond, where.getCond());
      prdPtn = where.getPtn();
    }

    List<Triple<IContentPattern[], ICondition, IContentExpression>> eqns = new ArrayList<Triple<IContentPattern[], ICondition, IContentExpression>>();

    IContentExpression succ = rhs != null ? transformConstraint(rhs, lhsDefined, succAdd, stVar, cxt, outer, errors)
        : succAdd.addToFront(stVar);

    eqns.add(Triple.create(new IContentPattern[] { shiftFreeVars(prdPtn, cond, outer), stVar }, cond.get(), succ));
    eqns.add(Triple.create(new IContentPattern[] { Variable.anonymous(loc, prType), stVar }, CompilerUtils.truth,
        (IContentExpression) stVar));

    Variable sFvar = Variable.create(loc, stFunType, sfName);

    Variable[] freeVars = new Variable[] {};
    FunctionLiteral stFun = (FunctionLiteral) Over.resolve(cxt, errors, MatchCompiler.generateFunction(eqns,
        null, stFunType, freeVars, sfName, loc, cxt, outer, errors));
    IStatement stDef = VarEntry.createVarEntry(loc, sFvar, stFun, AccessMode.readOnly, Visibility.priVate);

    IContentExpression sF = new LetTerm(loc, sFvar, stDef);
    IType itType = TypeUtils.functionType(sourceType, sF.getType(), stateType, stateType);
    IType itConType = TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.determinedContractType(
        StandardNames.ITERABLE, sourceType, prType)), itType);
    Variable iterVar = new MethodVariable(loc, StandardNames.ITERATE, itType, StandardNames.ITERABLE, itConType);

    return Application.apply(loc, stateType, iterVar, source, sF, initState);
  }

  private static IContentExpression indexingSearchQuery(ListSearch search, ICondition rhs, List<Variable> definedVars,
      WrapState succAdd, IContentExpression initState, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    Location loc = search.getLoc();

    IContentPattern prdPtn = search.getPtn();
    IContentPattern ixPtn = search.getIx();
    IContentExpression source = search.getSource();
    IType sourceType = source.getType();

    String sfName = GenSym.genSym("sF");
    IType stateType = initState.getType();

    assert TypeUtils.isIterstateType(stateType);

    Wrapper<ICondition> cond = new Wrapper<ICondition>(CompilerUtils.truth);
    final List<Variable> lhsDefined = new ArrayList<Variable>(definedVars);
    List<Variable> lhsRequired = new ArrayList<Variable>();

    FlowAnalysis.analyseCondition(search, lhsDefined, lhsDefined, lhsRequired, VarAnalysis.findDefinedVars(search,
        new DictionaryChecker(cxt, lhsDefined)));

    rhs = QueryPlanner.pullupEqualities(rhs, cond, lhsDefined);

    /**
     * Build the function that is equivalent to:
     * 
     * <pre>
     * let{
     *   sF(Ptn,IxPtn, St) is addElement(X,St);
     *   sF(_,_, S) default is S;
     * } in sF
     * </pre>
     * 
     * May be more complex in the case that the rhs is more complex.
     */

    Variable stVar = Variable.create(loc, stateType, GenSym.genSym("sT"));
    IType prType = prdPtn.getType();
    IType ixType = ixPtn.getType();
    IType stFunType = TypeUtils.functionType(ixType, prType, stateType, stateType);

    if (prdPtn instanceof WherePattern) {
      WherePattern where = (WherePattern) prdPtn;
      CompilerUtils.extendCondition(cond, where.getCond());
      prdPtn = where.getPtn();
    }

    List<Triple<IContentPattern[], ICondition, IContentExpression>> eqns = new ArrayList<Triple<IContentPattern[], ICondition, IContentExpression>>();

    IContentExpression succ = rhs != null ? transformConstraint(rhs, lhsDefined, succAdd, stVar, cxt, outer, errors)
        : succAdd.addToFront(stVar);

    eqns.add(Triple.create(new IContentPattern[] { shiftFreeVars(ixPtn, cond, outer),
        shiftFreeVars(prdPtn, cond, outer), stVar }, cond.get(), succ));
    eqns.add(Triple.create(new IContentPattern[] { Variable.anonymous(loc, ixType), Variable.anonymous(loc, prType),
        stVar }, CompilerUtils.truth, (IContentExpression) stVar));

    Variable sFvar = Variable.create(loc, stFunType, sfName);

    Variable[] freeVars = new Variable[] {};
    FunctionLiteral stFun = (FunctionLiteral) Over.resolve(cxt, errors, MatchCompiler.generateFunction(eqns,
        null, stFunType, freeVars, sfName, loc, cxt, outer, errors));
    IStatement stDef = VarEntry.createVarEntry(loc, sFvar, stFun, AccessMode.readOnly, Visibility.priVate);

    IContentExpression sF = new LetTerm(loc, sFvar, stDef);

    // Set up the call to iterate
    IType itType = TypeUtils.functionType(sourceType, sF.getType(), stateType, stateType);
    IType itConType = TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.determinedContractType(
        StandardNames.IXITERABLE, sourceType, ixType, prType)), itType);
    Variable iterVar = new MethodVariable(loc, StandardNames.IXITERATE, itType, StandardNames.IXITERABLE, itConType);

    return Application.apply(loc, stateType, iterVar, source, sF, initState);
  }

  // The rules for free variables in equations are different than for normal patterns. Implement the
  // shift
  private static IContentPattern shiftFreeVars(IContentPattern ptn, final Wrapper<ICondition> cond,
      final Dictionary dict)
  {
    ExpressionTransformer conditioner = new ExpressionTransformer(dict) {
      {
        install(new VariableFreePtnTransform());
      }

      class VariableFreePtnTransform implements TransformPattern
      {
        @Override
        public Class<? extends IContentPattern> transformClass()
        {
          return Variable.class;
        }

        @Override
        public IContentPattern transformPtn(IContentPattern ptn)
        {
          Variable var = (Variable) ptn;
          if (dict.isDefinedVar(var.getName())) {
            Location loc = var.getLoc();
            Variable newVar = var.copy();
            CompilerUtils.extendCondition(cond, CompilerUtils.equals(loc, newVar, var));
            return newVar;
          } else
            return ptn;
        }
      }
    };
    return conditioner.transform(ptn);
  }

  public interface WrapState
  {
    IContentExpression addToFront(IContentExpression state);
  }

  public static class PassThrough implements WrapState
  {

    @Override
    public IContentExpression addToFront(IContentExpression state)
    {
      return state;
    }
  }

  public static class IncrementState implements WrapState
  {
    private final IContentExpression boundExp;
    private final IType resltType;

    public IncrementState(IContentExpression boundExp, IType resltType)
    {
      this.boundExp = boundExp;
      this.resltType = resltType;
    }

    @Override
    public IContentExpression addToFront(IContentExpression state)
    {
      Location loc = boundExp.getLoc();
      Variable stVar = Variable.create(loc, resltType, GenSym.genSym("st"));
      IContentAction decl = new VarDeclaration(loc, CompilerUtils.continuePtn(loc, stVar), AccessMode.readOnly, state);
      IType consType = TypeUtils.functionType(resltType, boundExp.getType(), resltType);
      IType sequenceType = TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.determinedContractType(
          StandardNames.SEQUENCE, resltType, boundExp.getType())), consType);

      Variable cons = new MethodVariable(loc, StandardNames.APND, consType, StandardNames.SEQUENCE, sequenceType);
      IContentExpression consed = Application.apply(loc, resltType, cons, stVar, boundExp);
      IContentAction valis = new ValisAction(loc, CompilerUtils.continueWith(loc, consed));
      return new ValofExp(loc, state.getType(), decl, valis);
    }
  }

  public static class CountingState implements WrapState
  {
    private final IContentExpression boundExp;
    private final IContentExpression limit;
    private final IType resType;

    public CountingState(IContentExpression boundExp, IType resltType, IContentExpression limit)
    {
      assert boundExp != null && resltType != null && limit != null;
      this.boundExp = boundExp;
      this.resType = resltType;
      this.limit = limit;
    }

    @Override
    public IContentExpression addToFront(IContentExpression state)
    {
      Location loc = boundExp.getLoc();
      IType integerType = StandardTypes.integerType;

      Variable cntVar = new Variable(loc, integerType, GenSym.genSym("cnt"));
      Variable nxtVar = new Variable(loc, integerType, GenSym.genSym("cnt"));
      Variable stVar = Variable.create(loc, resType, GenSym.genSym("st"));
      ConstructorPtn statePtn = new ConstructorPtn(loc, stVar, cntVar);
      Variable tplVar = Variable.create(loc, statePtn.getType(), GenSym.genSym("tpl"));

      IContentExpression one = CompilerUtils.integerLiteral(loc, 1);

      IContentAction decl = new VarDeclaration(loc, CompilerUtils.continuePtn(loc, tplVar), AccessMode.readOnly, state);
      IContentAction tplDecl = new VarDeclaration(loc, statePtn, AccessMode.readOnly, tplVar);

      IType consType = TypeUtils.functionType(boundExp.getType(), resType, resType);
      IType sequenceType = TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.determinedContractType(
          StandardNames.SEQUENCE, resType, boundExp.getType())), consType);

      Variable cons = new MethodVariable(loc, StandardNames.ADD_TO_FRONT, consType, StandardNames.SEQUENCE,
          sequenceType);
      IContentExpression consed = Application.apply(loc, resType, cons, boundExp, stVar);

      IType arithContract = TypeUtils.typeExp(StandardNames.ARITHMETIC, integerType);
      IType plusType = TypeUtils.functionType(integerType, integerType, integerType);
      IType plusContractType = TypeUtils.overloadedType(TypeUtils.tupleType(arithContract), plusType);

      IContentAction nxt = new VarDeclaration(loc, nxtVar, AccessMode.readOnly, Application.apply(loc, integerType,
          new MethodVariable(loc, StandardNames.PLUS, plusType, StandardNames.ARITHMETIC, plusContractType), cntVar,
          one));

      ICondition test = CompilerUtils.greaterEquals(loc, nxtVar, limit);
      IContentExpression nxtTpl = new ConstructorTerm(loc, consed, nxtVar);
      Variable nxtStVar = Variable.create(loc, nxtTpl.getType(), GenSym.genSym("st"));
      IContentAction nxtDecl = new VarDeclaration(loc, nxtStVar, AccessMode.readOnly, nxtTpl);

      IType mType = TypeUtils.iterstateType(nxtStVar.getType());

      IContentAction cont = new ValisAction(loc, CompilerUtils.continueWith(loc, nxtStVar));
      IContentAction done = new ValisAction(loc, CompilerUtils.noMore(loc, nxtStVar));

      IContentAction result = new ConditionalAction(loc, test, done, cont);

      return new ValofExp(loc, state.getType(), new Sequence(loc, mType, FixedList.create(decl, tplDecl, nxt, nxtDecl,
          result)));
    }
  }

  // Used in a reduction query.
  public static class ReduceState implements WrapState
  {
    private final IContentExpression boundExp;
    private final IContentExpression reducer;
    private final IType resType;
    private final Dictionary dict;
    private final Dictionary outer;
    private final ErrorReport errors;

    public ReduceState(IContentExpression boundExp, IType resltType, IContentExpression reducer, Dictionary dict,
        Dictionary outer, ErrorReport errors)
    {
      assert boundExp != null && resltType != null && reducer != null;
      this.boundExp = boundExp;
      this.resType = resltType;
      this.reducer = reducer;
      this.dict = dict;
      this.outer = outer;
      this.errors = errors;
    }

    @Override
    public IContentExpression addToFront(IContentExpression state)
    {
      Location loc = boundExp.getLoc();

      // set up a case expression:
      // case St in {
      // NoneFound is ContinueWith(boundExp)
      // ContinueWith(O) is ContinueWith(reduce(boundExp,O))
      // }

      IContentPattern noneFound = CompilerUtils.noneFoundPtn(loc, resType);
      IContentExpression arm1 = CompilerUtils.continueWith(loc, boundExp);

      Variable nxtVar = new Variable(loc, resType, GenSym.genSym("O"));
      IContentPattern cont = CompilerUtils.continuePtn(loc, nxtVar);
      IContentExpression arm2 = CompilerUtils.continueWith(loc, Application.apply(loc, resType, reducer, boundExp,
          nxtVar));

      List<Pair<IContentPattern, IContentExpression>> cases = new ArrayList<>();

      cases.add(Pair.pair(noneFound, arm1));
      cases.add(Pair.pair(cont, arm2));

      Pair<IContentPattern, IContentExpression> deflt = Pair.pair((IContentPattern) Variable.anonymous(loc, state
          .getType()), state);

      return MatchCompiler.generateCaseExpression(loc, state, cases, deflt, resType, dict, outer, errors);
    }
  }

  public static class SetState implements WrapState
  {
    private final IContentExpression boundExp;

    public SetState(IContentExpression boundExp)
    {
      this.boundExp = boundExp;
    }

    @Override
    public IContentExpression addToFront(IContentExpression state)
    {
      Location loc = boundExp.getLoc();
      return CompilerUtils.noMore(loc, boundExp);
    }
  }

  public static class ActionState implements WrapState
  {
    private final IContentAction body;

    public ActionState(IContentAction body)
    {
      this.body = body;
    }

    @Override
    public IContentExpression addToFront(IContentExpression state)
    {
      List<IContentAction> acts = new ArrayList<IContentAction>();
      acts.add(body);
      Location loc = state.getLoc();
      acts.add(new ValisAction(loc, state));
      return new ValofExp(loc, state.getType(), acts);
    }
  }
}
