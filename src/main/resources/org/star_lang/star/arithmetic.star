/**
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
  integer(X)+integer(Y) is integer(__integer_plus(X,Y));
  integer(X)-integer(Y) is integer(__integer_minus(X,Y));
  integer(X)*integer(Y) is integer(__integer_times(X,Y));
  integer(X)/integer(Y) is integer(__integer_divide(X,Y));
  integer(X)%integer(Y) is integer(__integer_rem(X,Y));
  abs(integer(X)) is integer(__integer_abs(X));
  __uminus(integer(X)) is integer(__integer_uminus(X));
  zero is 0;
  one is 1;
};

implementation arithmetic over long is{
  long(X)+long(Y) is long(__long_plus(X,Y));
  long(X)-long(Y) is long(__long_minus(X,Y));
  long(X)*long(Y) is long(__long_times(X,Y));
  long(X)/long(Y) is long(__long_divide(X,Y));
  long(X)%long(Y) is long(__long_rem(X,Y));
  abs(long(X)) is long(__long_abs(X));
  __uminus(long(X)) is long(__long_uminus(X));
  zero is 0l;
  one is 1l;
};

implementation arithmetic over float is{
  float(X)+float(Y) is float(__float_plus(X,Y));
  float(X)-float(Y) is float(__float_minus(X,Y));
  float(X)*float(Y) is float(__float_times(X,Y));
  float(X)/float(Y) is float(__float_divide(X,Y));
  float(X)%float(Y) is float(__float_rem(X,Y));
  __uminus(float(X)) is float(__float_uminus(X));
  abs(float(X)) is float(__float_abs(X));
  zero is 0.0;
  one is 1.0;
};

implementation arithmetic over decimal is{
  decimal(X)+decimal(Y) is decimal(__decimal_plus(X,Y));
  decimal(X)-decimal(Y) is decimal(__decimal_minus(X,Y));
  decimal(X)*decimal(Y) is decimal(__decimal_times(X,Y));
  decimal(X)/decimal(Y) is decimal(__decimal_divide(X,Y));
  decimal(X)%decimal(Y) is decimal(__decimal_rem(X,Y));
  __uminus(decimal(X)) is decimal(__decimal_uminus(X));
  abs(decimal(X)) is decimal(__decimal_abs(X));
  zero is 0a;
  one is 1a;
};

implementation largeSmall over char is {
  largest is char(__integer_char(0xffffff_));
  smallest is char(__integer_char(0_));
}

implementation largeSmall over integer is {
  largest is integer(0x7fffffff_);
  smallest is integer(0x80000000_);
}

implementation largeSmall over long is {
  largest is long(0x7fffffffffffffffL_);
  smallest is long(0x8000000000000000L_);
}

implementation largeSmall over float is {
  largest is float(__bits_float(0x7fefffffffffffffL_));
  smallest is float(__bits_float(0x1L_));
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
  min(integer(X),integer(Y)) is integer(__integer_min(X,Y));
  max(integer(X),integer(Y)) is integer(__integer_max(X,Y));
  random(integer(X)) is integer(__integer_random(X));
  sqrt(integer(X)) is integer(__integer_sqrt(X));
  cbrt(integer(X)) is integer(__integer_cbrt(X));
  ceil(integer(X)) is integer(__integer_ceil(X));
  floor(integer(X)) is integer(__integer_floor(X));
  round(integer(X)) is integer(__integer_round(X));
  log(integer(X)) is integer(__integer_log(X));
  log10(integer(X)) is integer(__integer_log10(X));
  exp(integer(X)) is integer(__integer_exp(X));
  integer(X)**integer(Y) is integer(__integer_power(X,Y));
}
  
implementation math over long is {
  min(long(X),long(Y)) is long(__long_min(X,Y));
  max(long(X),long(Y)) is long(__long_max(X,Y));
  random(long(X)) is long(__long_random(X));
  sqrt(long(X)) is long(__long_sqrt(X));
  cbrt(long(X)) is long(__long_cbrt(X));
  ceil(long(X)) is long(__long_ceil(X));
  floor(long(X)) is long(__long_floor(X));
  round(long(X)) is long(__long_round(X));
  log(long(X)) is long(__long_log(X));
  log10(long(X)) is long(__long_log10(X));
  exp(long(X)) is long(__long_exp(X));
  long(X)**long(Y) is long(__long_power(X,Y));
}

implementation math over float is {
  min(float(X),float(Y)) is float(__float_min(X,Y));
  max(float(X),float(Y)) is float(__float_max(X,Y));
  random(float(X)) is float(__float_random(X));
  sqrt(float(X)) is float(__float_sqrt(X));
  cbrt(float(X)) is float(__float_cbrt(X));
  ceil(float(X)) is float(__float_ceil(X));
  floor(float(X)) is float(__float_floor(X));
  round(float(X)) is float(__float_round(X));
  log(float(X)) is float(__float_log(X));
  log10(float(X)) is float(__float_log10(X));
  exp(float(X)) is float(__float_exp(X));
  float(X)**float(Y) is float(__float_power(X,Y));
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
  sin(float(X)) is float(__float_sin(X));
  asin(float(X)) is float(__float_asin(X));
  cos(float(X)) is float(__float_cos(X));
  acos(float(X)) is float(__float_acos(X));
  tan(float(X)) is float(__float_tan(X));
  atan(float(X)) is float(__float_atan(X));
  sinh(float(X)) is float(__float_sinh(X));
  cosh(float(X)) is float(__float_cosh(X));
  tanh (float(X)) is float(__float_tanh(X));
}

-- Unary minus
# - #(?X)# ==> __uminus(X);