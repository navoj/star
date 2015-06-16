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
 * @author fgm
 *
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
  
fun hashCode(X) is integer(__hashCode(X));

private fun showBits(X) is let{
  fun showBit(0) is "0"
   |  showBit(1) is "1"

  fun showBts(0) is ""
   |  showBts(N) is showBts(N.>>>.1)++showBit(N.&.1)
} in showBts(X)++"B";  
 