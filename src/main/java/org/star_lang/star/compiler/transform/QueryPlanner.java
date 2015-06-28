package org.star_lang.star.compiler.transform;

import java.util.List;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.FreeVariables;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.ConditionalAction;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.ConstructorPtn;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.Implies;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.ListSearch;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.MemoExp;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.Negation;
import org.star_lang.star.compiler.canonical.Otherwise;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.transform.ConditionTransformer.ActionState;
import org.star_lang.star.compiler.transform.ConditionTransformer.CountingState;
import org.star_lang.star.compiler.transform.ConditionTransformer.IncrementState;
import org.star_lang.star.compiler.transform.ConditionTransformer.ReduceState;
import org.star_lang.star.compiler.transform.ConditionTransformer.SetState;
import org.star_lang.star.compiler.transform.ConditionTransformer.WrapState;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.TypeChecker;
import org.star_lang.star.compiler.type.TypeContracts;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

/**
 * Implement query transformation into nested for-loops Based on Keith's set macros from the glory
 * days of April
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
public class QueryPlanner
{
  public static IContentExpression transformQueryNxt(final Location loc, List<Variable> definedVars,
      IContentExpression countExp, final IContentExpression boundExp, final IType type, ICondition query,
      Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    final IContentExpression zero = CompilerUtils.integerLiteral(loc, 0);
    IType createType = TypeUtils.functionType(type);
    IType sequenceType = TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.typeExp(TypeContracts
        .contractImplTypeName(StandardNames.SEQUENCE), type, TypeUtils.determinedType(boundExp.getType()))),
        createType);

    IContentExpression nil = Application.apply(loc, type, new MethodVariable(loc, StandardNames.NIL, createType,
        StandardNames.SEQUENCE, sequenceType));
    IContentExpression nilTpl = new ConstructorTerm(loc, nil, zero);
    IContentExpression initState = CompilerUtils.continueWith(loc, nilTpl);

    WrapState succAdd = new CountingState(boundExp, type, countExp);

    IContentExpression txedCon = ConditionTransformer.transformConstraint(query, definedVars, succAdd, initState, cxt,
        outer, errors);

    IContentExpression checker = checkIterFun(loc, type, cxt, errors);

    IContentExpression finalState = Application.apply(loc, nilTpl.getType(), checker, txedCon, new MemoExp(loc, nilTpl,
        new Variable[] {}));

    IContentExpression project = new Variable(loc, TypeUtils.functionType(nilTpl.getType(), type, type),
        "_project_0_2");
    return Application.apply(loc, type, project, finalState);
  }

  public static IContentExpression transformQuery(Location loc, List<Variable> definedVars, IContentExpression boundExp,
      IType type, ICondition query, Dictionary dict, Dictionary outer, ErrorReport errors)
  {
    IType createType = TypeUtils.functionType(type);

    IType sequenceType = TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.typeExp(TypeContracts
        .contractImplTypeName(StandardNames.SEQUENCE), type, TypeUtils.determinedType(boundExp.getType()))),
        createType);

    IContentExpression nil = Application.apply(loc, type, new MethodVariable(loc, StandardNames.NIL, createType,
        StandardNames.SEQUENCE, sequenceType));
    final IContentExpression initState = CompilerUtils.continueWith(loc, nil);

    WrapState succAdd = new IncrementState(boundExp, type);

    IContentExpression txedCon = ConditionTransformer.transformConstraint(query, definedVars, succAdd, initState, dict,
        outer, errors);

    IContentExpression checker = checkIterFun(loc, type, dict, errors);

    return Application.apply(loc, type, checker, txedCon, new MethodVariable(loc, StandardNames.NIL, createType,
        StandardNames.SEQUENCE, sequenceType));
  }

  /**
   * Handle the plan construction for a query expression of the form anyof X where C
   * 
   * @param loc
   * @param definedVars
   * @param bound
   * @param expectedType
   * @param query
   * @param queryCxt
   * @param outer
   * @param errors
   * @return an executable expression
   */
  public static IContentExpression transformReferenceExpression(Location loc, List<Variable> definedVars,
      IContentExpression bound, IType expectedType, ICondition query, Dictionary queryCxt, Dictionary outer,
      ErrorReport errors)
  {
    IType resType = bound.getType();
    IContentExpression initState = CompilerUtils.noneFound(loc, resType);

    WrapState succAdd = new SetState(bound);

    IContentExpression txedCon = ConditionTransformer.transformConstraint(query, definedVars, succAdd, initState,
        queryCxt, outer, errors);

    IContentExpression checker = optionIterFun(loc, resType, queryCxt, errors);

    return Application.apply(loc, expectedType, checker, txedCon);
  }

  public static IContentExpression transformReferenceExpression(ICondition query, List<Variable> definedVars,
      Dictionary cxt, Dictionary outer, IContentExpression bound, IContentExpression deflt, IType expectedType,
      Location loc, ErrorReport errors)
  {
    IType resType = bound.getType();
    IContentExpression initState = CompilerUtils.noneFound(loc, resType);

    WrapState succAdd = new SetState(bound);

    IContentExpression txedCon = ConditionTransformer.transformConstraint(query, definedVars, succAdd, initState, cxt,
        outer, errors);

    Variable[] freeVars = FreeVariables.findFreeVars(deflt, cxt);

    IContentExpression checker = checkIterFun(loc, expectedType, cxt, errors);

    return Application.apply(loc, expectedType, checker, txedCon, new MemoExp(loc, deflt, freeVars));
  }

  public static IContentExpression transformReduction(IContentExpression reducer, IContentExpression bound,
      ICondition query, IContentExpression deflt, List<Variable> definedVars, Dictionary cxt, Dictionary outer,
      IType expectedType, Location loc, ErrorReport errors)
  {
    IContentExpression initState = CompilerUtils.noneFound(loc, expectedType);

    WrapState succAdd = new ReduceState(bound, expectedType, reducer, cxt, outer, errors);

    IContentExpression txedCon = ConditionTransformer.transformConstraint(query, definedVars, succAdd, initState, cxt,
        outer, errors);

    Variable[] freeVars = FreeVariables.findFreeVars(reducer, cxt);

    IContentExpression checker = checkIterFun(loc, expectedType, cxt, errors);

    return Application.apply(loc, expectedType, checker, txedCon, new MemoExp(loc, deflt, freeVars));
  }

  public static IContentAction transformConditionNxt(Location loc, List<Variable> defined, ICondition cond,
      IContentAction body, IContentAction other, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    if (!defined.isEmpty()) {
      IContentExpression defExps[] = defined.toArray(new IContentExpression[defined.size()]);
      IContentExpression defTpl = ConstructorTerm.tuple(loc, defExps);

      IType resltType = TypeUtils.typeExp(StandardNames.POSSIBLE, defTpl.getType());
      IContentExpression reslt = CompilerUtils.possible(loc, defTpl);
      IContentExpression deflt = CompilerUtils.impossible(loc);

      IContentExpression test = transformReferenceExpression(cond, defined, cxt, outer, reslt, deflt, resltType, loc,
          errors);

      ICondition matches = new Matches(loc, test, CompilerUtils.possiblePtn(loc, resltType, ConstructorPtn.tuplePtn(loc,
          defined.toArray(new IContentPattern[defined.size()]))));
      return new ConditionalAction(loc, matches, body, other);
    } else {
      IContentExpression ok = CompilerUtils.trueLiteral(loc);
      IContentExpression notOk = CompilerUtils.falseLiteral(loc);
      IContentExpression test = QueryPlanner.transformReferenceExpression(cond, defined, cxt, outer, ok, notOk,
          StandardTypes.booleanType, loc, errors);
      return new ConditionalAction(loc, new IsTrue(loc, test), body, other);
    }
  }

  public static IContentExpression transformCondition(ICondition cond, List<Variable> definedVars,
      IContentExpression answer, IContentExpression otherwise, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    Location loc = cond.getLoc();
    IType type = answer.getType();

    IContentExpression initState = CompilerUtils.noneFound(loc, type);

    WrapState succAdd = new SetState(answer);

    IContentExpression txedCon = ConditionTransformer.transformConstraint(cond, definedVars, succAdd, initState, cxt,
        outer, errors);

    Variable[] freeVars = FreeVariables.findFreeVars(otherwise, cxt);

    IContentExpression checker = checkIterFun(loc, type, cxt, errors);
    return Application.apply(loc, type, checker, txedCon, new MemoExp(loc, otherwise, freeVars));
  }

  private static IContentExpression optionIterFun(Location loc, IType type, Dictionary cxt, ErrorReport errors)
  {
    IType checkerType = TypeUtils.functionType(TypeUtils.iterstateType(type), TypeUtils.optionType(type));
    return TypeChecker.typeOfName(loc, StandardNames.OPTIONITERSTATE, checkerType, cxt, errors);
  }

  private static IContentExpression checkIterFun(Location loc, IType type, Dictionary cxt, ErrorReport errors)
  {
    IType checkerType = TypeUtils.functionType(TypeUtils.iterstateType(type), TypeUtils.functionType(type), type);
    return TypeChecker.typeOfName(loc, StandardNames.CHECKITERFUNC, checkerType, cxt, errors);
  }

  static ICondition pullupEqualities(ICondition query, Wrapper<ICondition> pulledEqualities, List<Variable> definedVars)
  {
    if (CompilerUtils.isEquality(query)) {
      if (VarAnalysis.allDefined(CompilerUtils.equalityLhs(query), definedVars) && VarAnalysis.allDefined(CompilerUtils
          .equalityRhs(query), definedVars)) {
        CompilerUtils.extendCondition(pulledEqualities, query);

        return null;
      } else
        return query;
    } else if (query instanceof Matches) {
      Matches match = (Matches) query;
      if (VarAnalysis.allDefined(match.getExp(), definedVars)) {
        CompilerUtils.extendCondition(pulledEqualities, match);
        return null;
      } else
        return query;
    } else if (query instanceof Conjunction) {
      Conjunction conj = (Conjunction) query;
      ICondition lhs = pullupEqualities(conj.getLhs(), pulledEqualities, definedVars);
      ICondition rhs = pullupEqualities(conj.getRhs(), pulledEqualities, definedVars);
      if (lhs == null)
        return rhs;
      else if (rhs == null)
        return lhs;
      else
        return new Conjunction(conj.getLoc(), lhs, rhs);
    } else
      return query;
  }

  public static boolean isTransformable(ICondition query)
  {
    if (query instanceof Search || query instanceof ListSearch)
      return true;
    else if (query instanceof Negation)
      return isTransformable(((Negation) query).getNegated());
    else if (query instanceof Conjunction)
      return isTransformable(((Conjunction) query).getLhs()) || isTransformable(((Conjunction) query).getRhs());
    else if (query instanceof Disjunction)
      return isTransformable(((Disjunction) query).getLhs()) || isTransformable(((Disjunction) query).getRhs());
    else if (query instanceof Otherwise)
      return isTransformable(((Otherwise) query).getLhs()) || isTransformable(((Otherwise) query).getRhs());
    else if (query instanceof Implies)
      return true;
    else
      return false;
  }

  public static IContentExpression transformForLoop(Location loc, List<Variable> definedVars, ICondition cond,
      IContentAction body, IType stType, IType returnType, IType mType, Dictionary cxt, Dictionary outer,
      ErrorReport errors)
  {
    IContentExpression initState = CompilerUtils.noneFound(loc, stType);

    IContentAction transAction = ActionTransformer.transformValis(body, returnType, cxt, errors);
    WrapState succAdd = new ActionState(transAction);

    return ConditionTransformer.transformConstraint(cond, definedVars, succAdd, initState, cxt, outer, errors);
  }
}
