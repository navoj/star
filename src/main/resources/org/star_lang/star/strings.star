/**
 * implementation of standard contracts over strings. 
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
private import arithmetic;
private import casting;

-- Structured string type
type pP is 
     ppStr(string)
  or ppSequence(integer,cons of pP)
  or ppNl
  or ppSpace;

implementation sizeable over string is {
  fun isEmpty(S) is S="";
  fun size(string(S)) is integer(__str_length(S));
}
 
implementation sequence over string determines char is {
  fun _cons(H,T) is __string_cons(H,T);
  fun _apnd(S,C) is __string_apnd(S,C); 
  ptn _empty() from "";
  ptn _pair(H,T) from __string_pair(H,T)
  ptn _back(T,H) from __string_back(T,H);
  fun _nil() is "";
}

implementation concatenate over string is {
  fun string(S1)++string(S2) is string(__string_concatenate(S1,S2));
}

implementation indexable over string determines (integer,char) is {
  fun _index(string(S),integer(Ix)) where __integer_ge(Ix,0_) and __integer_lt(Ix,__str_length(S)) is some(char(__get_char(S,Ix)))
   |  _index(_,_) default is none;
  fun _set_indexed(string(S),integer(Ix),char(C)) is string(__substitute_char(S,Ix,C)); 
  fun _delete_indexed(string(S),integer(Ix)) is string(__delete_char(S,Ix));
}
 
implementation sliceable over string determines integer is {
  fun _slice(string(S),integer(Fr),integer(To)) is string(__string_slice(S,Fr,To));
  fun _tail(string(S),integer(Fr)) is string(__string_slice(S,Fr,__str_length(S)));
  fun _splice(string(S),integer(Fr),integer(To),string(Rp)) is string(__string_splice(S,Fr,To,Rp));
}

fun findstring(string(S),string(T),integer(Ix)) is integer(__string_find(S,T,Ix));

fun isSubstring(string(S),string(T)) is __integer_ge(__string_find(T,S,0_),0_);

fun isIdentifierStart(string(S)) is __isIdentifierStart(__get_char(S,0_));

fun isUnicodeIdentifier(string(S)) is __isUnicodeIdentifier(S);

fun isUpperCase(char(C)) is __isUpperCase(C)

fun isLowerCase(char(C)) is __isLowerCase(C)

fun toUpperCase(string(S)) is string(__uppercase(S))

fun toLowerCase(string(S)) is string(__lowercase(S))

fun trim(`[ \t\n\r]*(.*:A)[ \t\n\r]+`) is A
 |  trim(`[ \t\n\r]*(.*:A)`) is A;


-- hex functions
fun integer2hex(integer(I)) is string(__integer_hex(I));
fun long2hex(long(I)) is string(__long_hex(I));
fun hex2integer(string(S)) is __hex_integer(S);
fun hex2long(string(S)) is __hex_long(S);

-- string formatting support

fun display(T) is flattenPP(ppDisp(T));

fun format(T,S) is flattenPP(_format(T,S));

fun flattenPP(P) is revImplode(fltn(P,0,0,100,nil,nlFun(P,0,100),(Off,Ind,Mx,SoF) => SoF));

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
fun nlFun(p,Off,Max) where width(p)+Off<=Max is (Indent,SoFar) => SoFar
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

fun spaces(integer(Ix)) is string(__spaces(Ix));

fun revImplode(X) is string(__string_rev_implode(X));

implementation explosion over (string,cons) determines char is {
  fun explode(string(S)) is __string_explode(S);
  
  fun implode(L) is string(__string_implode(L));
}

implementation explosion over (string,list) determines char is {
  fun explode(string(S)) is __string_array(S);
  fun implode(A) is string(__string_implode(A))
}

implementation coercion over (string,list of char) is {
  fun coerce(string(S)) is __string_array(S);
}

implementation reversible over string is {
  fun reverse(string(S)) is string(__string_reverse(S));
}
  
 -- The display handling contracts
  
contract pPrint over %t is {
  ppDisp has type (%t)=>pP
};

implementation pPrint over pP is {
  fun ppDisp(X) is ppStr(__display(X));
};

implementation equality over pP is {
 fun  L=R is __equal(L,R);
};
  
implementation pPrint over boolean is {
  fun ppDisp(true) is ppStr("true")
   |  ppDisp(false) is ppStr("false");
}

implementation pPrint over char is {
  fun ppDisp(char(ch)) is ppSequence(0,cons(ppStr("'"),cons(ppStr(string(__char_string(ch))),cons(ppStr("'"),nil))))
}

implementation pPrint over ((%s,%t)) where pPrint over %s and pPrint over %t is {
  fun ppDisp(E) is dispTuple(E)
} using {
  fun dispTuple((E1,E2)) is ppSequence(0,cons(ppStr("("),cons(ppDisp(E1),cons(ppStr(", "), cons(ppDisp(E2),cons(ppStr(")"),nil))))))
}

implementation pPrint over ((%s,%t,%u)) where pPrint over %s and pPrint over %t and pPrint over %u is {
  fun ppDisp(E) is dispTuple(E)
} using {
  fun dispTuple((E1,E2,E3)) is ppSequence(0,cons(ppStr("("),cons(ppDisp(E1),cons(ppStr(", "), cons(ppDisp(E2), cons(ppStr(", "), cons(ppDisp(E3), cons(ppStr(")"),nil))))))))
}

implementation pPrint over %t default is {
  fun ppDisp(X) is ppStr(__display(X));
}

implementation pPrint over integer is {
  fun ppDisp(integer(I)) is ppStr(string(__integer_string(I)))
}
 
implementation pPrint over long is {
  fun ppDisp(long(L)) is ppStr(string(__long_string(L)))
}

implementation pPrint over float is {
  fun ppDisp(float(F)) is ppStr(string(__float_string(F)));
}

implementation pPrint over decimal is {
  fun ppDisp(decimal(D)) is ppStr(string(__decimal_string(D)));
}

implementation pPrint over string is {
  fun ppDisp(string(S)) is ppStr(string(__string_quote(S)))
}

implementation pPrint over astLocation is {
  fun ppDisp(L) is ppStr(string(__display_location(L)));
};

implementation pPrint over exception is {
  fun ppDisp(E) is showException(E)
} using {
  fun showException(exception(Code,Reason,W)) is ppSequence(0,cons(displayCode(Code),cons(ppStr(__display(Reason)),cons(ppStr("@"),cons(ppDisp(W),nil)))));
  
  fun displayCode(nonString) is ppStr("")
   |  displayCode(C) is ppDisp(C);
}
  

sequenceDisplay has type (string,%t)=>pP where sequence over %t determines %e and pPrint over %e;
fun sequenceDisplay(lbl,L) is ppSequence(0,cons(ppStr(lbl),cons(ppStr(" of ["),cons(ppSequence(2,dispSeq(L,ppStr(""))),cons(ppStr("]"),nil)))))
using {
  def inter is ppStr(", ");
  fun dispSeq(_empty(),_) is nil
   |  dispSeq(_pair(H,T),Sep) is cons(Sep,cons(ppDisp(H),dispSeq(T,inter)));
}
  
contract formatting over %t is {
  _format has type (%t,string)=>pP;
}
 
implementation formatting over string is {
  fun _format(string(S),string(F)) is ppStr(string(__format_string(S,F)));
}
 
implementation formatting over integer is {
  fun _format(integer(Ix),string(F)) is ppStr(string(__format_integer(Ix,F)));
}

implementation formatting over long is {
  fun _format(long(Ix),string(F)) is ppStr(string(__format_long(Ix,F)));
}

implementation formatting over float is {
  fun _format(float(Dx),string(F)) is ppStr(string(__format_float(Dx,F)));
}

-- Macro replace the old logMsg ...
-- import as java code so that it can be easily replaced
java org.star_lang.star.operators.system.runtime.LogMsg;

# logMsg(?L,?M) ==> logMsg(L, #(#__location__)#,M);

type level is severe or warning or info or config or fine or finer or finest;

private 
fun levelName(severe) is "SEVERE"
 |  levelName(warning) is "WARNING"
 |  levelName(info) is "INFO"
 |  levelName(config) is "CONFIG"
 |  levelName(fine) is "FINE"
 |  levelName(finer) is "FINER"
 |  levelName(finest) is "FINEST";

logMsg has type (level,string,string)=>();
prc logMsg(Lvl,Loc,Msg) do __logMsg(levelName(Lvl),Loc,Msg);

fun getResource(U) is string(__getResource(U));
