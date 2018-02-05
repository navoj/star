package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.Assignment;
import org.star_lang.star.compiler.canonical.Canonical;
import org.star_lang.star.compiler.canonical.CharSet;
import org.star_lang.star.compiler.canonical.CharSetVisitor;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.NFA;
import org.star_lang.star.compiler.canonical.NullAction;
import org.star_lang.star.compiler.canonical.RegExpPattern;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.CharSet.AnyChar;
import org.star_lang.star.compiler.canonical.CharSet.CharClass;
import org.star_lang.star.compiler.canonical.CharSet.CharUnion;
import org.star_lang.star.compiler.canonical.NFA.BindNFA;
import org.star_lang.star.compiler.canonical.NFA.BoundNFA;
import org.star_lang.star.compiler.canonical.NFA.CharClassNFA;
import org.star_lang.star.compiler.canonical.NFA.Disjunct;
import org.star_lang.star.compiler.canonical.NFA.Sequence;
import org.star_lang.star.compiler.canonical.NFA.StarNFA;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.transform.MatchCompiler.Generate;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.TypeChecker;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.ConsList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.IntWrap;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntEQ;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntGE;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntLE;

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

public class RegexpCompiler {
  private static final IType voidActionType = TypeUtils.actionType(StandardTypes.unitType);

  public static <T extends Canonical> T altAnalyseRegexps(Location loc, ConsList<IContentExpression> vars,
                                                          List<MatchTriple<T>> triples, T deflt, Dictionary cxt, Dictionary outer, Generate<T> gen,
                                                          List<Variable> definedVars, int depth, ErrorReport errors) {
    // IContentExpression var = vars.head();
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

    // Apply the match compiler to remaining arguments, and build a new table
    List<Pair<RegExpPattern, T>> cases = new ArrayList<>();
    for (Entry<IContentPattern, List<MatchTriple<T>>> entry : caseMap.entrySet()) {
      List<MatchTriple<T>> list = new ArrayList<>();

      for (MatchTriple<T> tr1 : entry.getValue()) {
        ConsList<IContentPattern> nArgs = tr1.args.tail();

        list.add(new MatchTriple<>(nArgs, tr1.cond, tr1.body));
      }

      T nBody = MatchCompiler.compileMatch(loc, tail, list, deflt, cxt, outer, gen, definedVars, depth - 1,
              errors);

      cases.add(Pair.pair((RegExpPattern) entry.getKey(), nBody));
    }

    SortedMap<Integer, T> bodies = new TreeMap<>();
    Map<Integer, NFA> states = new HashMap<>();
    Map<Integer, List<Integer>> next = new HashMap<>();
    Map<Integer, List<Integer>> first = new HashMap<>();

    int stateNo = 0;
    for (Pair<RegExpPattern, T> entry : cases) {
      NFA nfa = entry.left().getNfa();
      stateNo = NFA.numberStates(nfa, stateNo, states);
      System.out.println(nfa);
      markFirstState(nfa, first);
      markNextState(nfa, -1, next);

      bodies.put(nfa.getStateNo(), entry.right());
    }

    System.out.println(showStates(states, first));
    System.out.println(showStates(states, next));

    Map<Integer, List<Integer>> follows = new HashMap<>();
    follow(states, first, next, follows);
    System.out.println(showStates(states, follows));

    return null;
  }

  private static String showStates(Map<Integer, NFA> states, Map<Integer, List<Integer>> next) {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();

    for (Entry<Integer, NFA> stEntry : states.entrySet()) {
      Integer stateNo = stEntry.getKey();
      NFA nfa = stEntry.getValue();

      assert stateNo == nfa.getStateNo();

      nfa.prettyPrint(disp);
      disp.append("->");
      List<Integer> nxt = next.get(stateNo);
      if (nxt != null) {
        disp.append("[");
        String sep = "";
        for (Integer nx : nxt) {
          disp.append(sep);
          sep = ", ";
          disp.append(nx);
        }
        disp.append("]\n");
      } else
        disp.append("ø\n");
    }

    return disp.toString();
  }

  private static void markFirstState(NFA state, Map<Integer, List<Integer>> first) {
    List<Integer> leading = getSubset(state.getStateNo(), first);
    markFirstState(state, first, leading);
  }

  private static void markFirstState(NFA state, Map<Integer, List<Integer>> first, List<Integer> leading) {
    switch (state.nfaKind()) {
      case charSetNFA:
        setFirstState(state, leading);
        break;
      case disjunctNFA: {
        Disjunct disj = (Disjunct) state;
        markFirstState(disj.getLeft(), first, leading);
        markFirstState(disj.getRight(), first, leading);

        markFirstState(disj, first);
        break;
      }
      case bindNFA:
      case boundNFA:
      case emptyNFA:
        setFirstState(state, leading);
        break;
      case seqNFA: {
        Sequence seq = (Sequence) state;
        NFA els[] = seq.getEls();

        markFirstState(els[0], first, leading);

        for (NFA el : els)
          markFirstState(el, first);
        break;
      }
      case starNFA: {
        StarNFA star = (StarNFA) state;
        markFirstState(star.getRepeat(), first, leading);
        markFirstState(star.getRepeat(), first);
        break;
      }
      case varNFA:
      case endVarNFA:
        setFirstState(state, leading);
        break;
    }
  }

