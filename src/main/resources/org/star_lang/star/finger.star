/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * The TypeChecker implements the type inference module for Star
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

private import base;
private import strings;
private import arithmetic;
private import cons;
private import fingerPrelude;
private import sequences;
private import iterable;
private import updateable;
private import folding;

private fun unwrapInt(integer(i)) is i;
private def ZERO is unwrapInt(0);
private def ONE is unwrapInt(1);
private def TWO is unwrapInt(2);
private def MINUS_ONE is unwrapInt(-1);

/**
* This is a specialized version of the generic FingerTree augmented
* to allow indexing. */

private def FINGER_LIST_NODE_WIDTH is 8;
private def _FINGER_LIST_NODE_WIDTH is unwrapInt(FINGER_LIST_NODE_WIDTH);

type FingerList of %a is
     FLEmpty
  or FLSingle(%a)
  or FLDeep(integer_, FLDigit of %a, FingerList of (FLNode of (%a)), FLDigit of %a); -- integer_ arg is amount of stored elements; used for indexing

/**
* FingerLists have a special recursive structure: not only does a
* FingerList consist of other FingerLists, but the enclosed FingerList
* is a FingerList of FLNode of %a, which itself contains a FingerList of
* FLNode of FLNode of %a and so on. Several of the following functions
* that deal with the index have to know if their '%a' is actually a
* FLNode of %a, which has size nodeSize(_), or a 'plain' %a, which
* has size 1. For this, there are two version of these functions: the
* ...Elem function which is the entry point and which deals with the
* 'plain' %a, and the ...Node function, which covers the recursive
* case (from which on, all %a are FLNode of %a).
*/

-- integer_: cumulative size of all elements in list
type FLNode of %a is FLNode(integer_, list of %a);

type FLDigit of %a is alias of list of %a;

/* FLDigit */

private digitCons has type (%a, FLDigit of %a) => FLDigit of %a;
fun digitCons(a,flDigit) is __array_cons(a, flDigit);

private digitAdjoin has type (FLDigit of %a, %a) => FLDigit of %a;
fun digitAdjoin(flDigit, a) is __array_append(flDigit, a);

private digitFirst has type (FLDigit of %a) => %a;
fun digitFirst(__array_head_match(H,_)) is H;

private digitRear has type (FLDigit of %a) => FLDigit of %a;
fun digitRear(__array_head_match(_, T)) is T;

private digitLast has type (FLDigit of %a) => %a;
fun digitLast(__array_tail_match(_, E)) is E;

private digitFront has type (FLDigit of %a) => FLDigit of %a;
fun digitFront(__array_tail_match(F, _)) is F;

private digitToNodeNode has type (integer_, FLDigit of %a) => FLNode of %a;
fun digitToNodeNode(newSize, flDigit) is valof {
  -- (assert (size(flDigit)>1 and size(flDigit) < FINGER_LIST_NODE_WIDTH));
  valis FLNode(newSize, flDigit);
}

/* shallow version, does not work for FLDigit of FLNode */
private digitSizeElem has type (FLDigit of %a) => integer_;
fun digitSizeElem(flDigit) is __array_size(flDigit);

private digitSizeNode has type (FLDigit of FLNode of (%a)) => integer_;
fun digitSizeNode(flDigit) is array_int_foldLeft((s, fln) => __integer_plus(s, nodeSize(fln)), ZERO, flDigit);

/* FLNode */
private nodeSize has type (FLNode of %a) => integer_;
fun nodeSize(FLNode(v, _)) is v;

/** smart constructors */
private flNodeElem has type (list of %a) => FLNode of %a;
fun flNodeElem(els) is FLNode(__array_size(els), els);

private flNodeNode has type (list of FLNode of %a) => FLNode of FLNode of %a;
fun flNodeNode(nodes) is FLNode(sz, nodes) using {
  def sz is array_int_foldLeft((s,n) => __integer_plus(s, nodeSize(n)), ZERO, nodes);
};

private nodesFromListHelper has type ((list of %a) => %%e of %a, list of %a) => list of %%e of %a;
private fun nodesFromListHelper(nodeConstr, els0) is valof {
  def _FINGER_LIST_NODE_WIDTH_MINUS_ONE is __integer_minus(_FINGER_LIST_NODE_WIDTH, ONE)
  var els := els0;
  var res := __array_nil();
  var sz := __array_size(els);
  while not(__integer_le(sz, _FINGER_LIST_NODE_WIDTH)) do { /* sz > FINGER_LIST_NODE_WIDTH */
    res := __array_append(res, nodeConstr(__array_slice(els, ZERO, _FINGER_LIST_NODE_WIDTH)));
    els := __array_slice(els, _FINGER_LIST_NODE_WIDTH, sz);
    sz := __array_size(els);
  }
  /* sz =< FINGER_LIST_NODE_WIDTH */
  if __integer_eq(sz, _FINGER_LIST_NODE_WIDTH) then {
    res := __array_append(__array_append(res, nodeConstr(__array_slice(els, ZERO, _FINGER_LIST_NODE_WIDTH_MINUS_ONE))), nodeConstr(__array_slice(els, _FINGER_LIST_NODE_WIDTH_MINUS_ONE, sz)));
  } else {
    res := __array_append(res, nodeConstr(els));
  }
  valis res;
};

private nodesFromListElem has type (list of %a) => list of FLNode of (%a);
fun nodesFromListElem(els) is nodesFromListHelper(flNodeElem, els);

private nodesFromListNode has type (list of (FLNode of %a)) => list of (FLNode of (FLNode of %a));
fun nodesFromListNode(nodes) is nodesFromListHelper(flNodeNode, nodes);

