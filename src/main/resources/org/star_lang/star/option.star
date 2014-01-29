private import strings
private import base

/*
type option of %a is
    none
    or some(%a);
*/  

implementation pPrint over option of %a where pPrint over %a is {
  ppDisp(X) is show(X)
} using {
  show(none) is ppStr("none");
  show(some(X)) is ppSequence(0,cons(ppStr("some("),cons(ppDisp(X),cons(ppStr(")"),nil))));
}
  
someValue has type (option of %a) => %a;
someValue(some(x)) is x;
  
isSome(some(_)) is true;
isSome(_) default is false;
  
isNone(none) is true;
isNone(_) default is false;