  private static List<Integer> getSubset(int stateNo, Map<Integer, List<Integer>> states) {
    List<Integer> sub = states.get(stateNo);
    if (sub == null) {
      sub = new ArrayList<>();
      states.put(stateNo, sub);
    }
    return sub;
  }

  private static void setFirstState(NFA state, List<Integer> first) {
    int stateNo = state.getStateNo();
    if (!first.contains(stateNo))
      first.add(stateNo);
  }

  // Record teh immediate follow on states, and return the first number of this state
  private static int markNextState(NFA state, int nextNo, Map<Integer, List<Integer>> next) {
    switch (state.nfaKind()) {
      case disjunctNFA: {
        Disjunct disj = (Disjunct) state;
        setNextState(state, markNextState(disj.getLeft(), nextNo, next), next);
        setNextState(state, markNextState(disj.getRight(), nextNo, next), next);
        return state.getStateNo();
      }
      case charSetNFA:
      case bindNFA:
      case boundNFA:
      case emptyNFA:
      case varNFA:
      default:
        setNextState(state, nextNo, next);
        return state.getStateNo();
      case seqNFA: {
        Sequence seq = (Sequence) state;
        setNextState(state, nextNo, next);

        NFA els[] = seq.getEls();
        for (int ix = els.length - 1; ix >= 0; ix--) {
          markNextState(els[ix], nextNo, next);
          nextNo = els[ix].getStateNo();
        }
        return nextNo;
      }
      case starNFA: {
        StarNFA star = (StarNFA) state;
        setNextState(state, nextNo, next);

        nextNo = markNextState(star.getRepeat(), state.getStateNo(), next);
        setNextState(state, nextNo, next);
        return state.getStateNo();
      }
    }
  }

  private static void setNextState(NFA state, Integer nextNo, Map<Integer, List<Integer>> next) {
    List<Integer> coming = next.get(state.getStateNo());
    if (coming == null) {
      coming = new ArrayList<>();
      next.put(state.getStateNo(), coming);
    }
    if (!coming.contains(nextNo))
      coming.add(nextNo);
  }

  private static void follow(Map<Integer, NFA> states, Map<Integer, List<Integer>> first,
                             Map<Integer, List<Integer>> next, Map<Integer, List<Integer>> follows) {
    boolean modified = true;
    while (modified) {
      modified = false;
      for (Entry<Integer, List<Integer>> entry : next.entrySet()) {
        Integer sNo = entry.getKey();
        List<Integer> follow = follows.get(sNo);
        if (follow == null) {
          follow = new ArrayList<>();
          follows.put(sNo, follow);
          mergeList(follow, entry.getValue(), modified);
          NFA nfa = states.get(sNo);
          if (nfa != null) {
            for (Integer nx : entry.getValue())
              modified = mergeList(follow, first.get(nx), modified);
          }
          modified = true;
        }
        int followLength = follow.size();
        for (int ix = 0; ix < followLength; ix++) {
          Integer nx = follow.get(ix);
          NFA nfa = states.get(nx);
          if (nfa != null && nfa.isNullable())
            modified = mergeList(follow, follows.get(nx), modified);
        }
      }
    }
  }

  private static boolean mergeList(List<Integer> list, Collection<Integer> add, boolean modified) {
    if (add != null) {
      for (Integer ix : add)
        if (!list.contains(ix)) {
          list.add(ix);
          modified = true;
        }
    }
    return modified;
  }

  @SuppressWarnings("unused")
  private static IContentAction genNFAPreCode(NFA nfa, Map<Integer, NFA> states,
                                              Map<Variable, VarDeclaration> varBinding, Variable collVar, Variable stateVar, Dictionary cxt, ErrorReport errors) {
    Location loc = nfa.getLoc();
    IType integerType = StandardTypes.integerType;
    IType stringType = StandardTypes.stringType;

    switch (nfa.nfaKind()) {
      case bindNFA: {
        BindNFA bind = (BindNFA) nfa;
        VarDeclaration binding = varBinding.get(bind.getVar());
        if (binding == null) {
          Variable v = new Variable(loc, integerType, GenSym.genSym(bind.getVar().getName()));
          binding = VarDeclaration.varDecl(loc, v, nonInteger(loc));
          varBinding.put(bind.getVar(), binding);
        }
        return new Assignment(loc, (Variable) binding.getPattern(), stateVar);
      }
      case boundNFA: {
        BoundNFA bound = (BoundNFA) nfa;
        VarDeclaration binding = varBinding.get(bound.getVar());

        assert binding != null;
        Variable v = (Variable) binding.getPattern();
        IContentExpression sliceFun = TypeChecker.typeOfName(loc, StandardNames.SLICE, TypeUtils.functionType(stringType,
                integerType, integerType, stringType), cxt, errors);
        IContentExpression slice = Application.apply(loc, stringType, sliceFun, collVar, v, stateVar);
        return new Assignment(loc, bound.getVar(), slice);
      }
      case charSetNFA:
      case disjunctNFA:
      case seqNFA:
      case starNFA:
      case emptyNFA:
      case varNFA:
      case endVarNFA:
      default:
        return new NullAction(loc, voidActionType);
    }
  }

