Prelude is package {

type Unit is Unit;

type Maybe of %a is
  	Nothing 
 or Just(%a);

isJust has type (Maybe of %a) => boolean;
isJust(Nothing) is false;
isJust(Just(_)) is true;

expectJust has type (Maybe of %a) => %a
expectJust(Just(x)) is x

expectJust1 has type (Maybe of %a) => %a
expectJust1(Just(x)) is x

type Either of (%a, %b) is Left(%a) or Right(%b)

fst has type ((%a, %b)) => %a
fst((a, _)) is a;

snd has type ((%a, %b)) => %b;
snd((_, b)) is b;

type Ordering is LT or GT or EQ;

type List of %a is 
    Null
 or Cons(%a, List of %a);

isNull has type (List of %a) => boolean;
isNull(Null) is true;
isNull(_) default is false;

head has type (List of %a) => %a;
head(Cons(x, _)) is x;

tail has type (List of %a) => List of %a;
tail(Cons(_, xs)) is xs;

List1 has type (%a) => List of %a;
List1(x) is Cons(x, Null);

List2 has type (%a, %a) => List of %a;
List2(x1, x2) is Cons(x1, Cons(x2, Null));

List3 has type (%a, %a, %a) => List of %a;
List3(x1, x2, x3) is Cons(x1, Cons(x2, Cons(x3, Null)));

List4 has type (%a, %a, %a, %a) => List of %a;
List4(x1, x2, x3, x4) is Cons(x1, Cons(x2, Cons(x3, Cons(x4, Null))));

List5 has type (%a, %a, %a, %a, %a) => List of %a;
List5(x1, x2, x3, x4, x5) is Cons(x1, Cons(x2, Cons(x3, Cons(x4, Cons(x5, Null)))));

length has type (List of %a) => integer where equality over %a;
length(lis) is valof {
	var r := lis;
	var l := 0;
	while r != Null do {
		Cons(_, xs) is r;
		l := l + 1;
		r := xs;
	};
	valis l;
};

implementation equality over (List of %a where equality over %a) is {
	(=) = listEq;
} using {
	listEq(Null,Null) is true;
	listEq(Cons(H1,L1),Cons(H2,L2)) where H1=H2 is listEq(L1,L2);
	listEq(_,_) default is false;
}

implementation sizeable over (List of %a where equality over %a) is {
	size = length;
	isEmpty = isNull;
};

forEach has type action(action(%a), List of %a) where equality over %a;
forEach(p, l) do {
	var r := l;
	while r != Null do {
		Cons(x, xs) is r
		p(x);
		r := xs;
	}
}

reverse has type (List of %a) => List of %a where equality over %a;
reverse(l) is 
	valof {
		var r := Null;
		forEach((procedure(x) do { r := Cons(x, r) }), l)
		valis r
	};
	
append has type (List of %a, List of %a) => List of %a  where equality over %a;
append(Null, lis2) is lis2;
append(lis1, Null) is lis1;
append(lis1, lis2) default is valof {
  var r:= reverse(lis1);
  var res := lis2;
  while r != Null do {
	Cons(x, xs) is r;
	res := Cons(x, res);
	r := xs;
  };
  valis res;
};

conc has type (List of List of %a) => List of %a  where equality over %a;
conc(lis) is valof {
  var r := reverse(lis);
  var res := Null;
  while r != Null do {
	Cons(x, xs) is r;
	res := append(x, res);
	r := xs;
  }
  valis res;
};

hasAny has type ((%a) => boolean, List of %a) => boolean where equality over %a;
hasAny(p, lis) is valof {
	var r := lis;
	while r != Null do {
		Cons(x, xs) is r;
		if p(x) then valis true; 
		r := xs;
	}
	valis false;
};

hasEvery has type ((%a) => boolean, List of %a) => boolean where equality over %a;
hasEvery(p, lis) is valof {
	var r := lis;
	while r != Null do {
		Cons(x, xs) is r;
		if not p(x) then valis false; 
		r := xs;
	}
	valis true;
};

find has type ((%a) => boolean, List of %a) => Maybe of %a where equality over %a;
find(p, lis) is valof {
	var r:= lis;
	while r != Null do {
		Cons(x, xs) is r;
		if p(x) then valis Just(x);
		r := xs;
	}
	valis Nothing;
};

/* find an element matching a given Just predicate, and return what that returns */

findAndApply has type ((%a) => Maybe of %b, List of %a) => Maybe of %b where equality over %a;
findAndApply(f, lis) is valof {
  var r:= lis;
  while r != Null do {
	Cons(x, xs) is r;
	case f(x) in {
	  Nothing do nothing;
	  a matching Just(y) do valis a;
	};
	r := xs;
  };
  valis Nothing;
};

member has type ((%a, %a) => boolean, %a, List of %a) => boolean where equality over %a;
member(eq, y, lis) is valof {
	var r:= lis;
	while r != Null do {
		Cons(x, xs) is r;
		if eq(x, y) then valis true;
		r := xs;
	}
	valis false;
};

assoc has type ((%a, %a) => boolean, %a, List of ((%a, %b))) => Maybe of ((%a, %b)) where equality over %a and equality over %b;
assoc(eq, y, lis) is valof {
  var r := lis;
  while r != Null do {
	Cons(p, xs) is r;
	(x, _) is p;
	if eq(x, y) then valis Just(p);
	r := xs;
  };
  valis Nothing
};

filter has type ((%a) => boolean, List of %a) => List of %a where equality over %a;
filter(p, lis) is
	foldLeft((function (r, x) is p(x) ? Cons(x, r) | r), Null,
			 reverse(lis));

filterMap has type ((%a) => Maybe of %b, List of %a) => List of %b where equality over %a and equality over %b;
filterMap(p, lis) is
	foldLeft((function (r, x) is 
				case p(x) in {
					Nothing is r
					Just(y) is Cons(y, r)
				}),
			 Null,
			 reverse(lis));

-- returns list of yeses, then list of nos
-- #### type declaration triggers Star compiler bug
-- partition has type ((%a) => boolean, List of %a) => (List of %a, List of %a);
partition(p, lis) is valof {
	var r := lis;
	var yeses := Null;
	var nos := Null;
	while r != Null do {
		Cons(x, xs) is r;
		if p(x)
		then yeses := Cons(x, yeses)
		else nos := Cons(x, nos);
		r := xs;
	};
	valis (reverse(yeses), reverse(nos));
};

removeAll has type ((%a ) => boolean, List of %a) => List of %a where equality over %a;
removeAll(p, lis) is
	foldLeft((function (r, x) is p(x) ? r | Cons(x, r)), Null,
			 reverse(lis));

-- #### type declaration triggers Star compiler bug
zip has type (List of %a, List of %b) => List of ((%a, %b)) where equality over %a and equality over %b;
zip(lis1, lis2) is valof {
  var r1 := lis1;
  var r2 := lis2;
  var res := Null;
  while r1 != Null do {
	Cons(x, xs) is r1;
	Cons(y, ys) is r2;
	res := Cons((x, y), res);
	r1 := xs;
	r2 := ys;
  };
  valis reverse(res);
};

zip3 has type (List of %a, List of %b, List of %c) => List of ((%a, %b, %c)) where equality over %a and equality over %b and equality over %c;
zip3(lis1, lis2, lis3) is valof {
  var r1 := lis1;
  var r2 := lis2;
  var r3 := lis3;
  var res := Null;
  while r1 != Null do {
	Cons(x, xs) is r1;
	Cons(y, ys) is r2;
	Cons(z, zs) is r3;
	res := Cons((x, y, z), res);
	r1 := xs;
	r2 := ys;
	r3 := zs;
  };
  valis reverse(res);
};

foldRight has type ((%a, %b) => %b, %b, List of %a) => %b;
foldRight(f, a, Null) is a;
foldRight(f, a, Cons(x, xs)) is f(x, foldRight(f, a, xs));

foldLeft has type ((%b, %a) => %b, %b, List of %a) => %b where equality over %a and equality over %b;
foldLeft(f, a, l) is valof {
	var res := a;
	forEach((procedure(x) do res := f(res, x)), l);
	valis res;
}

-- map is a keyword
mapcar has type ((%a) => (%b), List of %a) => List of %b where equality over %a and equality over %b;
mapcar(f, l) is reverse(
	foldLeft((function (r, x) is Cons(f(x), r)), Null, l)); 
 
-- combine is supposed to be associative
-- the idea is that all of this could be done in parallel
mapCombine has type ((%a) => %b, List of %a, (%b, %b) => %b, %b) => %b where equality over %a and equality over %b;
mapCombine(f, lis, combine, nill) is
	foldLeft((function (b, x) is
				combine(b, f(x))),
			 nill, lis);

nth has type (List of %a, integer) => %a where equality over %a;
nth(lis, n) is valof {
	var r := lis;
	var i := 0;
	while (i < n) do {
		assert r != Null;
		Cons(_, xs) is r
		r := xs
		i := i + 1;
	}
	Cons(x, _) is r;
	valis x
}

findIndexAndApply has type ((%a) => Maybe of %b, List of %a) => Maybe of ((integer, %b)) where equality over %a;
findIndexAndApply(f, lis) is valof {
  var r := lis;
  var i := 0;
  while r != Null do {
	Cons(x, xs) is r;
	case f(x) in {
	  Nothing do nothing;
	  Just(y) do
		valis Just((i, y));
	};
	i := i + 1;
	r := xs;
  };
  valis Nothing;
};

-- replaceAtIndex has type (List of %a, integer, %a) => List of %a;
replaceAtIndex(lis, i, new) is valof {
  var r := lis;
  var j := 0;
  var rev := Null;
  while r != Null do {
	Cons(x, xs) is r;
	if j = i
	then valis append(reverse(rev), Cons(new, xs));
	rev := Cons(x, rev);
	j := j + 1;
	r := xs;
  };
  valis reverse(rev);
};


-- type declaration triggers bug in Star
-- takeNDropWhile has type ((%a) => boolean, List of %a) => (List of %a, List of %a);
takeNDropWhile(p, lis) is valof {
	var r := lis;
	var b := Null;
	while (r != Null) do {
		Cons(x, xs) is r;
		if not p(x)
		then valis (reverse(b), r);
		r := xs;
		b := Cons(x, b);
	};
	valis (reverse(b), r);
};
	
-- "unique" is a keyword 
uniqueElements has type ((%a, %a) => boolean, List of %a) => List of %a where equality over %a;
uniqueElements(eq, lis) is valof {
	var r := lis;
	var res := Null;
	while r != Null do {
		Cons(x, xs) is r;
		res := Cons(x, res);
		r := filter((function (y) is not eq(x, y)), xs);
	};
	valis reverse(res);
};

List_to_list has type ((List of %a) => list of %a) where equality over %a;
List_to_list(l) is valof { 
    var res := list{};
	forEach( (procedure(x) do { res := res<>list{x} }) , l);
	valis res;
}

/* Oleg has this:
> type CFoldLeft coll val m seed = coll -> CollEnumerator val m seed
> type CollEnumerator val m seed =
>        Iteratee val seed
>        -> seed      -- the initial seed
>        -> m seed
> type Iteratee val seed = seed -> val -> Either seed seed
*/

type CollEnumerator of (%val, %seed) is alias of
	((Iteratee of (%val, %seed), %seed) => %seed);

/*
 * Left means quit with provided seed value, Right means go on.
 */
type Iteratee of (%val, %seed) is alias of
	((%seed, %val) => Either of (%seed, %seed));

emptyEnumerator has type CollEnumerator of (%val, %seed);
emptyEnumerator(it, seed) is seed;

enumeratorFilter has type
	((%val) => boolean, CollEnumerator of (%val, %seed)) => CollEnumerator of (%val, %seed);
enumeratorFilter(f, enum) is
	(function (it, seed) is
		enum((function (seed1, val) is
				f(val) ? it(seed1, val) | Right(seed1)), 
			 seed));
				
enumeratorFilterMap has type
	((%val1) => Maybe of %val2, CollEnumerator of (%val1, %seed))
		=> CollEnumerator of (%val2, %seed);
enumeratorFilterMap(f, enum) is	
	(function (it, seed) is
		enum((function (seed1, val1) is
				case f(val1) in {
					Nothing is Right(seed1);
					Just(val2) is it(seed1, val2);
				}),
			 seed));

enumeratorCount has type (CollEnumerator of (%val, integer)) => integer;
enumeratorCount(en) is
  en((function (c, _) is Right(c + 1)), 0);

enumeratorConcatenate has type
  (CollEnumerator of (CollEnumerator of (%val, Either of (%seed, %seed)), %seed))
	=> CollEnumerator of (%val, %seed);
enumeratorConcatenate(outerEnum) is
  (function (it, seed1) is
	outerEnum((function (seed2, innerEnum) is
				innerEnum((function (seed3, val) is
							case seed3 in {
							  Right(seed4) is
								(case it(seed4, val) in {
								  v matching Left(_) is Left(v);
								  v matching Right(_) is Right(v);
								})
							}),
						  Right(seed2))),
			  seed1));

ListEnumerator has type (List of %val) => CollEnumerator of (%val, %seed) where equality over %val
ListEnumerator(l) is
	(function(iteratee, seed) is
		valof {
			var r := l;
			var acc := seed;

			while r != Null do {
				Cons(x, xs) is r
				case iteratee(acc, x) in {
					Left(new) do valis new
					Right(new) do {
						acc := new;
						r := xs;
					}
				}
			}
			valis acc
		});
		
displayList has type ((%a) => string, List of %a) => string;
displayList(dis, lis) is "[" ++ d(lis) ++ "]" using {
	d(Null) is "";
	d(Cons(x, Null)) is dis(x);
	d(Cons(x, xs)) default is dis(x) ++ ", " ++ d(xs);
}

/* Oleg has this:
> type CFoldLeft' val m seed = 
>        Self (Iteratee val seed) m seed
>        -> CollEnumerator val m seed
> type Self iter m seed = iter -> seed -> m seed
> type CFoldLeft1Maker coll val m seed = coll -> m (CFoldLeft' val m seed)
*/

/* We'll do this later:
type CFoldLeft1 of (%val, %seed) is alias of
	((Self of (Iteratee of (%val, %seed), %seed)) => CollEnumerator of (%val, %seed))
	
type Self of (%iter, %seed) is alias of
	((%iter, %seed) => %seed)

type CFoldLeft1Maker of (%coll, %val, %seed) is alias of
	((%coll) => CFoldLeft1 of (%val, %seed)) 
*/	

LSetSubset has type ((%a, %a) => boolean, List of %a, List of %a) => boolean where equality over %a;
LSetSubset(eq, lis1, lis2) is valof {
	var r:= lis1;
	while r != Null do {
		Cons(x, xs) is r;
		if not member(eq, x, lis2) then valis false;
		r := xs;
	}
	valis true;
};

LSetEqual has type ((%a, %a) => boolean, List of %a, List of %a) => boolean where equality over %a;
LSetEqual(eq, lis1, lis2) is
	LSetSubset(eq, lis1, lis2) and LSetSubset(eq, lis2, lis1);

/* Left means stop, Right means go on */
iterAte has type (%a, (%a) => Either of (%b, %a)) => %b;
iterAte(theST, step) is valof {
	var st := theST;
	while true do {
	  case step(st) in {
		Left(final) do valis final;
		Right(next) do st := next;
	  };
	};
};

-- from Haskell

contract Bounded over %a is {
	minBound has type () => %a;
	maxBound has type () => %a;
};

implementation Bounded over boolean is {
	minBound = (function () is false);
	maxBound = (function () is true);
};

contract Enum over %a is {
	succ has type (%a) => %a;
	pred has type (%a) => %a;
	toEnum has type (integer) => %a;
	fromEnum has type (%a) => integer;
};

implementation Enum over boolean is {
	succ = boolSucc;
	pred = boolPred;
	toEnum = boolToEnum;
	fromEnum = boolFromEnum;
} using {
	boolSucc(false) is true;
	boolPred(true) is false;
	boolToEnum(0) is false;
	boolToEnum(1) is true;
	boolFromEnum(false) is 0;
	boolFromEnum(true) is 1;
};

implementation Enum over integer is {
	succ = intSucc;
	pred = intPred;
	toEnum = intId;
	fromEnum = intId;
} using {
	intSucc(n) is n + 1;
	intPred(n) is n - 1;
	intId(n) is n;
};



}