private nodeToArray has type (FLNode of (%a)) => list of %a;
fun nodeToArray(FLNode(_, arr)) is arr;

/* FingerList of %a */
private
fun flSizeElem(FLEmpty) is ZERO
 |  flSizeElem(FLSingle(x)) is ONE
 |  flSizeElem(FLDeep(v, _, _, _)) is v

private flSizeNode has type (FingerList of (FLNode of (%a))) => integer_;
fun flSizeNode(FLEmpty) is ZERO
 |  flSizeNode(FLSingle(FLNode(v, _))) is v
 |  flSizeNode(FLDeep((v), _, _, _)) is v

private def emptyFingerList is FLEmpty;

private flIsEmpty has type (FingerList of (%a)) => boolean;
fun flIsEmpty(FLEmpty) is true
 |  flIsEmpty(_) default is false

private flNodeToDigit has type (FLNode of (%a)) => FLDigit of %a;
fun flNodeToDigit(FLNode(_, arr)) is arr;

private flDigitToLeftistTree has type (integer_, FLDigit of %a) => FingerList of (%a);
fun flDigitToLeftistTree(_, __array_empty_match()) is FLEmpty
 |  flDigitToLeftistTree(v, __array_tail_match(F, E)) default is
    (__integer_eq(__array_size(F), ZERO)
     ? FLSingle(E)
     : FLDeep(v, F, FLEmpty, __array_cons(E, __array_nil())));

private flDigitToRightistTree has type (integer_, FLDigit of %a) => FingerList of (%a);
fun flDigitToRightistTree(_, __array_empty_match()) is FLEmpty
 |  flDigitToRightistTree(v, __array_head_match(H, T)) default is
    (__integer_eq(__array_size(T), ZERO)
     ? FLSingle(H)
     : FLDeep(v, __array_cons(H, __array_nil()), FLEmpty, T));

/* dictionary */
private ptn ALL_PATTERN() from _;
private flDigitMap has type (((%a) => %b), FLDigit of %a) => FLDigit of %b;
fun flDigitMap(f, flDigit) is array_mapcar(f, flDigit);

flMap has type (((%a) => %b), (FingerList of (%a))) => FingerList of (%b)
fun flMap(f, FLEmpty) is FLEmpty
 |  flMap(f, FLSingle(a)) is FLSingle(f(a))
 |  flMap(f, FLDeep(v0, l, m, r)) is let {
      node_f has type (FLNode of (%a)) => FLNode of (%b)
      fun node_f(FLNode(v, arr)) is FLNode(v, array_mapcar(f, arr))
    } in FLDeep(v0, flDigitMap(f, l), flMap(node_f, m), flDigitMap(f, r))

flFoldLeft has type ((%b, %a) => %b, %b, FingerList of (%a)) => %b
fun flFoldLeft(f, b, FLEmpty) is b
 |  flFoldLeft(f, b, FLSingle(a01)) is f(b, a01)
 |  flFoldLeft(f, b0, FLDeep(_, l, m, r)) is valof {
      def b1 is array_foldLeft(f, b0, (l));
      def b2 is flFoldLeft(node_f, b1, m);
      def b3 is array_foldLeft(f, b2, (r));
      valis b3;
    } using {
      node_f has type (%b, FLNode of (%a)) => %b;
      fun node_f(b0_, n) is array_foldLeft(f, b0_, nodeToArray(n));
    }

flFilter has type ((%a) => boolean, FingerList of (%a)) => FingerList of (%a);
fun flFilter(p, ft) is
  helper((b, a) => (p(a) ? flAdjoin(b, a) : b), FLEmpty, ft)
using {
  helper has type ((%b, %c) => %b, %b, FingerList of (%c)) => %b;
  fun helper(f, b, FLEmpty) is b
   |  helper(f, b, FLSingle(a01)) is f(b, a01)
   |  helper(f, b0, FLDeep(_, l, m, r)) is valof {
        def b1 is helperDigit(f, b0, l);
        def b2 is helper(node_f, b1, m);
        def b3 is helperDigit(f, b2, r);
        valis b3;
      } using {
        node_f has type (%b, FLNode of (%c)) => %b;
        fun node_f(b, n) is array_foldLeft(f, b, nodeToArray(n));

        helperDigit has type((%b, %c) => %b, %b, FLDigit of %c) => %b;
        fun helperDigit(f_, b, d) is array_foldLeft(f_, b, (d));
      } 
    };

private type Promise of %a is alias of (() => %a);
private fun force(p) is p();

/** view FingerList from the left */
private type FLViewFromLeft of (%a) is
     NoFLViewFromLeft
  or ConsFLViewFromLeft(%a, Promise of (FingerList of (%a)));

private flViewFromLeftElem has type (FingerList of (%a)) => FLViewFromLeft of (%a);
fun flViewFromLeftElem(FLEmpty) is NoFLViewFromLeft
 |  flViewFromLeftElem(FLSingle(a)) is ConsFLViewFromLeft(a, memo(FLEmpty))
 |  flViewFromLeftElem(FLDeep(v, l,m,r)) is ConsFLViewFromLeft(digitFirst(l), deepL(__integer_minus(v,ONE), digitRear(l), m, r))

private flViewFromLeftNode has type (FingerList of (FLNode of %a)) => FLViewFromLeft of (FLNode of %a);
fun flViewFromLeftNode(FLEmpty) is NoFLViewFromLeft
 |  flViewFromLeftNode(FLSingle(a)) is ConsFLViewFromLeft(a, memo(FLEmpty))
 |  flViewFromLeftNode(FLDeep(v, l,m,r)) is let {
      def first is digitFirst(l);
    } in
      ConsFLViewFromLeft(first, deepL(__integer_minus(v,nodeSize(first)), digitRear(l), m, r))

