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
  coerce(date(D)) is string(__date_string(D));
  coerce(never) is "never"
}

implementation coercion over (string,date) is {
  coerce(string(S)) where __string_date(S) has value long(D) is date(D);
  coerce(_) default is never;
}

implementation coercion over (date, long) is {
  coerce(date(D)) is long(D);
  coerce(never) is nonLong;
}

implementation coercion over (long,date) is {
  coerce(long(D)) is date(D);
  coerce(nonLong) is never;
}

implementation formatting over date is {
  _format(date(D),string(F)) is ppStr(string(__format_date(D,F)));
}

parse_date(string(S),string(F)) is date(__parse_date(S,F));

format_date(date(D),string(F)) is string(__format_date(D,F));
format_date(never,_) is "never";

implementation pPrint over date is {
  ppDisp(D) is ppStr(D as string)
}

implementation comparable over date is {
  date(X)<date(Y) is __long_lt(X,Y);
  _ < _ default is false;
  date(X)<=date(Y) is __long_le(X,Y);
  _ <= _ default is false;
  date(X)>date(Y) is __long_gt(X,Y);
  _ > _ default is false;
  date(X)>=date(Y) is __long_ge(X,Y);
  _ >= _ default is false;
}

implementation largeSmall over date is {
  largest is date(0x7fffffffffffffffL_);
  smallest is date(0x0L_);
}

now() is date(_now());

today() is date(_today());

timeDiff(date(F),date(T)) is long(__long_minus(F,T));
timeDiff(never,_) is nonLong;
timeDiff(_,never) is nonLong;

timeDelta(date(T),long(D)) is date(__long_plus(T,D));
timeDelta(never,_) is never;