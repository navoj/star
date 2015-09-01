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