private deepL has type (integer_, FLDigit of %a, FingerList of (FLNode of (%a)), FLDigit of %a) => Promise of FingerList of (%a);
fun deepL(newSize, l, m, r) is valof {
  def sz is __array_size(l);
  if __integer_eq(sz, ZERO) then {
    valis (switch flViewFromLeftNode(m) in {
      case NoFLViewFromLeft is memo(flDigitToLeftistTree(newSize, r));
      case ConsFLViewFromLeft(a, m2) is memo(FLDeep(newSize, flNodeToDigit(a), (m2()), r));
    });
  } else {
    valis memo(FLDeep(newSize, l, m, r));
  }
};

flFirst has type (FingerList of (%a)) => %a;
fun flFirst(ft) is switch flViewFromLeftElem(ft) in {
  case ConsFLViewFromLeft(a, _) is a;
};

flRear has type (FingerList of (%a)) => FingerList of (%a);
fun flRear(ft) is switch flViewFromLeftElem(ft) in {
  case ConsFLViewFromLeft(_, ft2) is (ft2());
};

/** return first and remaining elements in one operation */
flFirstRear has type (FingerList of (%a)) => (%a, FingerList of (%a));
fun flFirstRear(ft) is switch flViewFromLeftElem(ft) in {
  case ConsFLViewFromLeft(first, rear) is (first, (rear()));
}

/* view finger trees from the right */
private type FLViewFromRight of (%a) is
     NoFLViewFromRight
  or SnocFLViewFromRight(Promise of (FingerList of (%a)), %a);

private flViewFromRightElem has type (FingerList of (%a)) => FLViewFromRight of (%a);
fun flViewFromRightElem(FLEmpty) is NoFLViewFromRight
 |  flViewFromRightElem(FLSingle(a)) is SnocFLViewFromRight(memo(FLEmpty), a)
 |  flViewFromRightElem(FLDeep(v, l,m,r)) is SnocFLViewFromRight(deepR(__integer_minus(v, ONE), l, m, digitFront(r)), digitLast(r))

private flViewFromRightNode has type (FingerList of (FLNode of %a)) => FLViewFromRight of (FLNode of %a);
fun flViewFromRightNode(FLEmpty) is NoFLViewFromRight
 |  flViewFromRightNode(FLSingle(a)) is SnocFLViewFromRight(memo(FLEmpty), a)
 |  flViewFromRightNode(FLDeep(v, l,m,r)) is let {
      def last is digitLast(r);
    } in SnocFLViewFromRight(deepR(__integer_minus(v, nodeSize(last)), l, m, digitFront(r)), last)

private deepR has type (integer_, FLDigit of %a, FingerList of (FLNode of (%a)), FLDigit of %a) => Promise of FingerList of (%a);
fun deepR(newSize, l, m, r) is valof {
  def sz is __array_size(r);
  if __integer_eq(sz, ZERO) then {
    valis (switch flViewFromRightNode(m) in {
      case NoFLViewFromRight is memo(flDigitToRightistTree(newSize, l));
      case SnocFLViewFromRight(m2, a) is memo(FLDeep(newSize, l, (m2()), flNodeToDigit(a)));
    });
  } else {
    valis memo(FLDeep(newSize, l, m, r));
  }
};

flLast has type (FingerList of (%a)) => %a;
fun flLast(ft) is switch flViewFromRightElem(ft) in {
  case SnocFLViewFromRight(_, a) is a;
};

flFront has type (FingerList of (%a)) => FingerList of (%a);
fun flFront(ft) is switch flViewFromRightElem(ft) in {
  case SnocFLViewFromRight(tail, _) is (tail());
};

/** return the front and the last element of a finger tree */
flFrontLast has type (FingerList of (%a)) => (FingerList of (%a), %a);
fun flFrontLast(ft) is switch flViewFromRightElem(ft) in {
  case SnocFLViewFromRight(front, last) is ((front()), last);
};

private flAppend has type (FingerList of (%a), FingerList of (%a)) => FingerList of (%a);
fun flAppend(xs, ys) is app3Elem(xs, __array_nil(), ys) using {
  app3Elem has type (FingerList of (%a), list of %a, FingerList of (%a)) => FingerList of (%a);
  fun app3Elem(FLEmpty, ts, xs_) is array_foldRight(flConsElem, xs_, ts)
   |  app3Elem(xs_, ts, FLEmpty) is array_foldLeft(flAdjoinElem, xs_, ts)
   |  app3Elem(FLSingle(x), ts, xs_) is flConsElem(x, array_foldRight(flConsElem, xs_, ts))
   |  app3Elem(xs_, ts, FLSingle(x)) is flAdjoinElem(array_foldLeft(flAdjoinElem, xs_, ts), x)
   |  app3Elem(FLDeep(v1, pr1, m1, sf1), ts, FLDeep(v2, pr2, m2, sf2)) is
        FLDeep(__integer_plus(v1, __integer_plus(v2, __array_size(ts))), pr1, app3Node(m1, (nodesFromListElem(__array_concatenate((sf1), __array_concatenate(ts, (pr2))))), m2), sf2)

  app3Node has type (FingerList of (FLNode of %c), list of (FLNode of %c), FingerList of (FLNode of %c)) => FingerList of (FLNode of %c);
  fun app3Node(FLEmpty, ts, xs_) is array_foldRight(flConsNode, xs_, ts)
   |  app3Node(xs_, ts, FLEmpty) is array_foldLeft(flAdjoinNode, xs_, ts)
   |  app3Node(FLSingle(x), ts, xs_) is flConsNode(x, array_foldRight(flConsNode, xs_, ts))
   |  app3Node(xs_, ts, FLSingle(x)) is flAdjoinNode(array_foldLeft(flAdjoinNode, xs_, ts), x)
   |  app3Node(FLDeep(v1, pr1, m1, sf1), ts, FLDeep(v2, pr2, m2, sf2)) is
        FLDeep(__integer_plus(v1, __integer_plus(v2, sumNodeSize(ts))), pr1, app3Node(m1, (nodesFromListNode(__array_concatenate((sf1), __array_concatenate(ts, (pr2))))), m2), sf2)

  sumNodeSize has type (list of (FLNode of %c)) => integer_;
  fun sumNodeSize(s) is array_int_foldLeft((a,b) => __integer_plus(a,nodeSize(b)), ZERO, s);
}

