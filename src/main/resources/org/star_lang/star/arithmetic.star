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

contract arithmetic over t is {
  (+) has type (t,t) => t;
  (-) has type (t,t) => t;
  (*) has type (t,t) => t;
  (/) has type (t,t) => t;
  (%) has type (t,t) => t;
  abs has type (t)=>t;
  __uminus has type (t)=>t;
  zero has type t;
  one has type t;
};

implementation arithmetic over integer is{
  fun integer(X)+integer(Y) is integer(__integer_plus(X,Y));
  fun integer(X)-integer(Y) is integer(__integer_minus(X,Y));
  fun integer(X)*integer(Y) is integer(__integer_times(X,Y));
  fun integer(X)/integer(Y) is integer(__integer_divide(X,Y));
  fun integer(X)%integer(Y) is integer(__integer_rem(X,Y));
  fun abs(integer(X)) is integer(__integer_abs(X));
  fun __uminus(integer(X)) is integer(__integer_uminus(X));
  def zero is 0;
  def one is 1;
};

implementation arithmetic over long is{
  fun long(X)+long(Y) is long(__long_plus(X,Y));
  fun long(X)-long(Y) is long(__long_minus(X,Y));
  fun long(X)*long(Y) is long(__long_times(X,Y));
  fun long(X)/long(Y) is long(__long_divide(X,Y));
  fun long(X)%long(Y) is long(__long_rem(X,Y));
  fun abs(long(X)) is long(__long_abs(X));
  fun __uminus(long(X)) is long(__long_uminus(X));
  def zero is 0l;
  def one is 1l;
};

implementation arithmetic over float is{
  fun float(X)+float(Y) is float(__float_plus(X,Y));
  fun float(X)-float(Y) is float(__float_minus(X,Y));
  fun float(X)*float(Y) is float(__float_times(X,Y));
  fun float(X)/float(Y) is float(__float_divide(X,Y));
  fun float(X)%float(Y) is float(__float_rem(X,Y));
  fun __uminus(float(X)) is float(__float_uminus(X));
  fun abs(float(X)) is float(__float_abs(X));
  def zero is 0.0;
  def one is 1.0;
};

implementation largeSmall over integer is {
  def largest is integer(0x7fffffff_);
  def smallest is integer(0x80000000_);
}

implementation largeSmall over long is {
  def largest is long(0x7fffffffffffffffL_);
  def smallest is long(0x8000000000000000L_);
}

implementation largeSmall over float is {
  def largest is float(__bits_float(0x7fefffffffffffffL_));
  def smallest is float(__bits_float(0x1L_));
}

contract math over t is {
  min has type (t,t)=>t;
  max has type (t,t)=>t;
  random has type (t)=>t;
  sqrt has type (t)=>t;
  cbrt has type (t)=>t;
  ceil has type (t)=>t;
  floor has type (t)=>t;
  round has type (t)=>t;
  log has type (t)=>t;
  log10 has type (t)=>t;
  exp has type (t)=>t;
  (**) has type (t,t) => t;
}

implementation math over integer is {
  fun min(integer(X),integer(Y)) is integer(__integer_min(X,Y));
  fun max(integer(X),integer(Y)) is integer(__integer_max(X,Y));
  fun random(integer(X)) is integer(__integer_random(X));
  fun sqrt(integer(X)) is integer(__integer_sqrt(X));
  fun cbrt(integer(X)) is integer(__integer_cbrt(X));
  fun ceil(integer(X)) is integer(__integer_ceil(X));
  fun floor(integer(X)) is integer(__integer_floor(X));
  fun round(integer(X)) is integer(__integer_round(X));
  fun log(integer(X)) is integer(__integer_log(X));
  fun log10(integer(X)) is integer(__integer_log10(X));
  fun exp(integer(X)) is integer(__integer_exp(X));
  fun integer(X)**integer(Y) is integer(__integer_power(X,Y));
}
  
implementation math over long is {
  fun min(long(X),long(Y)) is long(__long_min(X,Y));
  fun max(long(X),long(Y)) is long(__long_max(X,Y));
  fun random(long(X)) is long(__long_random(X));
  fun sqrt(long(X)) is long(__long_sqrt(X));
  fun cbrt(long(X)) is long(__long_cbrt(X));
  fun ceil(long(X)) is long(__long_ceil(X));
  fun floor(long(X)) is long(__long_floor(X));
  fun round(long(X)) is long(__long_round(X));
  fun log(long(X)) is long(__long_log(X));
  fun log10(long(X)) is long(__long_log10(X));
  fun exp(long(X)) is long(__long_exp(X));
  fun long(X)**long(Y) is long(__long_power(X,Y));
}

implementation math over float is {
  fun min(float(X),float(Y)) is float(__float_min(X,Y));
  fun max(float(X),float(Y)) is float(__float_max(X,Y));
  fun random(float(X)) is float(__float_random(X));
  fun sqrt(float(X)) is float(__float_sqrt(X));
  fun cbrt(float(X)) is float(__float_cbrt(X));
  fun ceil(float(X)) is float(__float_ceil(X));
  fun floor(float(X)) is float(__float_floor(X));
  fun round(float(X)) is float(__float_round(X));
  fun log(float(X)) is float(__float_log(X));
  fun log10(float(X)) is float(__float_log10(X));
  fun exp(float(X)) is float(__float_exp(X));
  fun float(X)**float(Y) is float(__float_power(X,Y));
}

contract trig over t is {
  sin has type (t)=>t;
  asin has type (t)=>t;
  cos has type (t)=>t;
  acos has type (t)=>t;
  tan has type (t)=>t;
  atan has type (t)=>t;
  cosh has type (t)=>t;
  sinh has type (t)=>t;
  tanh has type (t)=>t;
};

implementation trig over float is {
  fun sin(float(X)) is float(__float_sin(X));
  fun asin(float(X)) is float(__float_asin(X));
  fun cos(float(X)) is float(__float_cos(X));
  fun acos(float(X)) is float(__float_acos(X));
  fun tan(float(X)) is float(__float_tan(X));
  fun atan(float(X)) is float(__float_atan(X));
  fun sinh(float(X)) is float(__float_sinh(X));
  fun cosh(float(X)) is float(__float_cosh(X));
  fun tanh (float(X)) is float(__float_tanh(X));
}

-- Unary minus
# - #(?X)# ==> __uminus(X);