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

/**
* verbatim extract from Prelude.srule of what is used in FingerList,
* but renamed List -> cons */

private def ZERO is 0_;
private def ONE is 1_;
  
array_foldRight has type ((%a, %b) => %b, %b, list of %a) => %b;
fun array_foldRight(f, a, arr) is valof {
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

array_foldLeft has type ((%b, %a) => %b, %b, list of %a) => %b;
fun array_foldLeft(f, a, arr) is valof {
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

array_int_foldLeft has type ((integer_,%a)=>integer_,integer_,list of %a)=>integer_;
fun array_int_foldLeft(f, a, arr) is valof {
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

array_mapcar has type ((%a) => %b, list of %a) => list of %b;
fun array_mapcar(f, arr) is valof {
  var res := __array_nil();
  var i := ZERO;
  var sz := __array_size(arr);
  while not(__integer_ge(i, sz)) do { -- i < sz
    res := __array_append(res, f(__array_el(arr, i)));
    i := __integer_plus(i, ONE);
  }
  valis res;
};