/********************************************************************************************/
/********************************************************************************************/
/********************************************************************************************/


private flConsElem has type (%a, FingerList of (%a)) => FingerList of (%a);
fun flConsElem(a, FLEmpty) is FLSingle(a)
 |  flConsElem(a, FLSingle(b)) is FLDeep((TWO), __array_cons(a, __array_nil()), FLEmpty, __array_cons(b, __array_nil()))
 |  flConsElem(a, FLDeep((v), l, m, r)) is valof {
      var sz := __array_size(l);
      if __integer_eq(sz, _FINGER_LIST_NODE_WIDTH) then {
        def sr  is digitSizeElem(r);
        def sm  is flSizeNode(m);
        valis FLDeep(__integer_plus(v,ONE), digitCons(a,__array_slice(l, ZERO, ONE)), flConsNode(FLNode(__integer_minus(__integer_minus(__integer_minus(v,ONE),sr),sm), __array_slice(l, ONE, sz)), m), r);
      } else {
        valis FLDeep(__integer_plus(v,ONE), digitCons(a, l), m, r);
      }
    };

private flConsNode has type ( FLNode of (%a), FingerList of (FLNode of (%a))) => FingerList of (FLNode of (%a));
fun flConsNode(na, FLEmpty) is FLSingle(na)
 |  flConsNode(na, FLSingle(nb)) is valof {
      def sa is (nodeSize(na));
      def sb is (nodeSize(nb));
      valis FLDeep(__integer_plus(sa,sb), __array_cons(na, __array_nil()), FLEmpty, __array_cons(nb, __array_nil()));
    }
 |  flConsNode(na, FLDeep((sb), l, m, r)) is valof {
      def sl is __array_size(l);
      if (__integer_eq(sl, _FINGER_LIST_NODE_WIDTH)) then {
        def sa is (nodeSize(na));
        def sa01 is nodeSize(__array_el(l, ZERO));
        def sm is flSizeNode(m);
        def sr is digitSizeNode(r);
        valis FLDeep(__integer_plus(sa,sb), digitCons(na, __array_slice(l, ZERO, ONE)), flConsNode(FLNode(__integer_minus(__integer_minus(__integer_minus(sb,sa01),sm),sr), __array_slice(l, ONE, sl)), m), r);
      } else {
        def sa is (nodeSize(na));
        valis FLDeep(__integer_plus(sa,sb), digitCons(na, l), m, r);
      }
    };

private flAdjoinElem has type (FingerList of (%a), %a) => FingerList of (%a);
fun flAdjoinElem(FLEmpty, a) is FLSingle(a)
 |  flAdjoinElem(FLSingle(b), a) is FLDeep((TWO), __array_cons(b, __array_nil()), FLEmpty, __array_cons(a, __array_nil()))
 |  flAdjoinElem(FLDeep((v), l, m, r), a) is valof {
      def sr is __array_size(r);
      if (__integer_eq(sr, _FINGER_LIST_NODE_WIDTH)) then {
        def sl is digitSizeElem(l);
        def sm is flSizeNode(m);
        def last is __integer_minus(sr, ONE);
        valis FLDeep(__integer_plus(v,ONE), l, flAdjoinNode(m, FLNode(__integer_minus(__integer_minus(__integer_minus(v,ONE),sl),sm), __array_slice(r, ZERO, last))), digitAdjoin(__array_slice(r, last, sr), a));
      } else {
        valis FLDeep(__integer_plus(v,ONE), l, m, digitAdjoin(r, a));
      }
    }

private flAdjoinNode has type (FingerList of (FLNode of (%a)),FLNode of (%a)) => FingerList of (FLNode of (%a));
fun flAdjoinNode(FLEmpty, na) is FLSingle(na)
 |  flAdjoinNode(FLSingle(nb), na) is valof {
      def sa is (nodeSize(na));
      def sb is (nodeSize(nb));
      valis FLDeep(__integer_plus(sa,sb), __array_cons(nb, __array_nil()), FLEmpty, __array_cons(na, __array_nil()));
    }
 |  flAdjoinNode(FLDeep((v), l, m, r), a) is valof {
      def sa is (nodeSize(a));
      def sr is __array_size(r);
      if __integer_eq(sr, _FINGER_LIST_NODE_WIDTH) then {
        def sl is digitSizeNode(l);
        def sm is flSizeNode(m);
        def last is __integer_minus(_FINGER_LIST_NODE_WIDTH, ONE);
        def sa08 is nodeSize(__array_el(r, last));
        valis FLDeep(__integer_plus(v,sa), l, flAdjoinNode(m, FLNode(__integer_minus(__integer_minus(__integer_minus(v,sl),sm),sa08), __array_slice(r,ZERO, last))), digitAdjoin(__array_slice(r, last, sr), a));
      } else {
        valis FLDeep(__integer_plus(v,sa), l, m, digitAdjoin(r, a));
      }
    };