  @SuppressWarnings("unused")
  private static IContentAction genNFAPostCode(NFA nfa, Map<Integer, NFA> states, Map<Integer, List<Integer>> follows,
                                               Map<Variable, VarDeclaration> varBinding, Variable collVar, Variable chVar, IContentExpression stateVar,
                                               Dictionary cxt, ErrorReport errors) {
    Location loc = nfa.getLoc();
    IType integerType = StandardTypes.integerType;
    IType stringType = StandardTypes.stringType;

    switch (nfa.nfaKind()) {
      case bindNFA: {
        BindNFA bind = (BindNFA) nfa;
        VarDeclaration binding = varBinding.get(bind.getVar());
        if (binding == null) {
          Variable v = new Variable(loc, TypeUtils.referencedType(integerType), GenSym.genSym(bind.getVar().getName()));
          binding = VarDeclaration.varDecl(loc, v, nonInteger(loc));
          varBinding.put(v, binding);
        }
        return new Assignment(loc, (Variable) binding.getPattern(), stateVar);
      }
      case boundNFA: {
        BoundNFA bound = (BoundNFA) nfa;
        VarDeclaration binding = varBinding.get(bound.getVar());

        assert binding != null;
        Variable v = (Variable) binding.getPattern();
        IContentExpression sliceFun = TypeChecker.typeOfName(loc, StandardNames.SLICE, TypeUtils.functionType(stringType,
                integerType, integerType, stringType), cxt, errors);
        IContentExpression slice = Application.apply(loc, stringType, sliceFun, collVar, v, stateVar);
        return new Assignment(loc, bound.getVar(), slice);
      }
      case charSetNFA: {
        CharClassNFA chars = (CharClassNFA) nfa;
        ICondition cond = genCharCondition(chars.getChars(), loc, chVar);
      }
      case disjunctNFA:
      case seqNFA:
      case starNFA:
      case emptyNFA:
      case varNFA:
      case endVarNFA:
      default:
        return new NullAction(loc, voidActionType);
    }
  }

  private static ICondition genCharCondition(CharSet spec, final Location loc, final Variable elVar) {
    final Stack<ICondition> stack = new Stack<>();
    CharSetVisitor visitor = new CharSetVisitor() {

      @Override
      public void visitAnyChar(AnyChar any) {
        stack.push(CompilerUtils.truth);
      }

      @Override
      public void visitCharClass(CharClass set) {
        int chars[] = set.getChars();
        Arrays.sort(chars);
        int ix = 0;
        int count = 0;
        while (ix < chars.length) {
          int first = chars[ix];
          int second = first;
          for (int jx = ix + 1; jx < chars.length && chars[jx - 1] + 1 == chars[jx]; jx++)
            second = chars[jx];
          if (first < second) {
            IsTrue lhs = new IsTrue(loc, Application.apply(loc, StandardTypes.booleanType, new Variable(loc, IntGE
                    .type(), IntGE.name), elVar, integer(loc, first)));
            ICondition rhs = new IsTrue(loc, Application.apply(loc, StandardTypes.booleanType, new Variable(loc, IntLE
                    .type(), IntLE.name), elVar, integer(loc, second)));
            stack.push(new Conjunction(loc, lhs, rhs));
          } else
            stack.push(new IsTrue(loc, Application.apply(loc, StandardTypes.booleanType, new Variable(loc,
                    IntEQ.type(), IntEQ.name), elVar, integer(loc, first))));
          count++;
          ix++;
        }
        while (count > 1) {
          ICondition lhs = stack.pop();
          ICondition rhs = stack.pop();
          stack.push(new Disjunction(loc, lhs, rhs));
          count--;
        }
      }

      @Override
      public void visitUnion(CharUnion union) {
        union.accept(this);
        union.accept(this);
        ICondition right = stack.pop();
        ICondition left = stack.pop();
        stack.push(new Disjunction(left.getLoc().extendWith(right.getLoc()), left, right));
      }
    };

    spec.accept(visitor);
    assert stack.size() == 1;
    return stack.pop();
  }

  private static IContentExpression nonInteger(Location loc) {
    return new ConstructorTerm(loc, IntWrap.nonIntegerEnum.getLabel(), StandardTypes.integerType);
  }

  private static IContentExpression integer(Location loc, int ix) {
    return new Scalar(loc, StandardTypes.rawIntegerType, ix);
  }
}
