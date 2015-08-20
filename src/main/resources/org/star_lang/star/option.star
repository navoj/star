private import strings
private import base
private import folding
private import casting
private import iterable

/*
type option of %a is
    none
    or some(%a);
*/  

implementation pPrint over option of %a where pPrint over %a is {
  fun ppDisp(X) is sho(X)
} using {
  fun sho(none) is ppStr("none")
   |  sho(some(X)) is ppSequence(0,cons(ppStr("some("),cons(ppDisp(X),cons(ppStr(")"),nil))));
}
  
someValue has type (option of %a) => %a;
fun someValue(some(x)) is x;
  
fun isSome(some(_)) is true
 |  isSome(_) default is false;
  
fun isNone(none) is true
 |  isNone(_) default is false;

implementation mappable over option is {
  map = _optionMap
}

implementation for all t such that
    coercion over (option of t,string) where coercion over (t,string) is {
  fun coerce(X) is toString(X)
} using {
  fun toString(some(X)) is X as string
   |  toString(none) is "none"
}
 
fun _optionMap(_,none) is none
 |  _optionMap(F,some(X)) is some(F(X))

fun _optionPartial(none,_) is none
 |  _optionPartial(some(X),F) is F(X)

fun _optionDeflt(none,D) is D()
 |  _optionDeflt(some(X),_) is X

fun _optionSelect(none,_,D) is D()
 |  _optionSelect(some(X),A,_) is A(X)

# prefix((present),500);

# ?E has value ?P :: condition :- E::expression :& P::pattern;
# present ?E :: condition :- E :: expression;

contract optional over t determines v is {
  _hasValue has type (v)<=t
}

implementation for all t such that
  optional over option of t determines t is {
  ptn _hasValue(X) from some(X)
}

#?R ?. ?F ==> (R has value #$R ? some(#$R.F) : none)
#?R or else ?E ==> _optionDeflt(R, ()=>E)

#?E has value ?P ==> E matches _hasValue(P);

# present ?E ==> E matches _hasValue(_);

implementation for all t,e such that
  iterable over option of t determines e where iterable over t determines e is {
  fun _iterate(none,_,S) is S
   |  _iterate(some(M),F,S) is iterate(M,F,S)
} using {
  fun iterate(M,F,S) is _iterate(M,F,S)
}

implementation for all t such that
  coercion over (quoted,option of t) where coercion over (quoted,t) is {
    coerce = optionDeQuote
  } using {
    fun optionDeQuote(Q) is some(Q as t)
  }

implementation for all t such that
  coercion over (option of t,quoted) where coercion over (t,quoted) is {
    coerce = optionQuote
  } using {
    fun optionQuote(some(Q)) is Q as quoted
     |  optionQuote(none) is <|none|>
  }