/** splitting */
private flSplitElem has type (FingerList of (%a), integer_) => %a;
fun flSplitElem(FLSingle(x), idx) is x
 |  flSplitElem(FLDeep(_, pr, m, sf), idx) is valof {
      def vpr is digitSizeElem(pr);
      if not(__integer_ge(idx, vpr)) then { -- idx < vpr
        valis flSplitDigitElem(idx, ZERO, pr);
      };
      def vm is __integer_plus(vpr, flSizeNode(m));
      if not(__integer_ge(idx, vm)) then { -- idx < vm
        def (integer(sml), xs) is flSplitNode(idx, vpr, m); -- work around starbug #8331 returning integer
        valis flSplitDigitElem(idx, __integer_plus(vpr, sml), flNodeToDigit(xs));
      };
      valis flSplitDigitElem(idx, vm, sf);
    };

private flSplitDigitElem has type (integer_, integer_, FLDigit of %a) => %a;
fun flSplitDigitElem(idx, i, digit) is __array_el(digit, __integer_minus(idx, i));

private flSplitNode has type (integer_, integer_, FingerList of (FLNode of (%a))) => (integer, FLNode of (%a)); -- work around starbug #8331 returning integer
fun flSplitNode(idx, i, FLSingle(x)) is (integer(ZERO), x) -- work around starbug #8331 returning integer
 |  flSplitNode(idx, i, FLDeep(_, pr, m, sf)) is valof {
      def spr is digitSizeNode(pr);
      def vpr is __integer_plus(i, spr);
      if not(__integer_ge(idx, vpr)) then { -- idx < vpr
        def (integer(sl), x) is flSplitDigitNode(idx, i, pr); -- work around starbug #8331 returning integer
        valis (integer(sl), x); -- work around starbug #8331 returning integer
      };
      def sm is flSizeNode(m);
      def vm  is __integer_plus(vpr, sm);
      if not(__integer_ge(idx, vm)) then { -- idx < vm
        def (integer(sml), xs) is flSplitNode(idx, vpr, m); -- work around starbug #8331 returning integer
        def (integer(sl), x)    is flSplitDigitNode(idx, __integer_plus(vpr, sml), flNodeToDigit(xs)); -- work around starbug #8331 returning integer
        valis (integer(__integer_plus(spr, __integer_plus(sml, sl))), x); -- work around starbug #8331 returning integer
      };
      def (integer(sl), x) is flSplitDigitNode(idx, vm, sf); -- work around starbug #8331 returning integer
      valis (integer(__integer_plus(spr, __integer_plus(sm, sl))), x) -- work around starbug #8331 returning integer
    };

private flSplitDigitNode has type (integer_, integer_, FLDigit of FLNode of (%a)) => (integer, FLNode of (%a)); -- work around starbug #8331 returning integer
fun flSplitDigitNode(idx, i00, flDigit) is valof {
  var i := ZERO;
  var consumed := ZERO;
  var ns := nodeSize(__array_el(flDigit, i));
  var ir := __integer_plus(i00, ns);
  def flDigitSizeMinusOne is __integer_minus(__array_size(flDigit), ONE);
  while not(__integer_ge(i, flDigitSizeMinusOne)) do { -- i < flDigitSize - 1;
    if not(__integer_ge(idx, ir)) then { -- idx < ir
      valis (integer(consumed), __array_el(flDigit, i)); -- work around starbug #8331 returning integer
    };
    i := __integer_plus(i, ONE);
    consumed := __integer_plus(consumed, ns);
    ns := nodeSize(__array_el(flDigit, i));
    ir := __integer_plus(ir, ns);
  };
  valis (integer(consumed), __array_el(flDigit, i));
};

private flSubstituteElem has type (FingerList of %a, integer_, %a) => FingerList of %a;
fun flSubstituteElem(FLSingle(x), idx, newElem) is FLSingle(newElem)
 |  flSubstituteElem(FLDeep(v, pr, m, sf), idx, newElem) is valof {
      def vpr is digitSizeElem(pr);
      if not(__integer_ge(idx, vpr)) then { -- idx < vpr
        valis FLDeep(v, flSubstituteDigitElem(idx, ZERO, pr, newElem), m, sf);
      };
      def vm is __integer_plus(vpr, flSizeNode(m));
      if not(__integer_ge(idx, vm)) then { -- idx < vm
        def newM is flSubstituteNode(idx, vpr, m, subsNode);
        valis FLDeep(v, pr, newM, sf);
      };
      valis FLDeep(v, pr, m, flSubstituteDigitElem(idx, vm, sf, newElem));
    } using {
      fun subsNode(idx_, i_, FLNode(sz, els)) is
        FLNode(sz, flSubstituteDigitElem(idx_, i_, els, newElem));
    };

private flSubstituteDigitElem has type (integer_, integer_, FLDigit of %a, %a) => FLDigit of %a;
fun flSubstituteDigitElem(idx, i, flDigit, newElem) is __array_set_element(flDigit, __integer_minus(idx,i), newElem);

private flSubstituteNode has type (integer_, integer_, FingerList of FLNode of %a,
 (integer_, integer_, FLNode of %a) => FLNode of %a) => FingerList of FLNode of %a;
