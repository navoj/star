private import strings
private import base
private import folding

/*
type option of %a is
    none
    or some(%a);
*/  

#infix("unwraps",900);

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

_optionMap(_,none) is none
_optionMap(F,some(X)) is some(F(X))

_optionPartial(none,_) is none
_optionPartial(some(X),F) is F(X)

# ?L is some ?E :: condition :- E::expression :& L::pattern;

#?R ?. ?F ==> _optionMap(fn RR=>RR.F,R)
#?R ?? ?E ==> processOption(E) ## {
  #processOption(<|[?Ex]|>) is <|_optionPartial(?R,fn RR=>_index(RR,?Ex))|>
  #processOption(applyAst(Loc,nameAst(_,Op),Args)) where Op matches `\$[0-9]+` is <|_optionMap(fn RR=>?applyAst(Loc,<|RR|>,Args),?R)|>
}

#?P unwraps ?E ==> E matches some(P);

