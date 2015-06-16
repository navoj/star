QuickCheck is package {
import Prelude;
import Random;

-- copied straight from QuickCheck 0.2

type Gen of %a is Gen((integer, StdGen) => %a)

-- work around bugs in Star compiler
unpackGen(Gen(f)) is f

sized has type ((integer) => Gen of %a) => Gen of %a
sized(fgen) is Gen((function(n, r) is 
	let {
		m is unpackGen(fgen(n));
	} in
		m(n, r)))
		
resize has type (integer, Gen of %a) => Gen of %a
resize(n, Gen(m)) is Gen((function(_, r) is m(n, r)))

rand has type Gen of StdGen;
rand is Gen((function(n, r) is r));

promote has type ((%a) => Gen of %b) => Gen of ((%a) => %b)
promote(f) is Gen((function (n, r) is
					(function (a) is
						let {
							m is unpackGen(f(a))
						} in
							m(n, r))))

variant has type (integer, Gen of %a) => Gen of %a
variant(val, Gen(m)) is
	Gen((function (n, r) is
		  valof {
		  	var v := val + 1;
		  	var rgen := r;
		  	while (v != 0) do {
		  		(rgen1, rgen2) is split(r);
		  		v := v - 1;
		  		rgen := rgen2;
		  	}
		  	valis m(n, rgen)
		  }))

generate has type (integer, StdGen, Gen of %a) => %a
generate(n, rnd, Gen(m)) is m(size, rnd1)
	using {
		def (size, rnd1) is randomR(0, n, rnd)
	};

#left((genFMap), 1000);
#left((genBind), 40);

-- monad
genReturn has type (%a) => Gen of %a
genReturn(a) is Gen((function (n, r) is a));

(genBind) has type (Gen of %a, (%a) => Gen of %b) => Gen of %b;
Gen(m) genBind k is
	Gen((function (n, r0) is
			let {
				(r1, r2) is split(r0);
				m1 is unpackGen(k(m(n, r1)));
			} in m1(n, r2)));

genLiftM2 has type ((%a, %b) => %c) => ((Gen of %a, Gen of %b) => Gen of %c);
genLiftM2(f) is
	(function(ga, gb) is
		ga genBind
		(function (a) is
			gb genBind
			(function (b) is
				genReturn(f(a, b))))); 
	
genLiftM3 has type ((%a, %b, %c) => %d) => ((Gen of %a, Gen of %b, Gen of %c) => Gen of %d);
genLiftM3(f) is
	(function (ga, gb, gc) is
		ga genBind
		(function (a) is
			gb genBind
			(function (b) is
				gc genBind
				(function (c) is
					genReturn(f(a, b, c)))))); 

-- functor
f genFMap m is m genBind (function (x) is genReturn(f(x)));

contract Arbitrary of %a is {
	arb has type () => Gen of %a; -- Star wants the "() =>" ...
	coarb has type (%a, Gen of %b) => Gen of %b;
}

choose has type (%a, %a) => Gen of %a where Random over %a
choose(l, h) is (function (r) is (fst(randomR(l, h, r)))) genFMap rand

listElements has type (list of %a) => Gen of %a;
listElements(xs) is (function (i) is xs[i]) genFMap choose(0, size(xs) -1);

ListVector has type (integer) => Gen of List of %a where Arbitrary over %a;
ListVector(n) is
	let {
		recur(n) is
			(n = 0) 
			? genReturn(Null)
			| arb() genBind
				(function (val) is
					recur(n-1) genBind
						(function (rest) is
							genReturn(Cons(val, rest))))		  
	} in recur(n);
	

listVector has type (integer) => Gen of list of %a Arbitrary over %a;
listVector(n) is List_to_list genFMap ListVector(n);

implementation Arbitrary over Unit is {
	arb = (function () is genReturn(Unit));
	coarb = (function (a, g) is variant(0, g));
}

implementation Arbitrary over boolean is {
	arb = (function () is listElements(list of [true, false]));
	coarb = (function (b, g) is b ? variant(0, g) | variant(1, g));
}

implementation Arbitrary over integer is {
	arb = (function () is sized((function (n) is choose(- n, n))));
	coarb = (function (n, g) is variant(((n >= 0) ? 2*n | 2*(-n) + 1), g));
}

/*
implementation Arbitrary over float is {
	arb = floatArb;
	coarb = floatCoarb;
} using {
	arb() is liftM3(fraction)@(arb(), arb(), arb());
	fraction has type (integer, integer, integer) => float;
	fraction(a, b, c) is (a cast float) + ((b cast float) / abs(c cast float) + 1);
	-- Star doesn't let me do coarb:
	-- We need either rationalize (via floor) or decodeFloat.
}
*/

/* Star drops the ball:

implementation Arbitrary over ((%a,%b) where Arbitrary over %a and Arbitrary over %b) {
	arb = pairArb;
	coarb = pairCoarb;
} using {
	pairArb is (function () is liftM2((function (a, b) is (a, b)))@(arb(), arb()));
	pairCoarb is (function (p, g) is
				let {
					(a, b) is p
				} in coarb(a, coarb(b, g)));
};

*/


implementation Arbitrary over (List of %a where Arbitrary over %a) is {
	arb = ListArb;
	coarb = ListCoarb;
} using {
	ListArb() is sized((function (n) is choose(0, n) genBind ListVector));
	ListCoarb(Null, g) is variant(0, g);
	ListCoarb(Cons(x, xs), g) is
		coarb(x, variant(1, ListCoarb(xs, g)));
}

implementation Arbitrary over (list of %a where Arbitrary over %a) is {
	arb = listArb;
	coarb = listCoarb;
} using {
	listArb() is sized((function (n) is choose(0, n) genBind listVector));
	listCoarb(l, g) is coarb(list_to_List(l), g);
}

/* Star drops the ball:
implementation Arbitrary over ((%a) => %b where Arbiotrary over %a and Arbitrary over %b) {
	arb = fArb;
	coarb = fCoarb;
} using {
	fArb() is promote((function (a) is coarb(a, arb())));
	fCoarb(f, gen) is
		arb genBind
		(function (val) is
			(function (x) is coarb(f(x), gen)));
}
*/

}