fun flSubstituteNode(idx, i, FLSingle(x), subs) is FLSingle(subs(idx, i, x))
 |  flSubstituteNode(idx, i, FLDeep(v, pr, m, sf), subs) is valof {
      def spr is digitSizeNode(pr);
      def vpr is __integer_plus(i, spr);
      if not(__integer_ge(idx, vpr)) then { -- idx < vpr
        def newPr is flSubstituteDigitNode(idx, i, pr, subs);
        valis FLDeep(v, newPr, m, sf);
      };
      def sm is flSizeNode(m);
      def vm  is __integer_plus(vpr, sm);
      if not(__integer_ge(idx, vm)) then { -- idx < vm
        def newM is flSubstituteNode(idx, vpr, m, subsNode);
        valis FLDeep(v, pr, newM, sf);
      };
      def newSf is flSubstituteDigitNode(idx, vm, sf, subs);
      valis FLDeep(v, pr, m, newSf);
    } using {
      fun subsNode(idx_, i_, FLNode(sz, els)) is
        FLNode(sz, flSubstituteDigitNode(idx_, i_, els, subs));
    };

private flSubstituteDigitNode has type (integer_, integer_, FLDigit of FLNode of %a,
  (integer_, integer_, FLNode of %a) => FLNode of %a) => FLDigit of FLNode of %a;
fun flSubstituteDigitNode(idx, i00, flDigit, subs) is valof {
  var i := ZERO;
  var consumed := i00;
  var ns := nodeSize(__array_el(flDigit, i));
  var ir := __integer_plus(i00, ns);
  def flDigitSizeMinusOne is __integer_minus(__array_size(flDigit), ONE);
  while not(__integer_ge(i, flDigitSizeMinusOne)) do { -- i < flDigitSize - 1;
    if (not(__integer_ge(idx, ir))) then { -- idx < ir
      valis __array_set_element(flDigit, i, subs(idx, consumed, __array_el(flDigit, i)));
    };
    i := __integer_plus(i, ONE);
    consumed := __integer_plus(consumed, ns);
    ns := nodeSize(__array_el(flDigit, i));
    ir := __integer_plus(ir, ns);
  };
  valis __array_set_element(flDigit, i, subs(idx, consumed, __array_el(flDigit, i)));
};

/** splits **/
private type Split of (%f, %a) is Split(%f, %a, %f);

private flFullSplitDigitElem has type (integer_, integer_, FLDigit of %a) => Split of (FLDigit of %a, %a);
fun flFullSplitDigitElem(idx, i, flDigit) is
  Split(__array_slice(flDigit, ZERO, pos),
        __array_el(flDigit, pos),
        __array_slice(flDigit, __integer_plus(pos, ONE), sz))
  using {
    def pos is __integer_minus(idx, i);
    def sz is __array_size(flDigit);
  };


private flFullSplitDigitNode has type (integer_, integer_, FLDigit of FLNode of %a) => Split of (FLDigit of FLNode of %a, FLNode of %a);
fun flFullSplitDigitNode(idx, i00, flDigit) is valof {
  var i := ZERO;
  var i2 := __integer_plus(i00, nodeSize(__array_el(flDigit,i)));
  var szMinusOne := __integer_minus(__array_size(flDigit), ONE);
  while __integer_le(i2, idx) and not(__integer_ge(i, szMinusOne)) do { -- i2 =< idx and i < szMinusOne
    i := __integer_plus(i, ONE);
    i2 := __integer_plus(i2, nodeSize(__array_el(flDigit, i)));
  };
  valis Split(__array_slice(flDigit, ZERO, i),
              __array_el(flDigit, i),
              __array_slice(flDigit, __integer_plus(i, ONE), __array_size(flDigit)));
};

/** split the list at given position, returning the list left of that
* position, the element at that position, and the list right of that
* position */
private flSplit has type (FingerList of (%a), integer) => (FingerList of %a, %a, FingerList of %a);
fun flSplit(fl, integer(pos)) is valof {
  def Split(l,x,r) is flSplitTreeElem(pos, ZERO, fl);
  valis (l, x, r)
};

private flSplitTreeElem has type (integer_, integer_, FingerList of (%a)) => Split of (FingerList of (%a), %a);
fun flSplitTreeElem(idx, i, FLSingle(x)) is Split(FLEmpty, x, FLEmpty)
 |  flSplitTreeElem(idx, i, FLDeep(v, pr, m, sf)) is valof {
      def vpr is (__integer_plus(i, digitSizeElem(pr)));
      if not(__integer_ge(idx, vpr)) then { -- idx < vpr
        def Split(l, x, r) is flFullSplitDigitElem(idx, i, pr);
        def lSize is digitSizeElem(l);
        valis Split(flDigitToLeftistTree(lSize, l), x, (deepL(__integer_minus(__integer_minus(v, lSize),ONE), r, m, sf)()));
      }
      def vm  is (__integer_plus(vpr, flSizeNode(m)));
      if not(__integer_ge(idx, vm)) then { -- idx < vm
        def Split(ml, xs, mr) is flSplitTreeNode(idx, vpr, m);
        def mlSize is flSizeNode(ml);
        def Split(l, x, r)    is flFullSplitDigitElem(idx, (__integer_plus(vpr, mlSize)), flNodeToDigit(xs));
        valis Split((deepR(__integer_plus(digitSizeElem(pr), __integer_plus(mlSize, digitSizeElem(l))), pr, ml, l)()),
                  x,
                  (deepL(__integer_plus(digitSizeElem(r), __integer_plus(flSizeNode(mr), digitSizeElem(sf))), r, mr, sf)()));
      }
      def Split(l, x, r) is flFullSplitDigitElem(idx, vm, sf);
      def rSize is digitSizeElem(r);
      valis Split((deepR(__integer_minus(__integer_minus(v, ONE), rSize), pr, m, l)()), x, flDigitToRightistTree(rSize, r))
    };

