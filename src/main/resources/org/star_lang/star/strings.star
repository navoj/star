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
private import arithmetic;
private import casting;

-- Structured string type
public type pP is 
     ppStr(string)
  or ppSequence(integer,cons of pP)
  or ppNl
  or ppSpace;

public implementation sizeable over string is {
  fun isEmpty(S) is S="";
  fun size(string(S)) is integer(__str_length(S));
}
 
public implementation sequence over string determines integer is {
  fun _cons(H,T) is __string_cons(H,T);
  fun _apnd(S,C) is __string_apnd(S,C); 
  ptn _empty() from "";
  ptn _pair(H,T) from __string_pair(H,T)
  ptn _back(T,H) from __string_back(T,H);
  fun _nil() is "";
}

public implementation concatenate over string is {
  fun string(S1)++string(S2) is string(__string_concatenate(S1,S2));
}

public implementation indexable over string determines (integer,integer) is {
  fun _index(string(S),integer(Ix)) where __integer_ge(Ix,0_) and __integer_lt(Ix,__str_length(S)) is some(integer(__get_codepoint(S,Ix)))
   |  _index(_,_) default is none;
  fun _set_indexed(string(S),integer(Ix),integer(C)) is string(__substitute_codepoint(S,Ix,C));
  fun _delete_indexed(string(S),integer(Ix)) is string(__delete_codepoint(S,Ix));
}
 
public implementation sliceable over string determines integer is {
  fun _slice(string(S),integer(Fr),integer(To)) is string(__string_slice(S,Fr,To));
  fun _tail(string(S),integer(Fr)) is string(__string_slice(S,Fr,__str_length(S)));
  fun _splice(string(S),integer(Fr),integer(To),string(Rp)) is string(__string_splice(S,Fr,To,Rp));
}

public fun findstring(string(S),string(T),integer(Ix)) is integer(__string_find(S,T,Ix));

public fun isSubstring(string(S),string(T)) is __integer_ge(__string_find(T,S,0_),0_);

public fun isIdentifierStart(string(S)) is __isIdentifierStart(__get_codepoint(S,0_));

public fun isUpperCase(integer(C)) is __is_upper_case(C)

public fun isLowerCase(integer(C)) is __is_lower_case(C)

public fun toUpperCase(string(S)) is string(__uppercase(S))

public fun toLowerCase(string(S)) is string(__lowercase(S))

public fun trim(`[ \t\n\r]*(.*:A)[ \t\n\r]+`) is A
 |  trim(`[ \t\n\r]*(.*:A)`) is A;


-- hex functions
public fun integer2hex(integer(I)) is string(__integer_hex(I));
public fun long2hex(long(I)) is string(__long_hex(I));
public fun hex2integer(string(S)) is __hex_integer(S);
public fun hex2long(string(S)) is __hex_long(S);

-- string formatting support

public fun display(T) is flattenPP(ppDisp(T));

public fun format(T,S) is flattenPP(_format(T,S));

public fun flattenPP(P) is revImplode(fltn(P,0,0,100,nil,nlFun(P,0,100),(Off,Ind,Mx,SoF) => SoF));

private fun revImplode(X) is string(__string_rev_implode(X));


-- Private implementation

private
fun fltn(ppStr(St),Off,Indent,Max,SoFar,Nl,Cont) where size(St)+Off>Max is 
      Cont(size(St),Indent,Max,cons(St,cons(spaces(Indent),cons("\n",SoFar))))
 |  fltn(ppStr(St),Off,Indent,Max,SoFar,Nl,Cont) is Cont(Off+size(St),Indent,Max,cons(St,SoFar))
 |  fltn(ppNl,Off,Indent,Max,SoFar,Nl,Cont) is Cont(Indent,Indent,Max,Nl(Indent,SoFar))
 |  fltn(ppSpace,Off,Indent,Max,SoFar,Nl,Cont) is fltn(ppStr(" "),Off,Indent,Max,SoFar,Nl,Cont)
 |  fltn(ppSequence(Ind,Seq),Off,Indent,Max,SoFar,Nl,Cont) is 
      fltnSeq(Seq,Off,Indent+Ind,Max,SoFar,nlFun(ppSequence(Ind,Seq),Off,Max),(Offx,SoF) => Cont(Offx,Indent,Max,SoF));

private
fun nlFun(p,Off,Max) where width(p)+Off=<Max is (Indent,SoFar) => SoFar
 |  nlFun(_,_,_) default is (Indent,SoFar) => cons(spaces(Indent),cons("\n",SoFar));

private
fun fltnSeq(nil,Off,Indent,Max,SoFar,Nl,Cont) is Cont(Off,SoFar)
 |  fltnSeq(cons(ppSpace,T),Off,Indent,Max,SoFar,Nl,Cont) is 
      fltn(ppStr(" "),Off,Indent,Max,SoFar,Nl,(Offx,Ind,Mx,SoF) => fltnSeq(dropSpaces(T),Offx,Ind,Max,SoF,Nl,Cont))
 |  fltnSeq(cons(H,T),Off,Indent,Max,SoFar,Nl,Cont) is 
      fltn(H,Off,Indent,Max,SoFar,Nl,(Offx,Ind,Mx,SoF) => fltnSeq(T,Offx,Ind,Max,SoF,Nl,Cont));
  
private
fun dropSpaces(nil) is nil
 |  dropSpaces(cons(ppSpace,T)) is dropSpaces(T)
 |  dropSpaces(T) default is T;
  
