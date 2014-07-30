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
 * @author andreas
 *
 */
/**
* verbatim extract from Prelude.srule of what is used in FingerList,
* but renamed List -> cons */

private unwrapInt(integer(i)) is i;
private ZERO is unwrapInt(0);
private ONE is unwrapInt(1);
  
array_foldRight has type ((%a, %b) => %b, %b, array of %a) => %b;
array_foldRight(f, a, arr) is valof {
  var res := a;
  var r := arr;
  var sz := __array_size(arr);
  var i := __integer_minus(sz, ONE);
  while __integer_ge(i, ZERO) do {
    res := f(__array_el(arr, i), res);
    i := __integer_minus(i, ONE);
  }
  valis res;
};

array_foldLeft has type ((%b, %a) => %b, %b, array of %a) => %b;
array_foldLeft(f, a, arr) is valof {
  var res := a;
  var r := arr;
  var i := ZERO;
  var sz := __array_size(arr);
  while __integer_ne(i, sz) do {
    res := f(res, __array_el(arr, i));
    i := __integer_plus(i, ONE);
  }
  valis res;
};

array_int_foldLeft has type ((integer_,%a)=>integer_,integer_,array of %a)=>integer_;
array_int_foldLeft(f, a, arr) is valof {
  var res := a;
  var r := arr;
  var i := ZERO;
  var sz := __array_size(arr);
  while __integer_ne(i, sz) do {
    res := f(res, __array_el(arr, i));
    i := __integer_plus(i, ONE);
  }
  valis res;
};

array_mapcar has type ((%a) => %b, array of %a) => array of %b;
array_mapcar(f, arr) is valof {
  var res := __array_nil();
  var i := ZERO;
  var sz := __array_size(arr);
  while not(__integer_ge(i, sz)) do { -- i < sz
    res := __array_append(res, f(__array_el(arr, i)));
    i := __integer_plus(i, ONE);
  }
  valis res;
};

