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
  ppDisp(X) is sho(X)
} using {
  sho(none) is ppStr("none");
  sho(some(X)) is ppSequence(0,cons(ppStr("some("),cons(ppDisp(X),cons(ppStr(")"),nil))));
}
  
someValue has type (option of %a) => %a;
someValue(some(x)) is x;
  
isSome(some(_)) is true;
isSome(_) default is false;
  
isNone(none) is true;
isNone(_) default is false;

implementation mappable over option is {
  map = _optionMap
}

implementation for all t such that
    coercion over (option of t,string) where coercion over (t,string) is {
  coerce(X) is toString(X)
} using {
  toString(some(X)) is X as string
  toString(none) is "none"
}
 
_optionMap(_,none) is none
_optionMap(F,some(X)) is some(F(X))

_optionPartial(none,_) is none
_optionPartial(some(X),F) is F(X)

_optionDeflt(none,D) is D()
_optionDeflt(some(X),_) is X

# ?E has value ?P :: condition :- E::expression :& P::pattern;

#?R ?. ?F ==> (R has value #$R ? some(#$R.F) | none)
#?R or else ?E ==> _optionDeflt(R,fn()=>E)

# ?O. #(?M)#[?K] has value ?V ==> _index(O.M,K) matches some(V);
# #(?M)#[?K] has value ?V ==> _index(M,K) matches some(V);
#?E has value ?P ==> E matches some(P);


implementation for all t,e such that
  iterable over option of t determines e where iterable over t determines e is {
  _iterate(none,_,S) is S
  _iterate(some(M),F,S) is iterate(M,F,S)
} using {
  iterate(M,F,S) is _iterate(M,F,S)
}