private
fun width(ppStr(S)) is size(S)
 |  width(ppNl) is 0
 |  width(ppSpace) is 1
 |  width(ppSequence(_,Seq)) is valof{
      var Cx := 0;
      var S := Seq;
      while S matches cons(H,T) do{
        if H=ppSpace then {
          Cx := Cx+1;
          S := dropSpaces(T)
        } else {
          Cx := width(H)+Cx;
          S := T;
        }
      }
      valis Cx;
    }

public fun spaces(integer(Ix)) is string(__spaces(Ix));

public implementation explosion over (string,cons of integer) is {
  fun explode(string(S)) is __string_explode(S);
  
  fun implode(L) is string(__string_implode(L));
}

public implementation explosion over (string,list of integer) is {
  fun explode(string(S)) is __string_array(S);
  fun implode(A) is string(__array_string(A))
}

public implementation coercion over (string,list of integer) is {
  fun coerce(string(S)) is __string_array(S);
}

public implementation coercion over (list of integer,string) is {
  fun coerce(L) is string(__array_string(L));
}

public implementation reversible over string is {
  fun reverse(string(S)) is string(__string_reverse(S));
}
  
 -- The display handling contracts
  
public contract pPrint over %t is {
  ppDisp has type (%t)=>pP
};

public implementation pPrint over pP is {
  fun ppDisp(X) is ppStr(__display(X));
};

public implementation equality over pP is {
 fun  L=R is __equal(L,R);
 fun hashCode(X) is integer(__hashCode(X))
};
  
public implementation pPrint over boolean is {
  fun ppDisp(true) is ppStr("true")
   |  ppDisp(false) is ppStr("false");
}

public implementation pPrint over ((%s,%t)) where pPrint over %s and pPrint over %t is {
  fun ppDisp(E) is dispTuple(E)
} using {
  fun dispTuple((E1,E2)) is ppSequence(0,cons(ppStr("("),cons(ppDisp(E1),cons(ppStr(", "), cons(ppDisp(E2),cons(ppStr(")"),nil))))))
}

public implementation pPrint over ((%s,%t,%u)) where pPrint over %s and pPrint over %t and pPrint over %u is {
  fun ppDisp(E) is dispTuple(E)
} using {
  fun dispTuple((E1,E2,E3)) is ppSequence(0,cons(ppStr("("),cons(ppDisp(E1),cons(ppStr(", "), cons(ppDisp(E2), cons(ppStr(", "), cons(ppDisp(E3), cons(ppStr(")"),nil))))))))
}

public implementation pPrint over %t default is {
  fun ppDisp(X) is ppStr(__display(X));
}

public implementation pPrint over integer is {
  fun ppDisp(integer(I)) is ppStr(string(__integer_string(I)))
}
 
public implementation pPrint over long is {
  fun ppDisp(long(L)) is ppStr(string(__long_string(L)))
}

public implementation pPrint over float is {
  fun ppDisp(float(F)) is ppStr(string(__float_string(F)));
}

public implementation pPrint over string is {
  fun ppDisp(string(S)) is ppStr(string(__string_quote(S)))
}

public implementation pPrint over astLocation is {
  fun ppDisp(L) is ppStr(string(__display_location(L)));
};

public implementation pPrint over exception is {
  fun ppDisp(E) is showException(E)
} using {
  fun showException(exception(Code,Reason,W)) is ppSequence(0,cons(displayCode(Code),cons(ppStr(__display(Reason)),cons(ppStr("@"),cons(ppDisp(W),nil)))));
  
  fun displayCode(nonString) is ppStr("")
   |  displayCode(C) is ppDisp(C);
}

public sequenceDisplay has type (string,%t)=>pP where sequence over %t determines %e and pPrint over %e;
fun sequenceDisplay(lbl,L) is ppSequence(0,cons(ppStr(lbl),cons(ppStr(" of ["),cons(ppSequence(2,dispSeq(L,ppStr(""))),cons(ppStr("]"),nil)))))
using {
  def inter is ppStr(", ");
  fun dispSeq(_empty(),_) is nil
   |  dispSeq(_pair(H,T),Sep) is cons(Sep,cons(ppDisp(H),dispSeq(T,inter)));
}
  
public contract formatting over %t is {
  _format has type (%t,string)=>pP;
}
 
public implementation formatting over string is {
  fun _format(string(S),string(F)) is ppStr(string(__format_string(S,F)));
}
 
public implementation formatting over integer is {
  fun _format(integer(Ix),string(F)) is ppStr(string(__format_integer(Ix,F)));
}

public implementation formatting over long is {
  fun _format(long(Ix),string(F)) is ppStr(string(__format_long(Ix,F)));
}

public implementation formatting over float is {
  fun _format(float(Dx),string(F)) is ppStr(string(__format_float(Dx,F)));
}

-- Macro replace the old logMsg ...
-- import as java code so that it can be easily replaced
java org.star_lang.star.operators.system.runtime.LogMsg;

# logMsg(?L,?M) ==> logMsg(L, #(#__location__)#,M);

public type level is severe or warning or info or config or fine or finer or finest;

private 
fun levelName(severe) is "SEVERE"
 |  levelName(warning) is "WARNING"
 |  levelName(info) is "INFO"
 |  levelName(config) is "CONFIG"
 |  levelName(fine) is "FINE"
 |  levelName(finer) is "FINER"
 |  levelName(finest) is "FINEST";

public logMsg has type (level,string,string)=>();
prc logMsg(Lvl,Loc,Msg) do __logMsg(levelName(Lvl),Loc,Msg);

public fun getResource(U) is string(__getResource(U));
