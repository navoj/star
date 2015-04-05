/*
 * Functional red-black search trees
 *
 * See Red-Black Trees in a Functional Setting by Chris
 * Okasaki. Journal of Functional Programming, 9(4):471-477, July 1999.
 * (variant from Stefan Kahrs)
 *
 * Red-black trees are binary search trees obeying two key invariants:
 * - Any path from a root node to a leaf node contains the same number
 *   of black nodes.
 * - Red nodes always have black children.
 */
 
RedBlackTree is package {
import Prelude;
import OddStream;
import Promise; -- needed to support oddCons

type Color is RBR or RBB;
type RedBlackTree of %a is
    RBE
 or RBT(Color, RedBlackTree of %a, %a, RedBlackTree of %a);

-- #### work around bug in Star compiler
unpackRedBlackTree(RBT(c, tl, a, tr)) is (c, tl, a, tr);

rbEmpty has type RedBlackTree of %a;
rbEmpty is RBE;

rbIsEmpty has type (RedBlackTree of %a) => boolean;
rbIsEmpty(rbt) is rbt = RBE;

-- Sets

-- rbInsert has type (RedBlackTree of %a, %a) => RedBlackTree of %a where comparable over %a;

/* hits Star compiler bug
rbInsert(s, x) is
	let {
		(_, a, z, b) is unpackRedBlackTree(
			let {
				ins(RBE) is RBT(RBR, RBE, x, RBE);
				ins(s where s matches RBT(RBB, a, y, b)) is
					(x <= y) -- too many comparisons
					? ((x >= y) ? s | rbBalance(ins(a), y, b))
					| rbBalance(a, y, ins(b));
				ins(s where s matches RBT(RBR, a, y, b)) is
					(x <= y)
					? ((x >= y) ? s | RBT(RBR, ins(a), y, b))
					| RBT(RBR, a, y, ins(b));
			} in ins(s));
	} in RBT(RBB, a, z, b);
	
*/

-- Workaround from Wonsoek:
rbInsertHelper(RBE,x) is RBT(RBR, RBE, x, RBE);
rbInsertHelper(s where s matches RBT(RBB, a, y, b),x) is
    (x <= y) -- too many comparisons
    ? ((x >= y) ? s | rbBalance(rbInsertHelper(a,x), y, b))
    | rbBalance(a, y, rbInsertHelper(b,x));
rbInsertHelper(s where s matches RBT(RBR, a, y, b),x) is
    (x <= y)
    ? ((x >= y) ? s | RBT(RBR, rbInsertHelper(a,x), y, b))
    | RBT(RBR, a, y, rbInsertHelper(b,x));

rbInsert(s0, x) is let {
        (_, a0, z0, b0) is unpackRedBlackTree(rbInsertHelper(s0,x));
    } in RBT(RBB, a0, z0, b0);

-- type declaration triggers compiler bug
-- rbMember has type (%a , RedBlackTree of %a) => boolean where comparable over %a;
rbMember(x, RBE) is false;
rbMember(x, RBT(_, a, y, b)) is
	(x <= y)
	? ((x >= y) ? true | rbMember(x, a))
	| rbMember(x, b);

rbBalance has type (RedBlackTree of %a, %a, RedBlackTree of %a)
     => RedBlackTree of %a;
rbBalance(RBT(RBR, a, x, b), y, RBT(RBR, c, z, d)) is RBT(RBR, RBT(RBB, a, x, b), y, RBT(RBB, c, z, d));
rbBalance(left, e, right) default is
	(left matches RBT(RBR, RBT(RBR, a, x, b), y, c))
	? RBT(RBR, RBT(RBB, a, x, b), y, RBT(RBB, c, e, right))
	| ((left matches RBT(RBR, a, x, RBT(RBR, b, y, c)))
		? RBT(RBR, RBT(RBB, a, x, b), y, RBT(RBB, c, e, right))
		| ((right matches RBT(RBR, b, y, RBT(RBR, c, z, d)))
			? RBT(RBR, RBT(RBB, left, e, b), y, RBT(RBB, c, z, d))
			| ((right matches RBT(RBR, RBT(RBR, b, y, c), z, d))
				? RBT(RBR, RBT(RBB, left, e, b), y, RBT(RBB, c, z, d))
				| RBT(RBB, left, e, right))));


/*
rbBalance(RBT(RBR, RBT(RBR, a, x, b), y, c), z, d) is RBT(RBR, RBT(RBB, a, x, b), y, RBT(RBB, c, z, d));
rbBalance(RBT(RBR, a, x, RBT(RBR, b, y, c)), z, d) is RBT(RBR, RBT(RBB, a, x, b), y, RBT(RBB, c, z, d));
rbBalance(a, x, RBT(RBR, b, y, RBT(RBR, c, z, d))) is RBT(RBR, RBT(RBB, a, x, b), y, RBT(RBB, c, z, d));
rbBalance(a, x, RBT(RBR, RBT(RBR, b, y, c), z, d)) is RBT(RBR, RBT(RBB, a, x, b), y, RBT(RBB, c, z, d))
rbBalance(a, x, b) default is RBT(RBB, a, x, b); 
*/

-- rbDelete has type (RedBlackTree of %a , %a) => RedBlackTree of %a where comparable over %a;
/*
-- triggers bug in Star compiler ...
rbDelete(t, x) is
	(case del(t) in {
		RBT(_, a, y, b) is RBT(RBB, a, y, b)
	} default RBE)
	using {
		del(RBE) is RBE;
		del(RBT(_, a, y, b)) is
			(x <= y)
			? ((x >= y) ? rbAppend(a, b) | delLeft(a, y, b))
			| delRight(a, y, b);
		delLeft(a matching RBT(RBB, _, _, _), y, b) is rbBalLeft(del(a), y, b);
		delLeft(a, y, b) default is RBT(RBR, del(a), y, b);
		delRight(a, y, b matching RBT(B, _, _, _)) is rbBalRight(a, y, del (b));
		delRight(a, y, b) default is RBT(RBR, a, y, del(b));
	};
*/


rbDelete(t, x) is
	case rbDeleteHelper(t, x) in {
		RBT(_, a, y, b) is RBT(RBB, a, y, b)
	} default RBE;

/*	
-- this attempt at a workaround triggers another bug in the Star compiler

rbDeleteHelper has type (RedBlackTree of %a , %a) => RedBlackTree of %a where comparable over %a;
rbDeleteHelper(RBE, x) is RBE;
rbDeleteHelper(RBT(_, a, y, b), x) is
	(x <= y)
	? ((x >= y) ? rbAppend(a, b) | rbDeleteHelperLeft(a, y, b, x))
	| rbDeleteHelperRight(a, y, b, x);
rbDeleteHelperLeft(a matching RBT(RBB, _, _, _), y, b, x) is
	rbBalLeft(rbDeleteHelper(a, x), y, b);
rbDeleteHelperLeft(a, y, b, x) default is RBT(RBR, rbDeleteHelper(a, x), y, b);
rbDeleteHelperRight(a, y, b matching RBT(B, _, _, _), x) is
	rbBalRight(a, y, rbDeleteHelper(b, x));
rbDeleteHelperRight(a, y, b, x) default is
	RBT(RBR, a, y, rbDeleteHelper(b, x));
*/

-- workaround by Wonsoek - inlining seems to help
-- rbDeleteHelper has type (RedBlackTree of %a , %a) => RedBlackTree of %a where comparable over %a;
rbDeleteHelper(RBE, x) is RBE;
rbDeleteHelper(RBT(_, a, y, b), x) is
	(x <= y)
    ? ((x >= y)
        ? rbAppend(a, b)
        | ( a matches RBT(RBB,_,_,_)
            ? rbBalLeft(rbDeleteHelper(a, x), y, b)
            | RBT(RBR, rbDeleteHelper(a, x), y, b)))
    | (b matches RBT(RBB, _, _, _)
       ? rbBalRight(a, y, rbDeleteHelper(b, x))
       | RBT(RBR, a, y, rbDeleteHelper(b, x)));

rbBalLeft has type (RedBlackTree of %a, %a, RedBlackTree of %a)
	 => RedBlackTree of %a;
rbBalLeft(left, e, right) is
	case left in {
		RBT(RBR, a, x, b) is RBT(RBR, RBT(RBB, a, x, b), e, right)
	} default
		(case right in {
			RBT(RBB, a, y, b) is rbBalance(left, e, RBT(RBR, a, y, b));
			RBT(RBR, RBT(RBB, a, y, b), z, c) is
				RBT(RBR, RBT(RBB, left, e, a), y, rbBalance(b, z, rbSub1(c)));
		});
		
/*
rbBalLeftX(RBT(RBR, a, x, b), y, c) is RBT(RBR, RBT(RBB, a, x, b), y, c);
rbBalLeftX(bl, x, RBT(RBB, a, y, b)) is rbBalance(bl, x, RBT(RBR, a, y, b));
rbBalLeftX(bl, x, RBT(RBR, RBT(RBB, a, y, b), z, c)) is	
	RBT(RBR, RBT(RBB, bl, x, a), y, rbBalance(b, z, rbSub1(c)));
*/

rbBalRight has type (RedBlackTree of %a, %a, RedBlackTree of %a)
	=> RedBlackTree of %a;
rbBalRight(left, e, right) is
	case right in {
		RBT(RBR, b, y, c) is RBT(RBR, left, e, RBT(RBB, b, y, c));
	} default
		(case left in {
			RBT(RBB, a, x, b) is rbBalance(RBT(RBR, a, x, b), e, right);
			RBT(RBR, a, x, RBT(RBB, b, y, c)) is
				RBT(RBR, rbBalance(rbSub1(a), x, b), y, RBT(RBB, c, e, right));
		});
/*
rbBalRightX(a, x, RBT(RBR, b, y, c)) is RBT(RBR, a, x, RBT(RBB, b, y, c));
rbBalRightX(RBT(RBB, a, x, b), y, bl) is rbBalance(RBT(RBR, a, x, b), y, bl);
rbBalRightX(RBT(RBR, a, x, RBT(RBB, b, y, c)), z, bl) is
	RBT(RBR, rbBalance(rbSub1(a), x, b), y, RBT(RBB, c, z, bl))
*/
rbSub1 has type (RedBlackTree of %a) => RedBlackTree  of %a
rbSub1(RBT(RBB, a, x, b)) is RBT(RBR, a, x, b);

rbAppend has type (RedBlackTree of %a, RedBlackTree of %a) => RedBlackTree of %a;
rbAppend(RBE, x) is x;
rbAppend(x, RBE) is x;
rbAppend(RBT(RBR, a, x, b), RBT(RBR, c, y, d)) is
	let {
		bc is rbAppend(b, c); 
	} in
		(case bc in {
		    RBT(RBR, b1, z, c1) is RBT(RBR, RBT(RBR, a, x, b1), z, RBT(RBR, c1, y, d));
		} default RBT(RBR, a, x, RBT(RBR, bc, y, d)));
rbAppend(RBT(RBB, a, x, b), RBT(RBB, c, y, d)) is
	let {
		bc is rbAppend(b, c);
	} in
		(case bc in {
		    RBT(RBR, b1, z, c1) is RBT(RBR, RBT(RBB, a, x, b1), z, RBT(RBB, c1, y, d));
		} default rbBalLeft(a, x, RBT(RBB, bc, y, d)));
rbAppend(left, right) default is
	case right in {
		RBT(RBR, b, x, c) is RBT(RBR, rbAppend(left, b), x, c);
	} default
		(case left in {
			RBT(RBR, a, x, b) is RBT(RBR, a, x, rbAppend(b, right))
		});
	 
/*
rbAppend(a, RBT(RBR, b, x, c)) is RBT(RBR, rbAppend(a, b), x, c);
rbAppend(RBT(RBR, a, x, b), c) is RBT(RBR, a, x, rbAppend(b, c));
*/

-- FINITE MAPS

rbLookup has type (RedBlackTree of ((%k, %a)), %k ) => option of %a where comparable over %k;
rbLookup(RBE, _) is none;
rbLookup(RBT(_, a, (l, x), b), k) is
	(k <= l)
	? ((l <= k) ? some(x) | rbLookup(a, k))
	| rbLookup(b, k); 

rbUpdate has type
	(RedBlackTree of ((%k, %a)), %k, %a)
			=> RedBlackTree of ((%k, %a)) where comparable over %k;
rbUpdate(rb, k, x) is
	rbFUpdate(rb, k, (function (_) is x));

rbFUpdate has type
	(RedBlackTree of ((%k, %a)),
	 %k,
     (option of %a) => %a) => RedBlackTree of ((%k, %a)) where comparable over %k;
rbFUpdate(s, k, x) is
	let {
		(_, a, y, b) is unpackRedBlackTree(rbDoFUpdate(s, k, x));
	}
	in RBT(RBB, a, y, b);

rbDoFUpdate(RBE, k, f) is RBT(RBR, RBE, (k, f(none)), RBE);
rbDoFUpdate(s matching RBT(RBB, a, (l, y), b), k, f) is
	(k <= l)
	? ((l <= k) ? RBT(RBB, a, (k, f(some(y))), b) | rbBalance(rbDoFUpdate(a, k, f), (l, y), b))
	| rbBalance(a, (l, y), rbDoFUpdate(b, k, f));
rbDoFUpdate(s matching RBT(RBR, a, (l, y), b), k, f) is
	(k <= l)
	? ((l <= k) ? RBT(RBR, a, (k, f(some(y))), b) | RBT(RBR, rbDoFUpdate(a, k, f), (l, y), b))
	| RBT(RBR, a, (l, y), rbDoFUpdate(b, k, f));

rbDeleteAt has type (RedBlackTree of ((%k, %a)), %k ) => RedBlackTree of ((%k, %a)) where comparable over %k;
rbDeleteAt(t, k) is 
	(case del(t) in {
		RBT(_, a, y, b) is RBT(RBB, a, y, b)
	} default RBE)
	using {
		del(RBE) is RBE;
		del(RBT(_, a, y matching (l, _), b)) is
			(k <= l)
			? ((l <= k) ? rbAppend(a, b) | delLeft(a, y, b))
			| delRight(a, y, b);
		delLeft(a matching RBT(RBB, _, _, _), y, b) is rbBalLeft(del(a), y, b);
		delLeft(a, y, b) default is RBT(RBR, del(a), y, b);
		delRight(a, y, b matching RBT(RBB, _, _, _)) is rbBalRight(a, y, del(b));
		delRight(a, y, b) default is RBT(RBR, a, y, del(b));
	};

rbEnumerator has type (RedBlackTree of %val) => CollEnumerator of (%val, %seed);
rbEnumerator(t) is
	(function(iteratee, seed) is
		let {
			traverse(RBE, acc) is Right(acc);
			traverse(RBT(_, a, x, b), acc) is
				case traverse(a, acc) in {
					r matching Left(acc1) is r;
					Right(acc1) is
						case iteratee(acc1, x) in {
							r matching Left(acc2) is r;
							Right(acc2) is traverse(b, acc2)
						}
				}
		} in
			(case traverse(t, seed) in {
				Left(acc) is acc;
				Right(acc) is acc;
			}));
			
rbMapCombine has type ((%a) => %b, RedBlackTree of %a, (%b, %b) => %b, %b) => %b;
rbMapCombine(_, RBE, _, nil) is nil;
rbMapCombine(f, RBT(_, a, x, b), combine, nil) is
	combine(rbMapCombine(f, a, combine, nil),
			combine(f(x),
					rbMapCombine(f, b, combine, nil)));

/* #### the type declaration triggers a bug in Star:
rbMapValuesCombine has type ((%key, %val) => (%b, %val),
							 RedBlackTree of ((%key, %val)),
							 (%b, %b) => %b, %b)
							 	=> (%b, RedBlackTree of ((%key, %val)));
*/
rbMapValuesCombine(_, RBE, combine, nil) is (nil, RBE);
rbMapValuesCombine(f, RBT(c, l, (k, v), r), combine, nil) is
	let {
		(b1, l1) is rbMapValuesCombine(f, l, combine, nil);
		(b0, v0) is f(k, v);
		(b2, r1) is rbMapValuesCombine(f, r, combine, nil);
	} in
		(combine(b1, combine(b0, b2)), RBT(c, l1, (k, v0), r1)); 

-- UTILITIES

rbToList has type (RedBlackTree of %a) => List of %a;
rbToList(RBE) is Null;
rbToList(RBT(_, a, y, b)) is append(rbToList(a), Cons(y, rbToList(b)));

rbToOdd has type (RedBlackTree of %a) => OddStream of %a;
rbToOdd(RBE) is OddNull;
rbToOdd(RBT(_, a, y, b)) is
	oddAppend(rbToOdd(a), oddCons(y, rbToOdd(b)));

-- type declaration triggers compiler bug
-- rbFromList has type (List of %a ) => RedBlackTree of %a where comparable over %a;
rbFromList(xs) is foldLeft(rbInsert, RBE, xs);

rbFromKVList has type (List of ((%k , %v))) => RedBlackTree of ((%k, %v)) where comparable over %k;
rbFromKVList(kvs) is foldLeft((function (rb, (k, v)) is rbUpdate(rb, k, v)), RBE, kvs);

rbCount has type (RedBlackTree of %a) => integer;
rbCount(RBE) is 0;
rbCount(RBT(_, a, x, b)) is 1 + rbCount(a) + rbCount(b);

rbMapValues has type ((%k, %v) => %w, RedBlackTree of ((%k, %v)))
	=> RedBlackTree of ((%k, %w)) where comparable over %k;
rbMapValues(f, RBE) is RBE;
rbMapValues(f, RBT(c, a, (k, v), b)) is 
	RBT(c, rbMapValues(f, a), (k, f(k, v)), rbMapValues(f, b));

rbMin(RBT(_, RBE, x, _)) is x; 
rbMin(RBT(_, t matching RBT(_, _, _, _), _, _)) is rbMin(t);

rbMax(RBT(_, _, x, RBE)) is x; 
rbMax(RBT(_, _, _, t matching RBT(_, _, _, _))) is rbMax(t);

rbKeys has type (RedBlackTree of ((%k, %v))) => List of %k;
rbKeys(RBE) is Null;
rbKeys(RBT(_, a, (k, _), b)) is append(rbKeys(a), Cons(k, rbKeys(b)));

rbValues has type (RedBlackTree of ((%k, %v))) => List of %v;
rbValues(RBE) is Null;
rbValues(RBT(_, a, (_, v), b)) is append(rbValues(a), Cons(v, rbValues(b)));

-- For writing tests

rbRedInvariant(RBE) is true;
rbRedInvariant(RBT(RBR, RBT(RBR, _, _, _), _, _)) is false;
rbRedInvariant(RBT(RBR, _, _, RBT(RBR, _, _, _))) is false;
rbRedInvariant(RBT(_, l, _, r)) default is rbRedInvariant(l) and rbRedInvariant(r);

rbBlackDepth(RBE) is 0;
rbBlackDepth(RBT(RBB, a, _, _)) is 1 + rbBlackDepth(a);
rbBlackDepth(RBT(RBR, a, _, _)) is rbBlackDepth(a);

rbBlackInvariant(RBE) is true;
rbBlackInvariant(RBT(_, a, _, b)) is 
	(rbBlackDepth(a) = rbBlackDepth(b)) 
	and rbBlackInvariant(a) and rbBlackInvariant(b);

rbOrderInvariant(RBE) is true;
rbOrderInvariant(RBT(_, RBE, x, RBE)) is true;
rbOrderInvariant(RBT(_, a matching RBT(_, _, _, _), x, RBE)) is
  (rbMax(a) < x) and rbOrderInvariant(a);
rbOrderInvariant(RBT(_, RBE, x, b matching RBT(_, _, _, _))) is
  (x < rbMin(b)) and rbOrderInvariant(b);
rbOrderInvariant(RBT(_, a, x, b)) default is
  (rbMax(a) < x) and (x < rbMin(b))
  and  rbOrderInvariant(a) and rbOrderInvariant(b);

-- #### using this triggers a bug in Star
rbInvariant(t) is 
 rbRedInvariant(t) and rbBlackInvariant(t) and rbOrderInvariant(t);	

}
