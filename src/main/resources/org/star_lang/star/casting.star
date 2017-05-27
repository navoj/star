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

public contract coercion over (%s,%d) is {
  coerce has type (%s)=>%d;
}

public implementation coercion over (string,boolean) is {
  fun coerce(string(X)) is __string_boolean(X);
}

public implementation coercion over (string,list of integer) is {
  fun coerce(string(X)) is __string_array(X);
}

public implementation coercion over (string,integer) is {
  fun coerce(string(X)) is __string_integer(X);
}

public implementation coercion over (string,long) is {
  fun coerce(string(X)) is __string_long(X);
}

public implementation coercion over (string,float) is {
  fun coerce(string(X)) is __string_float(X);
}

public implementation coercion over (string,string) is {
  fun coerce(S) is S;
}

public implementation coercion over (boolean,string) is {
  fun coerce(true) is "true"
   |  coerce(false) is "false"
}

public implementation coercion over (list of integer,string) is {
  fun coerce(L) is string(__array_string(L))
}

public implementation coercion over (integer,string) is {
  fun coerce(integer(I)) is string(__integer_string(I))
}

public implementation coercion over (long,string) is {
  fun coerce(long(L)) is string(__long_string(L));
}

public implementation coercion over (float,integer) is {
  fun coerce(float(F)) is integer(__float_integer(F))
}

public implementation coercion over (float,long) is {
  fun coerce(float(F)) is long(__float_long(F))
}

public implementation coercion over (float, float) is {
  fun coerce(X) is X
}

public implementation coercion over (float,string) is {
  fun coerce(float(F)) is string(__float_string(F))
   |  coerce(nonFLoat) is "nonFloat"
}

public implementation coercion over (integer, integer) is {
  fun coerce(I) is I;
}

public implementation coercion over (integer,long) is {
  fun coerce(integer(X)) is long(__integer_long(X));
}

public implementation coercion over (integer,float) is {
  fun coerce(integer(X)) is float(__integer_float(X));
}

public implementation coercion over (long,integer) is {
  fun coerce(long(X)) is integer(__long_integer(X));
}

public implementation coercion over (long, long) is {
  fun coerce(I) is I;
}

public implementation coercion over (long,float) is {
  fun coerce(long(X)) is float(__long_float(X));
}

public implementation coercion over (string,uri) is {
  fun coerce(string(S)) is __string2uri(S)
}

public implementation coercion over (uri,string) is {
  fun coerce(U) is string(__uri2string(U));
}

public implementation coercion over (quoted,string) is {
  fun coerce(stringAst(_,S)) is S
   |  coerce(<|nonString|>) is nonString
   |  coerce(Q) is raise __string_concat(__macro_display(Q)," is not a string")
}

public implementation coercion over (string,quoted) is {
  fun coerce(string(S)) is stringAst(noWhere,string(S))
   |  coerce(nonString) is <|nonString|>
}

public implementation coercion over (quoted,integer) is {
  fun coerce(integerAst(_,S)) is S
   |  coerce(<|nonInteger|>) is nonInteger
   |  coerce(Q) is raise __string_concat(__macro_display(Q)," is not an integer")
}

public implementation coercion over (integer,quoted) is {
  fun coerce(integer(Ix)) is integerAst(noWhere,integer(Ix))
   |  coerce(nonInteger) is <|nonInteger|>
}

public implementation coercion over (quoted,long) is {
  fun coerce(longAst(_,S)) is S
   |  coerce(<|nonLong|>) is nonLong
   |  coerce(Q) is raise __string_concat(__macro_display(Q)," is not a long")
}

public implementation coercion over (long,quoted) is {
  fun coerce(long(Ix)) is longAst(noWhere,long(Ix))
   |  coerce(nonLong) is <|nonLong|>
}

public implementation coercion over (quoted,float) is {
  fun coerce(floatAst(_,S)) is S
   |  coerce(<|nonFloat|>) is nonFloat
   |  coerce(Q) is raise __string_concat(__macro_display(Q)," is not a float")
}

public implementation coercion over (float,quoted) is {
  fun coerce(float(Dx)) is floatAst(noWhere,float(Dx))
   |  coerce(nonFloat) is <|nonFloat|>
}

public implementation for all t such that 
  coercion over (cons of t,quoted) where coercion over (t,quoted) is {
  fun coerce(L) is quoteList(L)
} using {
  fun quoteList(nil) is <|nil|>
   |  quoteList(cons(H,T)) is <|cons(?(H as quoted),?(quoteList(T)))|>
}

public implementation for all t such that
    coercion over (quoted,cons of t) where coercion over (quoted,t) is {
  fun coerce(Q) is unquoteList(Q)
} using {
  fun unquoteList(<|nil|>) is nil
   |  unquoteList(<|cons(?H,?T)|>) is cons(H as t,unquoteList(T))
}
 