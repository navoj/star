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
private import casting;
private import strings;
private import option;

-- dates and time

type date is date(long_) or never;

implementation coercion over (date,string) is {
  fun coerce(date(D)) is string(__date_string(D))
   |  coerce(never) is "never"
}

implementation coercion over (string,date) is {
  fun coerce(string(S)) where __string_date(S) has value long(D) is date(D)
   |  coerce(_) default is never
}

implementation coercion over (date, long) is {
  fun coerce(date(D)) is long(D)
   |  coerce(never) is nonLong
}

implementation coercion over (long,date) is {
  fun coerce(long(D)) is date(D)
   |  coerce(nonLong) is never
}

implementation formatting over date is {
  fun _format(date(D),string(F)) is ppStr(string(__format_date(D,F)));
}

fun parse_date(string(S),string(F)) is date(__parse_date(S,F));

fun format_date(date(D),string(F)) is string(__format_date(D,F))
 |  format_date(never,_) is "never"

implementation pPrint over date is {
  fun ppDisp(D) is ppStr(D as string)
}

implementation comparable over date is {
  fun date(X)<date(Y) is __long_lt(X,Y)
   |  _ < _ default is false
  fun date(X)=<date(Y) is __long_le(X,Y)
  |  _ =< _ default is false
  fun date(X)>date(Y) is __long_gt(X,Y)
   |  _ > _ default is false
  fun date(X)>=date(Y) is __long_ge(X,Y)
   |  _ >= _ default is false
}

implementation largeSmall over date is {
  def largest is date(0x7fffffffffffffffL_)
  def smallest is date(0x0L_)
}

fun now() is date(_now());

fun today() is date(_today());

fun timeDiff(date(F),date(T)) is long(__long_minus(F,T))
 |  timeDiff(never,_) is nonLong
 |  timeDiff(_,never) is nonLong

fun timeDelta(date(T),long(D)) is date(__long_plus(T,D))
 |  timeDelta(never,_) is never