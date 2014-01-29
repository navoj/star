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
arrayslice is package {

  # #(dbg(?msg, ?A))# ==> logMsg(info, msg ++ " : " ++ __display(?A));

  type Arr of %a is
       ArrEmpty
    or ArrSingle(%a)
    or ArrDeep(array of %a, Arr of (array of %a), array of %a);

  var debug := false;

  foldLeft has type ((%b, %a) => %b, %b, cons of %a) => %b;
  foldLeft(f, a, lis) is valof {
	var r := lis;
	var res := a;
	while r matches cons(x, xs) do {
	  res := f(res, x);
	  r := xs;
	};
	valis res;
  };
  foldRight has type ((%a, %b) => %b, %b, cons of %a) => %b;
  foldRight(f, a, nil) is a;
  foldRight(f, a, cons(x, xs)) is f(x, foldRight(f, a, xs));

  arrCons has type (%a, Arr of (%a)) => Arr of (%a) where pPrint over %a;
  arrCons(a, ArrEmpty) is ArrSingle(a);
  arrCons(a, ArrSingle(b)) is ArrDeep(__array_cons(a, __array_nil()), ArrEmpty, __array_cons(b, __array_nil()));
  arrCons(a, ArrDeep(l, m, r)) is valof {
    if debug then {
      logMsg(info, "called: arrCons($((a)), ArrDeep($(l), $(m), $(r)))");
    };
    sz is size(l);
    if sz = 8 then {
      if debug then {
        logMsg(info, "arrCons $(a): l = $(l)");
        logMsg(info, "arrCons $(a): sz = $((sz))");
        logMsg(info, "arrCons $(a): l[0:1] = $(l[0:1])");
        logMsg(info, "arrCons $(a): l[1:sz] = $(l[1:sz])");
        assert size(l[0:1]++l[1:sz])=size(l);
        case l in {_pair(head, tail) do {
          logMsg(info, "arrCons $(a): head = $(head)");
          logMsg(info, "arrCons $(a): tail = $(tail)");
        }};
      }
      valis ArrDeep(_cons(a,l[0:1]), arrCons((l[1:sz]), m), r);  /* l[1:sz] fails with 52nd element */
    } else {
      valis ArrDeep(_cons(a, l), m, r);
    }
  };

  main() do {
    t52a is foldRight(arrCons, ArrEmpty, iota(2,52-1,1));
    t52b is arrCons(1, t52a);
    dbg("t52b", t52b);
    debug := true;
    t52 is arrCons(0, t52b);
    dbg("t52", t52);
  }
}
