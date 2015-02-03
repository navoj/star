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
 * @author msperber
 *
 */
/* see http://chimera.labs.oreilly.com/books/1230000000929/ch04.html */
shortestPath is package {

  type vertex is alias of integer;
  type weight is alias of integer;
  type intmap of t is alias of dictionary of (integer, t);
  type graph is alias of intmap of intmap of weight;

  implementation (computation) over option is {
	_encapsulate(x) is some(x);
	_abort(e) is none;
	_handle(o, h) is o;
	_combine(m, f) is
	  case m in {
		none is none;
		some(x) is f(x);
	  };
  };

  implementation execution over option is {
	_perform(some(x), _) is x;
  };

  weight has type (graph, vertex, vertex) => option of weight;
  weight(g, i, j) is
    option computation {
	  jmap is valof mapLookup(g, i);
	  valis valof mapLookup(jmap, j);
	};


  makeGraph has type (cons of cons of integer) => graph;
  makeGraph(xss) is
	alistToMap(zipWith(row, iota(0, size(xss)-1, 1), xss))
	  using {
		row(i, xs) is 
		  (i, alistToMap(cons of { (j, w) where (j, w) in zip(iota(0, size(xs)-1, 1), xs) and w != 100 }));
	  };

  shortestPaths has type (cons of vertex, graph) => graph;
  shortestPaths(vs, g0) is valof leftFold(upd, task { valis g0; }, vs)
	using {
	  upd(gt, k) is 
		task {
		  g is valof gt;
		  valis
			let {
			  shortmap has type (vertex, intmap of weight) => intmap of weight;
			  shortmap(i, jmap) is rightFold(shortest, dictionary of {}, vs)
				using {
				  shortest(j, m) is 
					(case (old, new) in {
					  (none, none) is m;
					  (none, some(w)) is m[j->w];
					  (some(w), none) is m[j->w];
					  (some(w1), some(w2)) is m[j->min(w1, w2)];
					}) using {
						old is mapLookup(jmap, j);
						new is
						  option computation {
							w1 is valof weight(g, i, k);
							w2 is valof weight(g, k, j);
							valis w1+w2;
						  };
					  };
				};
			} in
			  alistToMap(valof parallel(fmap((function ((i, v)) is task { valis (i, shortmap(i, v)); }), g as (cons of ((integer, intmap of weight))))));
		};
	};


  mapLookup(m, k) is present m[k] ? some(m[k]) | none;

  itMap(f, lis) is
	let {
	  step(el, ContinueWith(l)) is ContinueWith(_cons(f(el), l));
	} in
	  (case _iterate(lis, step, ContinueWith(_nil())) in {
		ContinueWith(l) is reverse(l);
	  });

  ixitMap(f, lis) is
	let {
	  step(k, v, ContinueWith(l)) is ContinueWith(cons(f(k, v), l));
	} in
	  (case _ixiterate(lis, step, ContinueWith(nil)) in {
		ContinueWith(l) is reverse(l);
	  });

  implementation coercion over (dictionary of (%k, %v), cons of ((%k, %v))) is {
	coerce(mp) is
	  let {
		step(k, v, ContinueWith(l)) is ContinueWith(_cons((k, v), l));
	  } in
		(case _ixiterate(mp, step, ContinueWith(nil)) in {
		  ContinueWith(l) is reverse(l);
		});
  };

  alistToMap(l) is leftFold((function (m, (k, v)) is m[k->v]),dictionary of {}, l);


  mapUnion has type for all a such that (intmap of a, intmap of a) => intmap of a;
  mapUnion(mp1, mp2) is
	let {
	  step(k, v, st matching ContinueWith(mp)) is
		present mp1[k] ? st | ContinueWith(mp[k->v]);
	} in
	  (case _ixiterate(mp2, step, ContinueWith(mp1)) in {
		ContinueWith(mp) is mp;
	  });

  fmap(f, l) is reverse(leftFold((function (r, x) is cons(f(x), r)), nil(), l));

  randoms(n, hi) is nThings(n, (function (_) is random(hi)));

  zip3(l1, l2, l3) is
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
	reverse(s) is s;
  };

  setFromIterable(it) is itMap((function (x) is x), it);

  consFromIterable has type
	for all coll, el such that
	  (coll) => cons of el
		where iterable over coll determines el 
           and reversible over coll ; -- '
  consFromIterable(it) is itMap((function (x) is x), it);

  nThings(n, make) is
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
  zip(lis1, lis2) is valof {
	var r1 := lis1;
	var r2 := lis2;
	var res := nil;
	while r1 matches cons(x, xs) do {
	  cons(y, ys) is r2;
	  res := cons((x, y), res);
	  r1 := xs;
	  r2 := ys;
	};
	valis reverse(res);
  };

  zipWith has type ((%a, %b) => %c, cons of %a, cons of %b) => cons of %c;
  zipWith(f, lis1, lis2) is valof {
	var r1 := lis1;
	var r2 := lis2;
	var res := nil;
	while r1 matches cons(x, xs) do {
	  cons(y, ys) is r2;
	  res := cons(f(x, y), res);
	  r1 := xs;
	  r2 := ys;
	};
	valis reverse(res);
  };

  parallel has type for all t such that (cons of task of t) => task of cons of t;
  parallel(ts) is
	task {
	  pts is fmap((background), ts);
	  rev is leftFold((function (res, t) is task { valis cons(valof t, valof res); }), task { valis nil; }, ts);
	  valis reverse(valof rev);
 	};

  test is cons of {
         cons of {  0; 999; 999;  13; 999; 999};
         cons of {999;   0; 999; 999;   4;   9};
         cons of { 11; 999;   0; 999; 999; 999};
         cons of {999;   3; 999;   0; 999;   7};
         cons of { 15;   5; 999;   1;   0; 999};
         cons of { 11; 999; 999;  14; 999;   0}};

  main() do {
	res is shortestPaths(iota(0, 5, 1), makeGraph(test));
	logMsg(info, "res is: $(res)");
	vertices is 800;
	edges is 160;
  };

}