private flSplitTreeNode has type (integer_, integer_, FingerList of (FLNode of %a)) => Split of (FingerList of (FLNode of %a), FLNode of %a);
fun flSplitTreeNode(idx, i, FLSingle(x)) is Split(FLEmpty, x, FLEmpty)
 |  flSplitTreeNode(idx, i, FLDeep(v, pr, m, sf)) is valof {
      def vpr is (__integer_plus(i, digitSizeNode(pr)));
      if not(__integer_ge(idx, vpr)) then { -- idx < vpr
        def Split(l, x, r) is flFullSplitDigitNode(idx, i, pr);
        def lSize is digitSizeNode(l);
        valis Split(flDigitToLeftistTree(lSize, l), x, (deepL(__integer_minus(__integer_minus(v,lSize),nodeSize(x)), r, m, sf)()));
      }
      def vm  is (__integer_plus(vpr, flSizeNode(m)));
      if not(__integer_ge(idx, vm)) then { -- idx < vm
        def Split(ml, xs, mr) is flSplitTreeNode(idx, vpr, m);
        def mlSize is flSizeNode(ml);
        def Split(l, x, r)    is flFullSplitDigitNode(idx, (__integer_plus(vpr, mlSize)), flNodeToDigit(xs));
        valis Split((deepR(__integer_plus(digitSizeNode(pr), __integer_plus(mlSize, digitSizeNode(l))), pr, ml, l)()),
                  x,
                  (deepL(__integer_plus(digitSizeNode(r), __integer_plus(flSizeNode(mr), digitSizeNode(sf))), r, mr, sf)()));
      }
      def Split(l, x, r) is flFullSplitDigitNode(idx, vm, sf);
      def rSize is digitSizeNode(r);
      valis Split((deepR(__integer_minus(__integer_minus(v,nodeSize(x)), rSize), pr, m, l)()), x, flDigitToRightistTree(rSize, r))
    };

/** remove **/

private flRemoveElem has type (FingerList of %a, integer_) => FingerList of %a;
fun flRemoveElem(fl, idx) is valof {
  def Split(l, _, r) is flSplitTreeElem(idx, ZERO, fl);
  valis flAppend(l, r);
}

private flSize has type (FingerList of %a) => integer_;
fun flSize(xs) is flSizeElem(xs);

private flCons has type (%a, FingerList of %a) => FingerList of %a;
fun flCons(a, s) is flConsElem(a, s);

private flAdjoin has type (FingerList of %a, %a) => FingerList of %a;
fun flAdjoin(s, a) is flAdjoinElem(s, a);

flToCons has type (FingerList of %a) => cons of %a;
-- FIXME optimized version for performance
fun flToCons(s) is reverse(flFoldLeft((l,a) => cons(a,l), nil, s));

flFromCons has type (cons of %a) => FingerList of %a;
-- FIXME optimized version for performance
fun flFromCons(l) is leftFold(flAdjoin, FLEmpty, l);

private flIdx has type (FingerList of %a, integer) => %a;
fun flIdx(xs, integer(i)) is flSplitElem(xs, i);

private flEqual has type (FingerList of %a, FingerList of %a) => boolean where equality over %a;
fun flEqual(fl1, fl2) is valof {
  var st := (flViewFromLeftElem(fl1),flViewFromLeftElem(fl2));
  while true do {
    switch st in {
      case (NoFLViewFromLeft, NoFLViewFromLeft) do valis (true);
      case (NoFLViewFromLeft, ConsFLViewFromLeft(_,_)) do valis (false);
      case (ConsFLViewFromLeft(_,_), NoFLViewFromLeft) do valis (false);
      case (ConsFLViewFromLeft(h1,t1), ConsFLViewFromLeft(h1,t2)) do st := ((flViewFromLeftElem(force(t1)), flViewFromLeftElem(force(t2))));
      case (ConsFLViewFromLeft(h1,t1), ConsFLViewFromLeft(h2,t2)) default do valis (false);
    }
  };
}

/* replace value at indexed position */
private flSubstitute has type (FingerList of %a, integer, %a) => FingerList of %a;
fun flSubstitute(xs, integer(i), a) is flSubstituteElem(xs, i, a);

/* remove element at indexed position */
private flRemove has type (FingerList of %a, integer) => FingerList of %a;
fun flRemove(xs, integer(i)) is flRemoveElem(xs, i);

private
fun flCheckIndex(fl, Ix) is __integer_ge(Ix, ZERO) and (not(__integer_ge(Ix, flSizeElem(fl)))); -- Ix < flSizeElem(fl)

private
prc flRaiseIfIndexCheckFails(fl, Ix) do
  if not flCheckIndex(fl, Ix) then { raise "index out of range"; };

/* returns -1, 0, +1 for less than, equal or greater than
*  cannot easily have own type in star.star */
private flCompare has type (FingerList of %a, FingerList of %a) => integer where comparable over %a;
fun flCompare(fl1, fl2) is valof {
  var st := (flViewFromLeftElem(fl1),flViewFromLeftElem(fl2));
  while true do {
    switch st in {
      case (NoFLViewFromLeft, NoFLViewFromLeft) do valis (0);
      case (NoFLViewFromLeft, ConsFLViewFromLeft(_,_)) do valis (-1);
      case (ConsFLViewFromLeft(_,_), NoFLViewFromLeft) do valis (1);
      case (ConsFLViewFromLeft(h1,t1), ConsFLViewFromLeft(h2,t2)) default do {
        if h1 < h2 then {
          valis (-1)
        } else if h1 > h2 then {
          valis (1);
        } else {
          st := ((flViewFromLeftElem(force(t1)), flViewFromLeftElem(force(t2))));
        }
      }
    }
  }
};

