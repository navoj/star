/**
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
 *
 */
/* see http://chimera.labs.oreilly.com/books/1230000000929/ch04.html */
shortestPath is package {

  type vertex is alias of integer;
  type weight is alias of integer;
  type intmap of t is alias of dictionary of (integer, t);
  type graph is alias of intmap of intmap of weight;

  implementation (computation) over option is {
	fun _encapsulate(x) is some(x);
	fun _abort(e) is none;
	fun _handle(o, h) is o;
	fun _combine(m, f) is
	  switch m in {
		case none is none;
		case some(x) is f(x);
	  };
  };

  implementation execution over option is {
	fun _perform(some(x), _) is x;
  };

  weight has type (graph, vertex, vertex) => option of weight;
  fun weight(g, i, j) is
    option computation {
	  def jmap is valof mapLookup(g, i);
	  valis valof mapLookup(jmap, j);
	};


  makeGraph has type (cons of cons of integer) => graph;
  fun makeGraph(xss) is
	alistToMap(zipWith(row, iota(0, size(xss)-1, 1), xss))
	  using {
		fun row(i, xs) is 
		  (i, alistToMap(cons of { all (j, w) where (j, w) in zip(iota(0, size(xs)-1, 1), xs) and w != 100 }));
	  };

  shortestPaths has type (cons of vertex, graph) => graph;
  fun shortestPaths(vs, g0) is valof leftFold(upd, task { valis g0; }, vs)
	using {
	  fun upd(gt, k) is 
		task {
		  def g is valof gt;
		  valis
			let {
			  shortmap has type (vertex, intmap of weight) => intmap of weight;
			  fun shortmap(i, jmap) is rightFold(shortest, dictionary of [], vs)
				using {
				  fun shortest(j, m) is 
					(switch (old, new) in {
					  case (none, none) is m;
					  case (none, some(w)) is m[j->w];
					  case (some(w), none) is m[j->w];
					  case (some(w1), some(w2)) is m[j->min(w1, w2)];
					}) using {
						def old is mapLookup(jmap, j);
						def new is
						  option computation {
							def w1 is valof weight(g, i, k);
							def w2 is valof weight(g, k, j);
							valis w1+w2;
						  };
					  };
				};
			} in
			  alistToMap(valof parallel(fmap(( ((i, v)) => task { valis (i, shortmap(i, v)); }), g as (cons of ((integer, intmap of weight))))));
		};
	};


  fun mapLookup(m, k) is m[k];

  fun itMap(f, lis) is
	let {
	  fun step(el, ContinueWith(l)) is ContinueWith(_cons(f(el), l));
	} in
	  (switch _iterate(lis, step, ContinueWith(_nil())) in {
		case ContinueWith(l) is reverse(l);
	  });

  fun ixitMap(f, lis) is
	let {
	  fun step(k, v, ContinueWith(l)) is ContinueWith(cons(f(k, v), l));
	} in
	  (switch _ixiterate(lis, step, ContinueWith(nil)) in {
		case ContinueWith(l) is reverse(l);
	  });

  implementation coercion over (dictionary of (%k, %v), cons of ((%k, %v))) is {
	fun coerce(mp) is
	  let {
		fun step(k, v, ContinueWith(l)) is ContinueWith(_cons((k, v), l));
	  } in
		(switch _ixiterate(mp, step, ContinueWith(nil)) in {
		  case ContinueWith(l) is reverse(l);
		});
  };

  fun alistToMap(l) is leftFold(( (m, (k, v)) => m[k->v]),dictionary of [], l);


  mapUnion has type for all a such that (intmap of a, intmap of a) => intmap of a;
  fun mapUnion(mp1, mp2) is
	let {
	  fun step(k, v, st matching ContinueWith(mp)) is
		present mp1[k] ? st : ContinueWith(mp[k->v]);
	} in
	  (switch _ixiterate(mp2, step, ContinueWith(mp1)) in {
		case ContinueWith(mp) is mp;
	  });

  fun fmap(f, l) is reverse(leftFold(((r, x) => cons(f(x), r)), nil(), l));

  fun randoms(n, hi) is nThings(n,  (_) => random(hi));

  fun zip3(l1, l2, l3) is
	valof {
	  /* need general iteration construct */
	  var r1 := l1;
	  var r2 := l2;
	  var r3 := l3;
	  var res := nil;
	  while ((r1 matches cons(x1, rs1))
			 and (r2 matches cons(x2, rs2))
			 and (r3 matches cons(x3, rs3))) do {
		res := cons((x1, x2, x3), res);
		r1 := rs1;
		r2 := rs2;
		r3 := rs3;
	  };
	  valis reverse(res);
	};

  setFromIterable has type
	for all coll, el such that
	  (coll) => list of el
		where iterable over coll determines el 
           and reversible over coll ; -- '

  implementation reversible over list of %t is {
	fun reverse(s) is s;
  };

  fun setFromIterable(it) is itMap(((x) => x), it);

  consFromIterable has type
	for all coll, el such that
	  (coll) => cons of el
		where iterable over coll determines el 
           and reversible over coll ; -- '
  fun consFromIterable(it) is itMap(id, it);

  fun nThings(n, make) is
	valof {
	  var i := n;
	  var r := nil;
	  while i > 0 do {
		r := cons(make(i), r);
		i := i - 1;
	  }
	  valis r;
	};

  zip has type (cons of %a, cons of %b) => cons of ((%a, %b));
  fun zip(lis1, lis2) is valof {
	var r1 := lis1;
	var r2 := lis2;
	var res := nil;
	while r1 matches cons(x, xs) do {
	  def cons(y, ys) is r2;
	  res := cons((x, y), res);
	  r1 := xs;
	  r2 := ys;
	};
	valis reverse(res);
  };

  zipWith has type ((%a, %b) => %c, cons of %a, cons of %b) => cons of %c;
  fun zipWith(f, lis1, lis2) is valof {
	var r1 := lis1;
	var r2 := lis2;
	var res := nil;
	while r1 matches cons(x, xs) do {
	  def cons(y, ys) is r2;
	  res := cons(f(x, y), res);
	  r1 := xs;
	  r2 := ys;
	};
	valis reverse(res);
  };

  parallel has type for all t such that (cons of task of t) => task of cons of t;
  fun parallel(ts) is
	task {
	  def pts is fmap((background), ts);
	  def rev is leftFold(((res, t) => task { valis cons(valof t, valof res); }), task { valis nil; }, ts);
	  valis reverse(valof rev);
 	};

  def test is cons of [
         cons of [  0, 999, 999,  13, 999, 999],
         cons of [999,   0, 999, 999,   4,   9],
         cons of [ 11, 999,   0, 999, 999, 999],
         cons of [999,   3, 999,   0, 999,   7],
         cons of [ 15,   5, 999,   1,   0, 999],
         cons of [ 11, 999, 999,  14, 999,   0]]

  prc main() do {
	def res is shortestPaths(iota(0, 5, 1), makeGraph(test));
	logMsg(info, "res is: $(res)");
	def vertices is 800;
	def edges is 160;
  };

}
