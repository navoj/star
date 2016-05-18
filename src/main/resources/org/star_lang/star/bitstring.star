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
private import sequences;
private import strings;

#right(".&.",700);
#right(".^.",700);
#right(".|.",720);
#right(".<<.",650);
#right(".>>.",650);
#right(".>>>.",650);
#prefix(".~.",650);
#prefix(".\#.",650);

contract bitstring over %t is {
  (.&.) has type (%t,%t)=>%t;
  (.^.) has type (%t,%t)=>%t;
  (.|.) has type (%t,%t)=>%t;
  (.<<.) has type (%t,%t)=>%t;
  (.>>.) has type (%t,%t)=>%t;
  (.>>>.) has type (%t,%t)=>%t;
  (.~.) has type (%t)=>%t;
  (.#.) has type (%t)=>integer;
}

implementation bitstring over integer is {
  fun integer(L) .&. integer(R) is integer(__integer_bit_and(L,R));
  fun integer(L) .|. integer(R) is integer(__integer_bit_or(L,R));
  fun integer(L) .^. integer(R) is integer(__integer_bit_xor(L,R));
  fun integer(L) .<<. integer(R) is integer(__integer_bit_shl(L,R));
  fun integer(L) .>>. integer(R) is integer(__integer_bit_sar(L,R));
  fun integer(L) .>>>. integer(R) is integer(__integer_bit_shr(L,R));
  fun .~. integer(L) is integer(__integer_bit_neg(L));
  fun .#. integer(L) is integer(__integer_bit_count(L));
}

implementation bitstring over long is {
  fun long(L) .&. long(R) is long(__long_bit_and(L,R));
  fun long(L) .|. long(R) is long(__long_bit_or(L,R));
  fun long(L) .^. long(R) is long(__long_bit_xor(L,R));
  fun long(L) .<<. long(R) is long(__long_bit_shl(L,R));
  fun long(L) .>>. long(R) is long(__long_bit_sar(L,R));
  fun long(L) .>>>. long(R) is long(__long_bit_shr(L,R));
  fun .~. long(L) is long(__long_bit_neg(L));
  fun .#. long(L) is integer(__long_bit_count(L));
}

private fun showBits(X) is let{
  fun showBit(0) is "0"
   |  showBit(1) is "1"

  fun showBts(0) is ""
   |  showBts(N) is showBts(N.>>>.1)++showBit(N.&.1)
} in showBts(X)++"B";  
 