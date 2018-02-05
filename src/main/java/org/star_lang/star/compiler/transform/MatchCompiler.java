package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.StarCompiler;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.transform.VarAnalysis.VarChecker;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DictionaryChecker;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeChecker;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.ConsList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Triple;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.RecordSpecifier;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.value.Factory;

/**
 * The Match Compiler replaces sets of equations with case expressions
 *
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
public class MatchCompiler
{
  private static final int maxDepth = StarCompiler.MATCHDEPTH;

  public static IContentExpression generateCaseExpression(Location loc, IContentExpression selector,
      List<Pair<IContentPattern, IContentExpression>> cases, Pair<IContentPattern, IContentExpression> deflt,
      IType type, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    List<Variable> definedVars = new ArrayList<>();

    ConsList<IContentExpression> newVars = ConsList.nil();
    newVars = newVars.cons(selector);

    Dictionary subCxt = cxt.fork();

    List<MatchTriple<IContentExpression>> list = new ArrayList<>();

    for (Pair<IContentPattern, IContentExpression> entry : cases) {
      IContentPattern ptn = entry.getKey();
      if (ptn instanceof WherePattern) {
        WherePattern where = (WherePattern) ptn;
        list.add(new MatchTriple<>(where.getPtn(), where.getCond(), entry.getValue()));
      } else
        list.add(new MatchTriple<>(ptn, CompilerUtils.truth, entry.getValue()));
    }

    if (deflt != null) {
      IContentPattern defltPtn = deflt.left();
      ICondition defltCond = CompilerUtils.truth;
      if (defltPtn instanceof WherePattern) {
        WherePattern where = (WherePattern) defltPtn;
        defltCond = where.getCond();
        defltPtn = where.getPtn();
      }
      list.add(new MatchTriple<>(defltPtn, defltCond, deflt.right()));
    }

    Generate<IContentExpression> gen = new ExpressionCaseGenerator(type);

    IContentExpression ex = genException(loc, cxt, errors);
    AbortExpression failure = new AbortExpression(loc, new TypeVar(), ex);

    return compileMatch(loc, newVars, list, failure, subCxt, outer, gen, definedVars, maxDepth, errors);
  }

  public static IContentExpression genException(Location loc, Dictionary cxt, ErrorReport errors)
  {
    IContentExpression code = CompilerUtils.stringLiteral(loc, "error");
    IContentExpression raised = new Scalar(loc, StandardTypes.stringType, Factory
        .newString("all available cases failed, at " + loc));
    IContentExpression location = TypeChecker.typeOfName(loc, StandardNames.MACRO_LOCATION, StandardTypes.locationType,
        cxt, errors);

    return new ConstructorTerm(loc, EvaluationException.name, StandardTypes.exceptionType, code,
        raised, location);
  }

  private static class ExpressionCaseGenerator implements Generate<IContentExpression>
  {
    private final IType type;

    public ExpressionCaseGenerator(IType type)
    {
      this.type = type;
    }

    @Override
    public IContentExpression makeCase(Location loc, IContentExpression var,
        List<Pair<IContentPattern, IContentExpression>> cases, IContentExpression deflt)
    {
      assert deflt != null;
      if (cases.isEmpty())
        return deflt;
      else if (cases.size() == 1)
        return new ConditionalExp(loc, type, new Matches(loc, var, cases.get(0).left), cases.get(0).right, deflt);
      else
        return new CaseExpression(loc, type, var, cases, deflt);
    }

    @Override
    public IContentExpression conditionalize(Location loc, ConsList<IContentExpression> args,
        List<MatchTriple<IContentExpression>> eqns, IContentExpression deflt, List<Variable> definedVars,
        Dictionary cxt, Dictionary outer, ErrorReport errors)
    {
      if (eqns.isEmpty())
        return transformQueries(deflt, definedVars, cxt, outer, errors);
      else {
        IContentExpression body = transformQueries(deflt, definedVars, cxt, outer, errors);

        for (int ix = eqns.size(); ix > 0; ix--) {
          MatchTriple<IContentExpression> eq = eqns.get(ix - 1);
          ICondition cond = sortConjunction(mergeMatches(args, eq.args, eq.cond), definedVars, new DictionaryChecker(
              cxt, definedVars));
          if (!CompilerUtils.isTrivial(cond)) {
            if (body == null) {
              IContentExpression eqBody = transformQueries(eq.body, definedVars, cxt, outer, errors);
              IType returnType = eqBody.getType();

              IContentExpression code = CompilerUtils.stringLiteral(loc, "error");
              IContentExpression raised = new Scalar(loc, StandardTypes.stringType, Factory
                  .newString("all available rules failed, at " + loc));
              IContentExpression location = TypeChecker.typeOfName(loc, StandardNames.MACRO_LOCATION,
                  StandardTypes.locationType, cxt, errors);

              IContentExpression ex = new ConstructorTerm(loc, EvaluationException.name, StandardTypes.exceptionType,
                  code, raised, location);

              AbortExpression exit = new AbortExpression(loc, returnType, ex);
              if (QueryPlanner.isTransformable(cond))
                body = QueryPlanner.transformCondition(cond, definedVars, eqBody, exit, cxt, outer, errors);
              else if (!CompilerUtils.isTrivial(cond))
                body = new ConditionalExp(loc, returnType, cond, eqBody, exit);
              else
                body = eqBody;
            } else {
              IContentExpression eqBody = transformQueries(eq.body, definedVars, cxt, outer, errors);
              IType returnType = eqBody.getType();

              if (QueryPlanner.isTransformable(cond))
                body = QueryPlanner.transformCondition(cond, definedVars, eqBody, body, cxt, outer, errors);
              else
                body = new ConditionalExp(loc, returnType, cond, eqBody, body);
            }
          } else
            body = transformQueries(eq.body, definedVars, cxt, outer, errors);
        }
        return body;
      }
    }

    @Override
    public IContentExpression makeMatchTest(Location loc, IContentExpression var, IContentPattern ptn,
        IContentExpression cont, IContentExpression deflt)
    {
      return new ConditionalExp(loc, cont.getType(), new Matches(loc, var, ptn), cont, deflt);
    }
  }

  public static FunctionLiteral generateFunction(
      List<Triple<IContentPattern[], ICondition, IContentExpression>> equations,
      Triple<IContentPattern[], ICondition, IContentExpression> deflt, IType fType, Variable[] freeVars, String name,
      Location loc, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    List<Variable> definedVars = new ArrayList<>();
    Collections.addAll(definedVars, freeVars);

    assert TypeUtils.isFunType(fType);

    IType argTypes[] = TypeUtils.getFunArgTypes(fType);

    int arity = argTypes.length;

    ConsList<IContentExpression> newVars = ConsList.nil();

    Dictionary subCxt = cxt.fork();

    IContentPattern nArgs[] = new IContentPattern[arity];
    for (int ix = arity; ix > 0; ix--) {
      final Variable var = Variable.create(loc, argTypes[ix - 1], GenSym.genSym("_#"));
      newVars = new ConsList<>(var, newVars);
      nArgs[ix - 1] = var;
      definedVars.add(var);
    }

    List<MatchTriple<IContentExpression>> list = new ArrayList<>();
    if (equations != null)
      for (Triple<IContentPattern[], ICondition, IContentExpression> eq : equations)
        list.add(formMatchTriple(eq));

    if (deflt != null)
      list.add(formMatchTriple(deflt));

    Generate<IContentExpression> gen = new ExpressionCaseGenerator(TypeUtils.getFunResultType(fType));

    IContentExpression code = CompilerUtils.stringLiteral(loc, "error");
    IContentExpression raised = new Scalar(loc, StandardTypes.stringType, Factory
        .newString("all available equations failed, at " + loc));
    IContentExpression location = TypeChecker.typeOfName(loc, StandardNames.MACRO_LOCATION, StandardTypes.locationType,
        cxt, errors);

    IContentExpression ex = new ConstructorTerm(loc, EvaluationException.name, StandardTypes.exceptionType, code,
        raised, location);

    AbortExpression failure = new AbortExpression(loc, new TypeVar(), ex);

    IContentExpression nBody = compileMatch(loc, newVars, list, failure, subCxt, outer, gen, definedVars, maxDepth,
        errors);

    return new FunctionLiteral(loc, name, fType, nArgs, nBody, freeVars);
  }

  private static MatchTriple<IContentExpression> formMatchTriple(
      Triple<IContentPattern[], ICondition, IContentExpression> eq)
  {
    return new MatchTriple<>(eq.left(), eq.middle(), eq.right());
  }

  private static class ActionCaseGenerator implements Generate<IContentAction>
  {
    ActionCaseGenerator()
    {
    }

    @Override
    public IContentAction makeCase(Location loc, IContentExpression var,
        List<Pair<IContentPattern, IContentAction>> cases, IContentAction deflt)
    {
      if (cases.isEmpty())
        return deflt;
      else
        return new CaseAction(loc, var, cases, deflt);
    }

    @Override
    public IContentAction makeMatchTest(Location loc, IContentExpression var, IContentPattern ptn, IContentAction cont,
        IContentAction deflt)
    {
      return new ConditionalAction(loc, new Matches(loc, var, ptn), cont, deflt);
    }

    @Override
    public IContentAction conditionalize(Location loc, ConsList<IContentExpression> args,
        List<MatchTriple<IContentAction>> eqns, IContentAction deflt, List<Variable> free, Dictionary cxt,
        Dictionary outer, ErrorReport errors)
    {
      if (eqns.isEmpty())
        return transformQueries(deflt, free, cxt, outer, errors);
      else {
        IContentAction body = transformQueries(deflt, free, cxt, outer, errors);

        for (int ix = eqns.size(); ix > 0; ix--) {
          MatchTriple<IContentAction> eq = eqns.get(ix - 1);
          ICondition cond = mergeMatches(args, eq.args, eq.cond);
          if (!CompilerUtils.isTrivial(cond)) {
            assert body != null;
            if (QueryPlanner.isTransformable(cond)) {
              List<Variable> defined = VarAnalysis.findDefinedVars(cond, new DictionaryChecker(cxt,
                      new ArrayList<>()));

              body = QueryPlanner.transformConditionNxt(loc, defined, cond, eq.body, body, cxt, outer, errors);
            } else
              body = new ConditionalAction(loc, cond, transformQueries(eq.body, free, cxt, outer, errors), body);
          } else
            body = transformQueries(eq.body, free, cxt, outer, errors);
        }
        return body;
      }
    }
  }

  public static IContentAction generateCaseAction(Location loc, IContentExpression selector,
      List<Pair<IContentPattern, IContentAction>> cases, Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    List<Variable> definedVars = new ArrayList<>();

    ConsList<IContentExpression> newVars = ConsList.nil();
    newVars = newVars.cons(selector);

    List<MatchTriple<IContentAction>> list = new ArrayList<>();

    for (Pair<IContentPattern, IContentAction> entry : cases) {
      IContentPattern ptn = entry.getKey();
      if (ptn instanceof WherePattern) {
        WherePattern where = (WherePattern) ptn;
        list.add(new MatchTriple<>(where.getPtn(), where.getCond(), entry.getValue()));
      } else
        list.add(new MatchTriple<>(ptn, CompilerUtils.truth, entry.getValue()));
    }

    Generate<IContentAction> gen = new ActionCaseGenerator();

    IContentExpression ex = genException(loc, cxt, errors);
    AbortAction failure = new AbortAction(loc, ex);

    return compileMatch(loc, newVars, list, failure, cxt, outer, gen, definedVars, maxDepth, errors);
  }

  public static PatternAbstraction compileMatch(Location loc, String name,
      List<Triple<IContentPattern[], ICondition, IContentExpression>> rules, IType pttrnType, Variable[] freeVars,
      Dictionary cxt, Dictionary outer, ErrorReport errors)
  {
    List<Variable> definedVars = new ArrayList<>();
    Collections.addAll(definedVars, freeVars);

    assert TypeUtils.isPatternType(pttrnType);

    TypeExp ptnType = (TypeExp) Freshen.freshenForUse(pttrnType);

    ConsList<IContentExpression> newVars = ConsList.nil();
    IType argType = TypeUtils.getPtnMatchType(ptnType);

    final Variable var = Variable.create(Location.nullLoc, argType, GenSym.genSym("_#"));
    newVars = new ConsList<>(var, newVars);
    definedVars.add(var);

    List<MatchTriple<IContentExpression>> list = new ArrayList<>();

    for (Triple<IContentPattern[], ICondition, IContentExpression> rl : rules)
      list.add(formMatchTriple(rl));

    Generate<IContentExpression> gen = new ExpressionCaseGenerator(TypeUtils.tupleType(TypeUtils
        .getPtnResultType(ptnType)));

    IContentExpression nBody = compileMatch(loc, newVars, list, new NullExp(loc, TypeUtils.getPtnMatchType(ptnType)),
        cxt, outer, gen, definedVars, maxDepth, errors);

    return new PatternAbstraction(loc, name, pttrnType, var, nBody, freeVars);
  }

  private static <T extends Canonical> List<MatchTriple<T>> analyseAggConTriples(IContentExpression var,
      Map<String, IContentPattern> newArgPtns, List<String> argIndex, List<MatchTriple<T>> triples, Dictionary cxt)
  {
    List<MatchTriple<T>> list = new ArrayList<>();

    for (MatchTriple<T> tr : triples) {
      ConsList<IContentPattern> nArgs = tr.args.tail();

      IContentPattern first = tr.args.head();

      if (first instanceof MatchingPattern)
        tr = substitute(tr, var, cxt);

      RecordPtn aggCon = (RecordPtn) deRefPtn(first);
      Map<String, IContentPattern> aggArgs = aggCon.getElements();

      for (String att : argIndex) {
        IContentPattern exPtn = aggArgs.get(att);

        if (exPtn == null) {
          exPtn = newArgPtns.get(att);
          nArgs = new ConsList<>(Variable.create(Location.nullLoc, exPtn.getType(), "_"), nArgs);
        } else
          nArgs = new ConsList<>(exPtn, nArgs);
      }

      list.add(new MatchTriple<>(nArgs, tr.cond, tr.body));
    }
    return list;
  }

  private static <T extends Canonical> T analyseTupleCases(Location loc, ConsList<IContentExpression> vars,
      List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
      List<Variable> definedVars, int depth, ErrorReport errors)
  {
    IContentExpression var = vars.head();
    ConsList<IContentExpression> varTail = vars.tail();
    IType type = TypeUtils.deRef(var.getType());

    assert TypeUtils.isTupleType(type);

    IType argTypes[] = TypeUtils.tupleTypes(type);
    int arity = argTypes.length;

    // There will only ever be a single entry here
    List<Pair<IContentPattern, T>> cases = new ArrayList<>();

    List<MatchTriple<T>> subTriples = new ArrayList<>();

    for (MatchTriple<T> eq : triples) {
      ConsList<IContentPattern> nArgs = eq.args.tail();

      IContentPattern first = eq.args.head();

      if (first instanceof MatchingPattern)
        eq = substitute(eq, var, cxt);

      TuplePtn posCon = (TuplePtn) eq.args.head();

      List<IContentPattern> posArgs = posCon.getElements();
      for (int ix = posArgs.size(); ix > 0; ix--)
        nArgs = new ConsList<>(posArgs.get(ix - 1), nArgs);

      subTriples.add(new MatchTriple<>(nArgs, eq.cond, eq.body));
    }

    if (!subTriples.isEmpty()) {
      Variable posArgs[] = new Variable[arity];
      List<Variable> tupleDefined = new ArrayList<>(definedVars);

      for (int ix = posArgs.length; ix > 0; ix--) {
        Variable newVar = Variable.create(Location.nullLoc, argTypes[ix - 1], GenSym.genSym("_#"));
        posArgs[ix - 1] = newVar;
        tupleDefined.add(newVar);
        varTail = new ConsList<>(posArgs[ix - 1], varTail);
      }

      T nBody = compileMatch(loc, varTail, subTriples, deflt, cxt, outer, gen, tupleDefined, depth - 1, errors);

      cases.add(Pair.pair(TuplePtn.tuplePtn(loc, posArgs), nBody));
    }

    // See if every constructor case is covered
    return gen.makeCase(loc, var, cases, deflt);
  }

  // A bunch of anonymous aggregates. They are wrapped into a single generic
  // one, with the sub-cases analyzed afterwards
  private static <T extends Canonical> T analyseRecordCases(Location loc, ConsList<IContentExpression> vars,
      List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
      List<Variable> definedVars, int depth, ErrorReport errors)
  {
    IContentExpression var = vars.head();
    ConsList<IContentExpression> varTail = vars.tail();
    IType type = TypeUtils.deRef(var.getType());

    assert TypeUtils.isTypeInterface(type);

    SortedMap<String, IContentPattern> newArgPtns = new TreeMap<>();
    List<Variable> caseDefined = new ArrayList<>(definedVars);

    ConsList<IContentExpression> aggArgs = varTail;

    // We do two passes; because a given aggregate may be partial
    for (MatchTriple<T> tr : triples) {
      RecordPtn record = (RecordPtn) deRefPtn(tr.args.head());

      for (Entry<String, IContentPattern> eLitArgs : record.getElements().entrySet()) {
        String att = eLitArgs.getKey(); // We have an actual attribute
        // that has been used
        if (!newArgPtns.containsKey(att)) {
          Variable nVar = Variable.create(Location.nullLoc, eLitArgs.getValue().getType(), GenSym.genSym(att));
          newArgPtns.put(att, nVar);
          aggArgs = new ConsList<>(nVar, aggArgs);
          caseDefined.add(nVar);
        }
      }
    }

    Map<String, Integer> index = new HashMap<>();

    int ix = 0;
    for (Entry<String, IType> entry : ((TypeInterface) type).getAllFields().entrySet())
      index.put(entry.getKey(), ix++);

    IContentPattern newMatch = new RecordPtn(loc, type, newArgPtns, index);

    List<MatchTriple<T>> subTriples = new ArrayList<>();

    for (MatchTriple<T> eq : triples) {
      ConsList<IContentPattern> nArgs = eq.args.tail();

      IContentPattern first = eq.args.head();

      if (first instanceof MatchingPattern) {
        eq = substitute(eq, var, cxt);
        first = eq.args.head();
      }

      assert first instanceof RecordPtn;

      RecordPtn agg = (RecordPtn) first;
      Map<String, IContentPattern> els = agg.getElements();

      for (Entry<String, IContentPattern> argEntry : newArgPtns.entrySet()) {
        nArgs = nArgs.cons(els.get(argEntry.getKey()));
      }

      subTriples.add(new MatchTriple<>(nArgs, eq.cond, eq.body));
    }

    return gen.makeMatchTest(loc, var, newMatch, compileMatch(loc, aggArgs, subTriples, deflt, cxt, outer, gen,
        caseDefined, depth - 1, errors), deflt);
  }

  static <T extends Canonical> T compileMatch(Location loc, ConsList<IContentExpression> vars,
      List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
      List<Variable> definedVars, int depth, ErrorReport errors)
  {
    if (isEmpty(triples) || depth <= 0)
      return gen.conditionalize(loc, vars, triples, deflt, definedVars, cxt, outer, errors);
    else if (isAllTuples(triples))
      return analyseTupleCases(loc, vars, triples, deflt, cxt, outer, gen, definedVars, depth, errors);
    else if (isAllAnonRecords(triples))
      return analyseRecordCases(loc, vars, triples, deflt, cxt, outer, gen, definedVars, depth, errors);
    else if (isAllConstructors(triples))
      return analyseConstructors(loc, vars, triples, deflt, cxt, outer, gen, definedVars, depth, errors);
    else if (isAllPatternApplications(triples))
      return analysePatternApplications(loc, vars, triples, deflt, cxt, outer, gen, definedVars, depth, errors);
    else if (isAllScalarLiterals(triples))
      return analyseScalars(loc, vars, triples, deflt, cxt, outer, gen, definedVars, depth, errors);
    else if (isAllRegexpPtns(triples))
      return analyseRegexpPtns(loc, vars, triples, deflt, cxt, outer, gen, definedVars, depth, errors);
    else if (isAllVars(triples)) {
      List<MatchTriple<T>> nxtList = new ArrayList<>();
      IContentExpression var = vars.head();

      for (MatchTriple<T> eqn : triples)
        nxtList.add(substituteVar(eqn, var, cxt));

      return compileMatch(loc, vars.tail(), nxtList, deflt, cxt, outer, gen, definedVars, depth, errors);
    } else {
      List<List<MatchTriple<T>>> parts = partition(triples);

      assert parts.size() > 1;

      for (int ix = parts.size(); ix > 0; ix--) {
        deflt = compileMatch(loc, vars, parts.get(ix - 1), deflt, cxt, outer, gen, definedVars, depth, errors);
      }

      return deflt;
    }
  }

  private static <T extends Canonical> T analyseScalars(Location loc, ConsList<IContentExpression> vars,
      List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
      List<Variable> definedVars, int depth, ErrorReport errors)
  {
    IContentExpression var = vars.head();
    ConsList<IContentExpression> tail = vars.tail();
    Map<IContentPattern, List<MatchTriple<T>>> caseMap = new HashMap<>();

    for (MatchTriple<T> tr : triples) {
      IContentPattern first = tr.args.head();

      if (first instanceof MatchingPattern) {
        tr = substitute(tr, var, cxt);
        first = tr.args.head();
      }

      assert first instanceof ScalarPtn;

      ScalarPtn scalar = (ScalarPtn) first;
      List<MatchTriple<T>> eqnList = caseMap.get(scalar);

      if (eqnList == null) {
        eqnList = new ArrayList<>();
        caseMap.put(scalar, eqnList);
      }
      eqnList.add(tr);
    }

    // Start building the case expression
    List<Pair<IContentPattern, T>> cases = new ArrayList<>();
    for (Entry<IContentPattern, List<MatchTriple<T>>> entry : caseMap.entrySet()) {
      List<MatchTriple<T>> list = new ArrayList<>();

      for (MatchTriple<T> tr1 : entry.getValue()) {
        ConsList<IContentPattern> nArgs = tr1.args.tail();

        assert tr1.args.head() instanceof ScalarPtn;

        list.add(new MatchTriple<>(nArgs, tr1.cond, tr1.body));
      }

      T nBody = compileMatch(loc, tail, list, deflt, cxt, outer, gen, definedVars, depth - 1, errors);

      cases.add(Pair.pair(entry.getKey(), nBody));
    }
    return gen.makeCase(loc, var, cases, deflt);
  }

  private static <T extends Canonical> T analyseRegexpPtns(Location loc, ConsList<IContentExpression> vars,
      List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
      List<Variable> definedVars, int depth, ErrorReport errors)
  {
    IContentExpression var = vars.head();
    ConsList<IContentExpression> tail = vars.tail();

    // This is a temporary mapping. In future, we need to properly analyze regular expressions.

    T result = deflt;
    for (MatchTriple<T> tr : triples) {
      IContentPattern first = tr.args.head();

      if (first instanceof MatchingPattern) {
        tr = substitute(tr, var, cxt);
        first = tr.args.head();
      }

      assert first instanceof RegExpPattern;

      List<MatchTriple<T>> subTriples = new ArrayList<>();

      ConsList<IContentPattern> nArgs = tr.args.tail();

      subTriples.add(new MatchTriple<>(nArgs, tr.cond, tr.body));

      T nBody = compileMatch(loc, tail, subTriples, result, cxt, outer, gen, definedVars, depth - 1, errors);

      result = gen.makeMatchTest(loc, var, first, nBody, result);
    }

    // Temp for debugging
    if (StarCompiler.TEST_REGEXP)
      RegexpCompiler.altAnalyseRegexps(loc, vars, triples, deflt, cxt, outer, gen, definedVars, depth, errors);

    return result;
  }

  @SuppressWarnings("unused")
  private static <T extends Canonical> T altAnalyseRegexps(Location loc, ConsList<IContentExpression> vars,
      List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
      List<Variable> definedVars, ErrorReport errors)
  {
    IContentExpression var = vars.head();
    ConsList<IContentExpression> tail = vars.tail();
    Map<IContentPattern, List<MatchTriple<T>>> caseMap = new HashMap<>();

    for (MatchTriple<T> tr : triples) {
      IContentPattern arg = tr.args.head();
      assert arg instanceof RegExpPattern;

      RegExpPattern regexp = (RegExpPattern) arg;
      List<MatchTriple<T>> eqnList = caseMap.get(regexp);

      if (eqnList == null) {
        eqnList = new ArrayList<>();
        caseMap.put(regexp, eqnList);
      }
      eqnList.add(tr);
    }

    // Start building the case expression
    List<Pair<IContentPattern, T>> cases = new ArrayList<>();
    for (Entry<IContentPattern, List<MatchTriple<T>>> entry : caseMap.entrySet()) {
      List<MatchTriple<T>> list = new ArrayList<>();

      for (MatchTriple<T> tr1 : entry.getValue()) {
        ConsList<IContentPattern> nArgs = tr1.args.tail();

        list.add(new MatchTriple<>(nArgs, tr1.cond, tr1.body));
      }

      T nBody = compileMatch(loc, tail, list, deflt, cxt, outer, gen, definedVars, maxDepth, errors);

      cases.add(Pair.pair(entry.getKey(), nBody));
    }
    return gen.makeCase(loc, var, cases, deflt);
  }

  private static <T extends Canonical> T analyseConstructors(Location loc, ConsList<IContentExpression> vars,
      List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
      List<Variable> definedVars, int depth, ErrorReport errors)
  {
    IContentExpression var = vars.head();
    ConsList<IContentExpression> varTail = vars.tail();
    IType type = TypeUtils.deRef(var.getType());
    ITypeDescription desc = cxt.getTypeDescription(type.typeLabel());
    if (desc == null)
      errors.reportError(StringUtils.msg("cannot locate definition for type ", type), loc);
    else if (!(desc instanceof IAlgebraicType))
      errors.reportError(StringUtils.msg("type ", type, " not a algebraic type"), loc);

    Map<IContentExpression, List<MatchTriple<T>>> eqnMap = new HashMap<>();

    for (MatchTriple<T> eq : triples) {
      IContentPattern arg = eq.args.head();

      IContentExpression label = pttrnLabel(arg);
      List<MatchTriple<T>> eqnList = eqnMap.get(label);
      if (eqnList == null) {
        eqnList = new ArrayList<>();
        eqnMap.put(label, eqnList);
      }
      eqnList.add(eq);
    }

    // Start building the case expression
    List<Pair<IContentPattern, T>> cases = new ArrayList<>();
    for (Entry<IContentExpression, List<MatchTriple<T>>> entry : eqnMap.entrySet()) {
      Variable cVar = (Variable) entry.getKey();

      IValueSpecifier conSpec = desc instanceof IAlgebraicType ? ((IAlgebraicType) desc).getValueSpecifier(cVar
          .getName()) : null; // only happens on errors
      Dictionary caseCxt = cxt.fork();
      List<Variable> caseDefined = new ArrayList<>(definedVars);

      if (conSpec instanceof RecordSpecifier) {
        RecordSpecifier recSpec = (RecordSpecifier) conSpec;

        // We find out which attributes of this record constructor have
        // been used in any equation
        Map<String, IContentPattern> newArgPtns = new HashMap<>();
        ConsList<IContentExpression> aggArgs = varTail;
        List<String> argIndex = new ArrayList<>();

        for (MatchTriple<T> tr : entry.getValue()) {
          RecordPtn eqnLit = (RecordPtn) deRefPtn(tr.args.head());

          for (Entry<String, IContentPattern> eLitArgs : eqnLit.getElements().entrySet()) {
            String att = eLitArgs.getKey(); // We have an actual attribute
            // that has been used
            if (!newArgPtns.containsKey(att)) {
              Variable nVar = Variable.create(Location.nullLoc, eLitArgs.getValue().getType(), GenSym.genSym(att));
              newArgPtns.put(att, nVar);
              aggArgs = new ConsList<>(nVar, aggArgs);
              argIndex.add(att);
              caseDefined.add(nVar);
            }
          }
        }

        List<MatchTriple<T>> subTriples = analyseAggConTriples(var, newArgPtns, argIndex, entry.getValue(), cxt);

        T nBody = compileMatch(loc, aggArgs, subTriples, deflt, caseCxt, outer, gen, caseDefined, depth - 1, errors);

        IContentExpression rec = new Variable(loc, recSpec.getConType(), recSpec.getLabel());
        cases.add(Pair.pair((IContentPattern) new RecordPtn(loc, type, rec, newArgPtns, recSpec.getIndex()), nBody));
      } else if (conSpec instanceof ConstructorSpecifier) {
        List<MatchTriple<T>> subTriples = new ArrayList<>();
        ConstructorSpecifier posSpec = (ConstructorSpecifier) conSpec;
        IType posTypes[] = new IType[posSpec.arity()];

        ConsList<IContentExpression> conArgs = varTail;

        List<MatchTriple<T>> subCases = entry.getValue();

        // if (subCases.size() > 1) {
        for (MatchTriple<T> eq : subCases) {
          ConsList<IContentPattern> nArgs = eq.args.tail();

          IContentPattern first = eq.args.head();
          ConstructorPtn posCon = (ConstructorPtn) deRefPtn(first);

          if (first instanceof MatchingPattern)
            eq = substitute(eq, var, cxt);

          List<IContentPattern> posArgs = posCon.getElements();
          for (int ix = posArgs.size(); ix > 0; ix--) {
            nArgs = new ConsList<>(posArgs.get(ix - 1), nArgs);
            if (posTypes[ix - 1] == null)
              posTypes[ix - 1] = posArgs.get(ix - 1).getType();
          }

          subTriples.add(new MatchTriple<>(nArgs, eq.cond, eq.body));
        }

        if (!subTriples.isEmpty()) {
          Variable posArgs[] = new Variable[posSpec.arity()];

          final String varPrefix = "_#";
          for (int ix = posArgs.length; ix > 0; ix--) {
            Variable nVar = Variable.create(Location.nullLoc, posTypes[ix - 1], GenSym.genSym(varPrefix));
            posArgs[ix - 1] = nVar;
            conArgs = new ConsList<>(posArgs[ix - 1], conArgs);

            caseDefined.add(nVar);
          }
          T nBody = compileMatch(loc, conArgs, subTriples, deflt, caseCxt, outer, gen, caseDefined, depth - 1, errors);

          cases.add(Pair.pair((IContentPattern) new ConstructorPtn(loc, posSpec.getLabel(), type, posArgs), nBody));
        }
      }
    }

    // See if every constructor case is covered
    return gen.makeCase(loc, var, cases, deflt);
  }

  private static IContentExpression pttrnLabel(IContentPattern ptn)
  {
    while (ptn instanceof MatchingPattern)
      ptn = ((MatchingPattern) ptn).getPtn();
    return (ptn instanceof ConstructorPtn ? ((ConstructorPtn) ptn).getFun() : ((RecordPtn) ptn).getFun());
  }

  private static IContentPattern deRefPtn(IContentPattern ptn)
  {
    if (ptn instanceof MatchingPattern)
      return deRefPtn(((MatchingPattern) ptn).getPtn());
    else
      return ptn;
  }

  // Handle calls to abstract pattern applications in a similar fashion to
  // regular constructors
  private static <T extends Canonical> T analysePatternApplications(Location loc, ConsList<IContentExpression> vars,
      List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
      List<Variable> definedVars, int depth, ErrorReport errors)
  {
    IContentExpression var = vars.head();
    Map<IContentExpression, List<MatchTriple<T>>> cases = new HashMap<>();

    // Sort pattern applications by NAME
    for (MatchTriple<T> eq : triples) {
      IContentPattern head = eq.args.head();
      if (head instanceof MatchingPattern) {
        eq = substitute(eq, var, cxt);
        head = eq.args.head();
      }
      PatternApplication arg = (PatternApplication) head;

      IContentExpression label = arg.getAbstraction();
      List<MatchTriple<T>> list = cases.get(label);
      if (list == null) {
        list = new ArrayList<>();
        cases.put(label, list);
      }
      list.add(eq);
    }

    // We build an if-then-else structure from the different pattern
    // abstractions we have encountered
    T tree = deflt;

    for (Entry<IContentExpression, List<MatchTriple<T>>> cEntry : cases.entrySet()) {
      IContentExpression ptn = cEntry.getKey();
      List<Variable> listDefined = new ArrayList<>(definedVars);

      List<MatchTriple<T>> subTriples = subPttrnTriples(cEntry.getValue());

      final String varPrefix = "var_#";
      IType pttrnType = ptn.getType();

      IType[] elemTypes = TypeUtils.tupleTypes(TypeUtils.isPatternType(pttrnType) ? TypeUtils
          .getPtnResultType(pttrnType) : TypeUtils.getConstructorArgType(pttrnType));
      int lstPtnLen = elemTypes.length;

      IContentPattern eArgs[] = new IContentPattern[lstPtnLen];
      ConsList<IContentExpression> nVars = vars.tail();

      for (int ix = lstPtnLen; ix > 0; ix--) {
        Variable eArg = Variable.create(loc, elemTypes[ix - 1], GenSym.genSym(varPrefix));
        eArgs[ix - 1] = eArg;
        listDefined.add(eArg);
        nVars = new ConsList<>(eArg, nVars);
      }

      T nBody = compileMatch(loc, nVars, subTriples, deflt, cxt, outer, gen, listDefined, depth - 1, errors);

      tree = gen.makeMatchTest(loc, var, new PatternApplication(loc, var.getType(), ptn, eArgs), nBody, tree);
    }

    return tree;
  }

  private static <T extends Canonical> List<MatchTriple<T>> subPttrnTriples(List<MatchTriple<T>> eqns)
  {
    List<MatchTriple<T>> list = new ArrayList<>();

    for (MatchTriple<T> eq : eqns) {
      PatternApplication head = (PatternApplication) eq.args.head();

      ConsList<IContentPattern> nArgs = eq.args.tail();

      List<IContentPattern> elArgs = ((TuplePtn) head.getArg()).getElements();
      for (int ix = elArgs.size(); ix > 0; ix--)
        nArgs = new ConsList<>(elArgs.get(ix - 1), nArgs);

      list.add(new MatchTriple<>(nArgs, eq.cond, eq.body));
    }

    return list;
  }

  @SuppressWarnings("unchecked")
  private static <T extends Canonical> MatchTriple<T> substituteVar(MatchTriple<T> tr, IContentExpression var,
      Dictionary cxt)
  {
    IContentPattern old = tr.args.head();
    if (old instanceof CastPtn)
      old = ((CastPtn) old).getInner();
    else if (old instanceof MatchingPattern) {
      tr = substitute(tr, var, cxt);
      return substituteVar(tr, var, cxt);
    }
    Substituter visitor = new Substituter(old, var, cxt);
    ConsList<IContentPattern> nArgs = substCons(tr.args.tail(), visitor);

    ICondition cond = tr.cond != null ? (ICondition) substitute(tr.cond, visitor) : null;

    return new MatchTriple<>(nArgs, cond, (T) substitute(tr.body, visitor));
  }

  @SuppressWarnings("unchecked")
  private static <T extends Canonical> MatchTriple<T> substitute(MatchTriple<T> tr, IContentExpression var,
      Dictionary cxt)
  {
    IContentPattern head = tr.args.head();
    IContentPattern old = head;
    if (old instanceof CastPtn)
      old = ((CastPtn) old).getInner();
    else if (old instanceof MatchingPattern) {
      old = ((MatchingPattern) old).getVar();
      head = ((MatchingPattern) head).getPtn();
    }
    Substituter visitor = new Substituter(old, var, cxt);
    ConsList<IContentPattern> nArgs = substCons(tr.args.tail(), visitor);

    ICondition cond = tr.cond != null ? (ICondition) substitute(tr.cond, visitor) : null;

    return new MatchTriple<>(ConsList.cons(head, nArgs), cond, (T) substitute(tr.body, visitor));
  }

  private static ConsList<IContentPattern> substCons(ConsList<IContentPattern> cons, Substituter visitor)
  {
    if (cons.isNil())
      return cons;
    else
      return new ConsList<>((IContentPattern) substitute(cons.head(), visitor), substCons(cons.tail(), visitor));
  }

  private static <T extends Canonical> Canonical substitute(T term, Substituter visitor)
  {
    if (term instanceof IContentExpression)
      return visitor.transform((IContentExpression) term);
    else if (term instanceof IContentAction)
      return visitor.transform((IContentAction) term);
    else if (term instanceof ICondition)
      return visitor.transform((ICondition) term);
    else if (term instanceof IContentPattern)
      return visitor.transform((IContentPattern) term);
    else
      throw new UnsupportedOperationException("expecting an expression or action");
  }

  @SuppressWarnings("unchecked")
  private static <T> T transformQueries(T term, final List<Variable> definedVars, Dictionary cxt,
      final Dictionary outer, final ErrorReport errors)
  {
    final DictionaryChecker checker = new DictionaryChecker(cxt, definedVars);
    final ExpressionTransformer visitor = new ExpressionTransformer(cxt) {
      {
        install(new ConditionalActionTransform());
      }

      class ConditionalActionTransform implements TransformAction
      {
        @Override
        public IContentAction transformAction(IContentAction act)
        {
          ConditionalAction cnd = (ConditionalAction) act;
          Location loc = act.getLoc();
          ICondition cond = cnd.getCond();
          if (QueryPlanner.isTransformable(cond)) {
            List<Variable> defined = VarAnalysis.findDefinedVars(cond, checker);

            return QueryPlanner.transformConditionNxt(loc, defined, cond, cnd.getThPart(), cnd.getElPart(), cxt, outer,
                errors);
          } else
            return new ConditionalAction(act.getLoc(), transform(sortConjunction(cnd.getCond(), definedVars, checker)),
                transform(cnd.getThPart()), transform(cnd.getElPart()));
        }

        @Override
        public Class<? extends IContentAction> transformClass()
        {
          return ConditionalAction.class;
        }
      }
    };

    if (term instanceof IContentExpression)
      return (T) visitor.transform((IContentExpression) term);
    else if (term instanceof IContentAction)
      return (T) visitor.transform((IContentAction) term);
    else
      throw new UnsupportedOperationException("expecting an expression or action");
  }

  private static ICondition sortConjunction(ICondition cond, List<Variable> definedVars, VarChecker checker)
  {
    return FlowAnalysis.analyseFlow(cond, definedVars, checker);
  }

  // partition is used in the mixed case situation -- we repartition the
  // equations so that each partition is 'pure'
  private enum PartitionMode {
    initial, inVars, inConstructors, inPatterns, inScalars, inRegexps, inTuple, unknown
  }

  private static <T extends Canonical> List<List<MatchTriple<T>>> partition(List<MatchTriple<T>> list)
  {
    List<List<MatchTriple<T>>> parts = new ArrayList<>();

    PartitionMode mode = PartitionMode.initial;
    List<MatchTriple<T>> soFar = null;

    for (MatchTriple<T> tr : list) {
      PartitionMode first = argMode(tr.args.head());

      if (mode == first) {
        assert soFar != null;
        soFar.add(tr);
      } else {
        if (mode == PartitionMode.initial) {
          soFar = new ArrayList<>();
          soFar.add(tr);
        } else if (first != PartitionMode.unknown) {
          assert !soFar.isEmpty();
          parts.add(soFar);
          soFar = new ArrayList<>();
          soFar.add(tr);
        }
        mode = first;
      }
    }

    if (soFar != null && !soFar.isEmpty())
      parts.add(soFar);

    return parts;
  }

  private static PartitionMode argMode(IContentPattern arg)
  {
    if (arg instanceof Variable)
      return PartitionMode.inVars;
    else if (arg instanceof ScalarPtn)
      return PartitionMode.inScalars;
    else if (arg instanceof ConstructorPtn || arg instanceof RecordPtn)
      return PartitionMode.inConstructors;
    else if (arg instanceof PatternApplication)
      return PartitionMode.inPatterns;
    else if (arg instanceof CastPtn)
      return argMode(((CastPtn) arg).getInner());
    else if (arg instanceof RegExpPattern)
      return PartitionMode.inRegexps;
    else if (arg instanceof MatchingPattern)
      return argMode(((MatchingPattern) arg).getPtn());
    else if (arg instanceof TuplePtn)
      return PartitionMode.inTuple;
    else {
      assert false : "cannot determine " + arg + " as pattern form at " + arg.getLoc();
      return PartitionMode.unknown;
    }
  }

  private static <T extends Canonical> boolean isAllVars(List<MatchTriple<T>> eqns)
  {
    for (MatchTriple<T> eqn : eqns) {
      if (!isVarPttrn(deRefPtn(eqn.args.head())))
        return false;
    }
    return true;
  }

  private static boolean isVarPttrn(IContentPattern arg)
  {
    return arg instanceof Variable || arg instanceof CastPtn && isVarPttrn(((CastPtn) arg).getInner());
  }

  private static <T extends Canonical> boolean isEmpty(List<MatchTriple<T>> eqns)
  {
    for (MatchTriple<T> eqn : eqns) {
      if (!eqn.args.isNil())
        return false;
    }
    return true;
  }

  private static <T extends Canonical> boolean isAllTuples(List<MatchTriple<T>> eqns)
  {
    for (MatchTriple<T> eqn : eqns) {
      IContentPattern head = deRefPtn(eqn.args.head());

      if (!CompilerUtils.isTuplePattern(head))
        return false;
    }
    return true;
  }

  private static <T extends Canonical> boolean isAllAnonRecords(List<MatchTriple<T>> eqns)
  {
    for (MatchTriple<T> eqn : eqns) {
      IContentPattern head = deRefPtn(eqn.args.head());

      if (!isAnonRecordPattern(head))
        return false;
    }
    return true;
  }

  private static boolean isAnonRecordPattern(IContentPattern tpl)
  {
    tpl = deRefPtn(tpl);
    return tpl instanceof RecordPtn && TypeUtils.isTypeInterface(tpl.getType());
  }

  private static <T extends Canonical> boolean isAllConstructors(List<MatchTriple<T>> eqns)
  {
    for (MatchTriple<T> eqn : eqns) {
      IContentPattern head = deRefPtn(eqn.args.head());
      if (!(head instanceof ConstructorPtn)) {
        if (head instanceof RecordPtn) {
          if (TypeUtils.isTypeInterface(head.getType()))
            return false;
        } else
          return false;
      }
    }
    return true;
  }

  private static <T extends Canonical> boolean isAllPatternApplications(List<MatchTriple<T>> cases)
  {
    for (MatchTriple<T> eqn : cases) {
      IContentPattern head = deRefPtn(eqn.args.head());
      if (!(head instanceof PatternApplication))
        return false;
    }
    return true;
  }

  private static <T extends Canonical> boolean isAllScalarLiterals(List<MatchTriple<T>> eqns)
  {
    for (MatchTriple<T> eqn : eqns) {
      if (!(deRefPtn(eqn.args.head()) instanceof ScalarPtn))
        return false;
    }
    return true;
  }

  private static <T extends Canonical> boolean isAllRegexpPtns(List<MatchTriple<T>> eqns)
  {
    for (MatchTriple<T> eqn : eqns) {
      if (!(deRefPtn(eqn.args.head()) instanceof RegExpPattern))
        return false;
    }
    return true;
  }

  public static ICondition mergeMatches(ConsList<IContentExpression> args, ConsList<IContentPattern> ptns,
      ICondition cond)
  {
    assert args.length() == ptns.length();
    if (args.isNil())
      return cond;
    else {
      cond = mergeMatches(args.tail(), ptns.tail(), cond);
      IContentExpression hdExp = args.head();
      IContentPattern hdPtn = ptns.head();
      return CompilerUtils.conjunction(cond, new Matches(hdExp.getLoc(), hdExp, hdPtn));
    }
  }

  interface Generate<T extends Canonical>
  {
    T makeCase(Location loc, IContentExpression var, List<Pair<IContentPattern, T>> cases, T deflt);

    T conditionalize(Location loc, ConsList<IContentExpression> args, List<MatchTriple<T>> eqns, T deflt,
        List<Variable> definedVars, Dictionary cxt, Dictionary outer, ErrorReport errors);

    T makeMatchTest(Location loc, IContentExpression var, IContentPattern ptn, T cont, T deflt);
  }
}