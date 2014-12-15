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

contract coercion over (%s,%d) is {
  coerce has type (%s)=>%d;
}

implementation coercion over (string,boolean) is {
  coerce(string(X)) is __string_boolean(X);
}

implementation coercion over (string,char) is {
  coerce(string(X)) is __string_char(X);
}

implementation coercion over (string,integer) is {
  coerce(string(X)) is __string_integer(X);
}

implementation coercion over (string,long) is {
  coerce(string(X)) is __string_long(X);
}

implementation coercion over (string,float) is {
  coerce(string(X)) is __string_float(X);
}

implementation coercion over (string,decimal) is {
  coerce(string(X)) is __string_decimal(X);
}

implementation coercion over (string,binary) is {
  coerce(S) is __string_binary(S);
}

implementation coercion over (string,string) is {
  coerce(S) is S;
}

implementation coercion over (boolean,string) is {
  coerce(true) is "true";
  coerce(false) is "false";
}

implementation coercion over (char,string) is {
  coerce(char(C)) is string(__char_string(C));
}

implementation coercion over (integer,string) is {
  coerce(integer(I)) is string(__integer_string(I));
}

implementation coercion over (long,string) is {
  coerce(long(L)) is string(__long_string(L));
}

implementation coercion over (float,integer) is {
  coerce(float(F)) is integer(__float_integer(F));
}
implementation coercion over (float,long) is {
  coerce(float(F)) is long(__float_long(F));
}

implementation coercion over (float,decimal) is {
  coerce(float(F)) is decimal(__float_decimal(F));
}

implementation coercion over (float, float) is {
  coerce(X) is X;
}

implementation coercion over (float,string) is {
  coerce(float(F)) is string(__float_string(F));
  coerce(nonFLoat) is "nonFloat"
}

implementation coercion over (decimal,string) is {
  coerce(decimal(D)) is string(__decimal_string(D));
}

implementation coercion over (integer, integer) is {
  coerce(I) is I;
}

implementation coercion over (integer,long) is {
  coerce(integer(X)) is long(__integer_long(X));
}

implementation coercion over (integer,float) is {
  coerce(integer(X)) is float(__integer_float(X));
}

implementation coercion over (integer,decimal) is {
  coerce(integer(X)) is decimal(__integer_decimal(X));
}

implementation coercion over (long,integer) is {
  coerce(long(X)) is integer(__long_integer(X));
}

implementation coercion over (long, long) is {
  coerce(I) is I;
}

implementation coercion over (long,float) is {
  coerce(long(X)) is float(__long_float(X));
}

 implementation coercion over (long,decimal) is {
  coerce(long(X)) is decimal(__long_decimal(X));
}
 
implementation coercion over (string,uri) is {
  coerce(string(S)) is __string2uri(S);
}

implementation coercion over (uri,string) is {
  coerce(U) is string(__uri2string(U));
}

implementation coercion over (binary,string) is {
  coerce(S) is __binary_string(S);
}

implementation coercion over (integer,char) is {
  coerce(integer(I)) is char(__integer_char(I));
}

implementation coercion over (char,integer) is {
  coerce(char(C)) is integer(__char_integer(C));
}

implementation coercion over (long,char) is {
  coerce(long(I)) is char(__integer_char(__long_integer(I)));
}

implementation coercion over (char,long) is {
  coerce(char(C)) is long(__integer_long(__char_integer(C)));
}

implementation coercion over (quoted,string) is {
  coerce(stringAst(_,S)) is S;
  coerce(<|nonString|>) is nonString;
  coerce(Q) is raise __string_concat(__macro_display(Q)," is not a string");
}

implementation coercion over (string,quoted) is {
  coerce(string(S)) is stringAst(noWhere,string(S));
  coerce(nonString) is <|nonString|>;
}

implementation coercion over (quoted,integer) is {
  coerce(integerAst(_,S)) is S;
  coerce(<|nonInteger|>) is nonInteger;
  coerce(Q) is raise __string_concat(__macro_display(Q)," is not an integer");
}

implementation coercion over (integer,quoted) is {
  coerce(integer(Ix)) is integerAst(noWhere,integer(Ix));
  coerce(nonInteger) is <|nonInteger|>;
}

implementation coercion over (quoted,long) is {
  coerce(longAst(_,S)) is S;
  coerce(<|nonLong|>) is nonLong;
  coerce(Q) is raise __string_concat(__macro_display(Q)," is not a long");
}

implementation coercion over (long,quoted) is {
  coerce(long(Ix)) is longAst(noWhere,long(Ix));
  coerce(nonLong) is <|nonLong|>;
}

implementation coercion over (quoted,float) is {
  coerce(floatAst(_,S)) is S;
  coerce(<|nonFloat|>) is nonFloat;
  coerce(Q) is raise __string_concat(__macro_display(Q)," is not a float");
}

implementation coercion over (float,quoted) is {
  coerce(float(Dx)) is floatAst(noWhere,float(Dx));
  coerce(nonFloat) is <|nonFloat|>;
}

implementation for all t such that 
  coercion over (cons of t,quoted) where coercion over (t,quoted) is {
  coerce(L) is quoteList(L);
} using {
  quoteList(nil) is <|nil|>;
  quoteList(cons(H,T)) is <|cons(?(H as quoted),?(quoteList(T)))|>
}

implementation for all t such that
    coercion over (quoted,cons of t) where coercion over (quoted,t) is {
  coerce(Q) is unquoteList(Q);
} using {
  unquoteList(<|nil|>) is nil;
  unquoteList(<|cons(?H,?T)|>) is cons(H as t,unquoteList(T));
}
 