/** contracts */
implementation pPrint over FingerList of %t where pPrint over %t is {
  fun ppDisp(fl) is ppSequence(0,cons(ppStr("fl of ["),cons(ppSequence(2,dispFtl(fl,ppStr(""))),cons(ppStr("]"),nil))));
} using {
  def inter is ppStr(",");
  fun dispFtl(fl, sep0) is let {
    def (res, _) is flFoldLeft(
                  (((d, sep), t) => (cons(ppDisp(t), cons(sep, d)), inter)),
                  (nil,sep0),
                  fl);
    } in reverse(res);
}

implementation equality over FingerList of %e where equality over %e is {
  (=) = flEqual;
};

implementation sequence over FingerList of %e determines %e is {
  fun _cons(H,T) is flCons(H, T);
  fun _apnd(T,H) is flAdjoin(T, H);
  ptn _empty() from FLEmpty;
  ptn _pair(H,T) from fl where flFirstRear(fl) matches (H,T);
  ptn _back(T, H) from fl where flFrontLast(fl) matches (T,H);
  fun _nil() is FLEmpty;
};

/* only return new values */
implementation indexable over FingerList of %e determines (integer,%e) is {
  fun _index(fl,integer(Ix)) is flCheckIndex(fl,Ix) ? some(flSplitElem(fl, Ix)) : none;
  fun _set_indexed(fl,integer(Ix),E) is valof {flRaiseIfIndexCheckFails(fl, Ix); valis flSubstituteElem(fl, Ix, E)};
  fun _delete_indexed(fl,integer(Ix)) is valof {flRaiseIfIndexCheckFails(fl, Ix); valis flRemoveElem(fl, Ix)};
};

implementation updateable over FingerList of %t determines %t is {
  fun _extend(L,E) is flCons(E, L);
  fun _merge(L,R) is flAppend(L,R);
  fun _delete(R,P) is flDelete(R,P);
  fun _update(R,M,U) is flUpdate(R,M,U);
} using {
  fun flDelete(R,P) is flFilter(filter_, R) using {
    fun filter_(E) where E matches P() is false
     |  filter_(_) default is true;
  };

  fun flUpdate(R,M,U) is flMap(map_, R) using {
    fun map_(E) where E matches M() is U(E)
     |  map_(E) default is E;
  };
};

implementation iterable over FingerList of %a determines %a is {
  fun _iterate(L,F,S) is flIterate(L,F,S);
};

private
fun flIterate(FLEmpty,_,St) is St
 |  flIterate(_,_,NoMore(X)) is NoMore(X)
 |  flIterate(fl,F,St) default is
    switch flViewFromLeftElem(fl) in {
      case NoFLViewFromLeft is St;
      case ConsFLViewFromLeft(head, tail) is
        -- delay forcing tail until necessary
        switch F(head, St) in {
          case NoMore(X) is NoMore(X);
          case St2 default is flIterate(force(tail),F,St2);
        }
      }

implementation indexed_iterable over FingerList of %a determines (integer, %a) is {
  fun _ixiterate(M,F,S) is flIxIterate(M,F,S,ZERO);
} using {
  fun flIxIterate(FLEmpty,_,St,_) is St
   |  flIxIterate(_,_,NoMore(X),_) is NoMore(X)
   |  flIxIterate(fl,F,St,Ix) default is
        switch flViewFromLeftElem(fl) in {
          case NoFLViewFromLeft is St;
          case ConsFLViewFromLeft(head, tail) is
            -- delay forcing tail until necessary
            switch F(integer(Ix), head, St) in {
              case NoMore(X) is NoMore(X);
              case St2 default is flIxIterate(force(tail),F,St2, __integer_plus(Ix, ONE));
            }
          }
      };

implementation comparable over FingerList of %a where comparable over %a is {
  (<) = lt;
  (=<) = le;
  (>) = gt;
  (>=) = ge;
} using {
  fun lt(x, y) is
    switch flCompare(x, y) in {
       case -1 is true;
       case 0 is false;
       case 1 is false;
    };
  fun le(x, y) is
    switch flCompare(x, y) in {
       case -1 is true;
       case 0 is true;
       case 1 is false;
    };
  fun gt(x, y) is
    switch flCompare(x, y) in {
       case -1 is false;
       case 0 is false;
       case 1 is true;
    };
  fun ge(x, y) is
    switch flCompare(x, y) in {
       case -1 is false;
       case 0 is true;
       case 1 is true;
    };
};

implementation sliceable over FingerList of %a determines integer is {
  fun _slice(L,integer(Fr),integer(To)) is valof {
        def Split(_,m1,r1) is flSplitTreeElem(Fr, ZERO, L)
        def Split(l2,m2,_) is flSplitTreeElem(__integer_minus(To, Fr),ZERO,flCons(m1, r1));
        valis l2;
      }
  fun _splice(L,integer(Fr),integer(To),R) is valof {
        def Split(l1,m1,r1) is flSplitTreeElem(Fr, ZERO, L)
        def Split(l2,m2,r2) is flSplitTreeElem(__integer_minus(To, Fr),ZERO,flCons(m1,r1));
        valis flAppend(l1,flAppend(R, flCons(m2, r2)));
      }
  fun _tail(L,integer(Fr)) is valof {
    def Split(l, m, r) is flSplitTreeElem(Fr, ZERO, L);
    valis flCons(m,r);
  }
}

implementation sizeable over FingerList of %e is {
  fun size(L) is integer(flSize(L));
  fun isEmpty(L) is flIsEmpty(L);
}

