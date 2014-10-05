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
private import macrosupport;

-- standard validation rules for Star
# #( ?S ; ?T)# :: statement :- S::statement :& T::statement;
# #(# ?S)# :: statement; -- We do not validate macro rules

-- packages
# ?Pk is package { ?Defs } ==> Defs;
# ?Pk is package{ ?Defs} :: statement :- Pk::id :& Defs ;* statement;

# ?Pk is package {} ==> {};
# ?Pk is package{ } :: statement :- Pk::id ;

# private #( ?S )# :: statement :- S :: statement;

-- import statement
# import ?Pkg :: statement :- Pkg::packageName;
# identifier is import ?Pkg :: statement :- Pkg::packageName;
# open ?E :: statement :- E::expression;

# identifier :: packageName;
# string :: packageName;
 
# java ?Cls :: statement :- Cls::className ## {
  # symbol::className;
  # (symbol)::className;
  # ?X . symbol :: className :- X::className;
  # ?X . (symbol) :: className :- X::className;
  # string :: className;
};

# type ?N is alias of ?T :: statement :- N::id :& T::typeExpression;
# type ?N of ?A is alias of ?T :: statement :- N::id :& A::typeArgs :& T::typeExpression;
# type ?N is ?Algebraic :: statement :- N::typeSpec :& Algebraic :: valueSpecifier;
# type ?N counts as ?M :: statement :- N::typeExpression :& M::typeSpec;

# ?N of ?A :: typeSpec :- #(N::id :| N::typeVar)# :& A::typeArgs;
# ?T where ?C :: typeSpec :- T::typeSpec :& C::typeConstraint;
# identifier :: typeSpec;
# (?T) :: typeSpec :- T::typeSpec;
 
# ?Tp where ?C :: typeArgs :- Tp::typeArgs :& C::typeConstraint;
# % identifier :: typeArgs;
# %% identifier :: typeArgs;
# %%identifier of ?A :: typeArgs :- A::typeArgs;
# identifier :: typeArgs;
# identifier of ?A :: typeArgs :- A::typeArgs;
# ( ?Args ) :: typeArgs :- Args::typeArgs;
# tuple?Args :: typeArgs :- Args:*typeArgs;
# ?X :: typeArgs :- error("$X not a valid type argument");

# %identifier :: typeVar;
# %%identifier :: typeVar;
# ?V :: typeVar :- V::id;
 
# ?A or ?B :: valueSpecifier :- A::valueSpecifier :& B::valueSpecifier;
# identifier :: valueSpecifier;
# identifier{ ?Els} :: valueSpecifier :-  Els;*typeAnnotation;
# identifier#@#(tuple?Arg)# :: valueSpecifier :- Arg :* typeExpression;
 
-- Type contracts
# contract ?Con is { ?Body} :: statement :- Con :: contractSpec :& Body;*typeAnnotation;

# ?Tp where ?C :: contractSpec :- Tp::contractSpec :& C::typeConstraint;
# ?N over ?Tp determines ?Te :: contractSpec :- N::id :& Tp::typeArgs :& Te::typeArgs;
# ?N over ?Tp :: contractSpec :- N::id :& Tp::typeArgs;

# implementation ?Con default is ?Body :: statement :- Con :: contractType :& Body::expression;
# implementation ?Con is ?Body :: statement :- Con :: contractType :& Body::expression;

# ?Tp where ?C :: contractType :- Tp::contractType :& C::typeConstraint;
# ?N over ?Tp determines ?Te :: contractType :- N::id :& Tp::typeExpression :& Te::typeExpression;
# ?N over ?Tp :: contractType :- N::id :& Tp::typeExpression;
# for all ?Tvs such that ?Tp :: contractType :- Tvs::typeVars :& Tp::contractType;

# identifier :: id;
# (symbol) :: id;
# ?N :: id :- error("$N must be an identifier");

-- Type annotations
# ?Id has type ?Tp :: typeAnnotation :- Id::id :& Tp::typeExpression;
# ?Id has kind ?K :: typeAnnotation :- Id::id :& K::typeKind;
# ?Id has kind ?K where ?C :: typeAnnotation :- K::typeKind :& C::typeConstraint;

# #(?N)##@#(tuple?Arg)# default is ?Exp :: typeAnnotation :- N::name :& ?Arg :* pattern :& ?Exp :: expression;
# ?Id default is ?Exp :: typeAnnotation :- Id::id :& Exp::expression;
# ?Id default := ?Exp :: typeAnnotation :- Id::id :& Exp::expression;

-- written in this weird way because type is normally an operator.   
# #(type)#::typeKind;
# #(type)# of #(type)# :: typeKind;
# #(type)# of #(tuple?Args)# :: typeKind :- Args :* typeKind;
# ?T where ?C :: typeKind :- T::typeKind :& C::typeConstraint;

# assert ?C :: typeAnnotation :- C::condition;

-- Type annotations may also be statements when they are declaring variables
# ?Id has type ?Tp :: statement :- Id::id :& Tp::typeExpression;

-- Type Expressions
# ?T where ?C :: typeExpression :- T::typeExpression :& C::typeConstraint;
# % identifier :: typeExpression;
# %% identifier :: typeExpression;
# (?T) :: typeExpression :- T::typeExpression;
# tuple?T :: typeExpression :- T:*typeExpression;
# ref ?T :: typeExpression :- T::typeExpression;
# hash of (?K,?V) :: typeExpression :- K::typeExpression :& V::typeExpression :& warning("hash type deprecated, use dictionary");
# ?N of ?T :: typeExpression :- #(N::identifier :| N::typeVar)# :& T::typeExpression;
# symbol :: typeExpression;
# #(?M)#.#(?P)# :: typeExpression :- M::id :& P::path ## {
  # #(?L)# . #(?R)# :: path :- L::path :& R::path;
  # ?I :: path :- I::id;
}

# ?A => ?R :: typeExpression :- A::typeExpression :& R::typeExpression;
# ?A <= ?P :: typeExpression :- A::typeExpression :& P::typeExpression;
# ?A <=> ?P :: typeExpression :- A::typeExpression :& P::typeExpression;

# { ?A } :: typeExpression :- A;*typeAnnotation;

# for all ?Tvs such that ?Tp :: typeExpression :- Tvs::typeVars :& Tp::typeExpression;
# exists ?Tvs such that ?Tp :: typeExpression :- Tvs::typeVars :& Tp::typeExpression;

# action#@#(tuple?Arg)# :: typeExpression :- Arg:*typeExpression;

# ?Id . ?Tp :: typeExpression :- Id::expression :& Tp::id;
 
# ?T :: typeExpression :- error("$T not a well formed type expression");
 
# #(?L,?R)# :: typeVars :- L::typeVars :& R::typeVars;
# ?Tp :: typeVars :- Tp::typeVar;
 
-- Type constraints
# ?L and ?R :: typeConstraint :- L :: typeConstraint :& R :: typeConstraint;
# ?N over ?T determines ?D :: typeConstraint :- N::id :& T::typeExpression :& D::typeExpression;
# ?N over ?T :: typeConstraint :- N::id :& T::typeExpression;
# (?C) :: typeConstraint :- C :: typeConstraint;
# ?T implements { ?A } :: typeConstraint :- T::typeExpression :& A;*typeAnnotation;
# ?T is tuple :: typeConstraint :- T::typeExpression;
# ?Tv instance of ?Tp :: typeConstraint :- Tv::typeVar :& Tp::typeExpression;
# ?Tv has kind ?K :: typeConstraint :- Tv::typeVar :& K :: typeKind;

# assert ?E :: statement :- E::condition;
# ignore ?E :: statement :- E::expression;

# {?A} :: statement :- A;* action;

-- Function definitions
# #(?N)##@#(tuple?Arg)# where ?Cnd is ?Exp :: statement :- 
    N::name :& ?Arg :* pattern :&
    ?Cnd :: condition :&
    ?Exp :: expression;
# #(?N)##@#(tuple?Arg)# default is ?Exp :: statement :-
    N::name :& ?Arg :* pattern :& ?Exp :: expression;
# #(?N)##@#(tuple?Arg)# is ?Exp :: statement :-
    N::name :& Arg :* pattern :& Exp :: expression;
# #(?N)#{?Arg} where ?Cnd is ?Exp :: statement :-
    N::name :& ?Arg ;* fieldPattern :&
    ?Cnd :: condition :&
    ?Exp :: expression;
# #(?N)#{?Arg} default is ?Exp :: statement :-
    N::name :& ?Arg ;* fieldPattern :& Exp :: expression;
# #(?N)#{?Arg} is ?Exp :: statement :-
    N::name :& Arg ;* fieldPattern :& Exp :: expression;

-- Variable definitions
# ?V is ?Exp :: statement :- V::pattern :& Exp::expression;
# var ?V is ?Exp :: statement :- V::pattern :& Exp::expression;
# var identifier := ?Exp :: statement :- Exp::expression;

# ?A is ?E :: statement :- error("does not look like a valid statement");

-- Procedure definition
# #(?N#@#(tuple?Arg)#)#{ ?A } :: statement :-
    N::name :& Arg:*identifier :& A:: action;
# ?N#@#(tuple?Arg)# where ?Cnd do ?A :: statement :-
    N :: name :& Arg:*pattern :& Cnd::condition :& A:: action;
# ?N#@#(tuple?Arg)# default do ?A :: statement :-
    N::name :& Arg:*pattern :& A:: action;
# ?N#@#(tuple?Arg)# do ?A :: statement :-
    N::name :& Arg:*pattern :& A:: action;

-- Pattern abstraction definition
# ?name#@#(tuple?Arg)# from ?A :: statement :-
    name::name :& Arg:*expression :& A::pattern;

-- Convenience rules
# number :: number;
# identifier :: identifier;

# identifier :: name;
# ?I :: name :- error("$I not permitted as regular identifier");

-- Actions
# {} :: action;
# { ?A } :: action :- ?A ;* action;
# #( ?S ; ?T )# :: action :- S:: action :& T:: action;
# #(?A ; )# :: action :- A:: action;
# nothing :: action;

# spawn ?A :: action :- A :: action;
# waitfor ?T :: action :- T :: expression;
# ?A // ?B :: action :- A::action :& B::action;

# ?A // ?B ==> { T is spawn{A}; B; waitfor T};

# ?Id has type ?Tp :: action :- Id::id :& Tp::typeExpression;
# ?Id is ?Exp :: action :- Id::pattern :& Exp::expression;
# var ?Ptn is ?Exp :: action :- Ptn::pattern :& Exp::expression;
# ?N := ?E :: action :- ?N :: lvalue :& ?E :: expression;

# var identifier := ?E :: action :- ?E :: expression;  

# if ?S then ?T else ?E :: action :-
    ?S :: condition :& ?T :: action :& ?E :: action;
# if ?S then ?T :: action :-
    ?S :: condition :& ?T :: action;

# assert ?E :: action :- E::condition;
# ignore ?E :: action :- E::expression;
# yield ?A :: action :- A::action;

# for ?C do ?B :: action :- C :: condition :& B :: action;
# while ?C do ?B :: action :- C :: condition :& B :: action;

# let{ ?B } in ?E  :: action :- B;*statement :& E:: action;
# ?A using ?S :: action :- A:: action :& S::expression;

# open ?E :: action :- E::expression;

-- This is complex because we cannot re-use an identifier. So we have to synthesize new ones.

# ?Exp using #(identifier?SS)# 's ?E ==> #*unwrapLet(#*defs(SS,E,Exp,())) ## {
  #defs(?S,?L 'n ?R,?A,?D) ==> defs(S,R, A./L->#$L, glue((#(#$L is S.L)#,D)));
  #defs(?S,?L,?A,?D) ==> (A./L->#$L, glue((#(#$L is S.L)#,D)));
  
  #glue( (?L,()) )==> L;
  #glue( (?L, ?R) ) ==> #(L ; R)#;
  
  #unwrapLet((?A, ())) ==>  A;
  #unwrapLet((?A, ?D)) ==> let { D } in A;
};

# ?Exp using ?SS 's ?E ==> #*unwrapLet(#$Id, #*defs(#$Id,E,Exp,())) ## {
  #defs(?S,?L 'n ?R,?A,?D) ==> defs(S,R, A./L->#$L, glue((#(#$L is S.L)#,D)));
  #defs(?S,?L,?A,?D) ==> (A./L->#$L, glue((#(#$L is S.L)#,D)));
  
  #glue( (?L,()) )==> L;
  #glue( (?L, ?R) ) ==> #(L ; R)#;
  
  #unwrapLet(?Id, (?A, ())) ==> let { Id is SS; A} in A;
  #unwrapLet(?Id, (?A, ?D)) ==> let { Id is SS; D } in A;
};

# case ?E in ?Cs :: action :- E::expression :& Cs::actionCases;

# { ?Cs } :: actionCases :- Cs ;* actionCase;

# ?Ptn do ?Act :: actionCase :- Ptn::pattern :& Act:: action;
# ?Ptn default do ?Act :: actionCase :- Ptn::pattern :& Act::action; 

-- lValues 
# identifier :: lvalue;
# ?Id.identifier :: lvalue :- Id::lvalue;
# #(?R)#. #(?A)#[?E] :: lvalue :- R.A::lvalue :& E::expression;
# ?Id.(?A) :: lvalue :- Id::identifier :& A::expression;
# #(?A)#[?S] :: lvalue :- A::lvalue :& S::expression;
# tuple?Args :: lvalue :- Args:*lvalue;

# #(?A)#[?E] :: expression :- A::expression :& E::expression;

# number :: expression;
# char :: expression;
# string :: expression;
# identifier :: expression;
# symbol :: expression;

# (?E) :: expression :- E::expression;

# ref ?E :: expression :- E::expression;
# ! ?E :: expression :- E::expression;

# ?E cast ?T :: expression :- E::expression :& T::typeExpression;
# ?E as ?T :: expression :- E::expression :& T::typeExpression;
# ?E has type ?T :: expression :- E::expression :& T::typeExpression;

-- @ expressions
# identifier@ #(?E)# :: expression :- E::expression;

-- Let expressions
# let {?S} in ?E :: expression :- ?S ;* statement :& ?E :: expression;
# ?E using ?S  :: expression :- S::expression :& E::expression;

-- case expression
# case ?E in { ?Cs } :: expression :- E::expression :& Cs;*caseExpRule;

# ?Ptn is ?Exp :: caseExpRule :- Ptn::pattern :& Exp::expression;
# ?Ptn default is ?Exp :: caseExpRule :- Ptn::pattern :& Exp::expression;

-- Lambdas
# function#@?Arg is ?Exp :: expression :- Arg :* pattern :& ?Exp :: expression;
# fn ?Arg => ?Exp :: expression :- Arg:*pattern :& Exp::expression;

# #(procedure#@?Arg)# { ?Act } :: expression :- Arg :* pattern :& Act:: action;
# #(?P#@?Arg)# { ?Act } :: expression :- Arg :* pattern :& Act:: action :& error("expecting `procedure', not `$P'");
 
# #(procedure#@?Arg)# do { ?Act } :: expression :-
    Arg :* pattern :& Act:: action;

# pattern#@?Arg matches ?Ptn :: expression :- Arg:*expression :& Ptn::pattern;
# pattern#@?Arg from ?Ptn :: expression :- Arg:*expression :& Ptn::pattern;
 
-- Conditional expression
# #( ?T ? ?Th | ?El)# :: expression :- T::condition :& Th::expression :& El::expression;

-- Hash expressions
# hash{} :: expression;
# hash{?A} :: expression :-  ?A ;* hashElement ## {
  # ?A -> ?V :: hashElement :- A::expression :& V::expression;
};

-- Record expressions
# identifier{} :: expression;
# identifier{?A} :: expression :- A;*attributeExpression;

# ?Ix=?V :: attributeExpression :- iX::id :& ?V :: expression;
# ?Ix:=?V :: attributeExpression :- iX::id :& ?V :: expression;
# type ?Id = ?T :: attributeExpression :- Id::id :& T::typeExpression; 

# ?X :: attributeExpression :- X :: statement;

-- List expressions

# #( ?S ; ?T )# :: listBody :- S::expression :& T::listBody;
# #( ?S ;.. ?T )# :: listBody :- S::expression :& T::expression;  
# #( ?S ;)#::listBody :- S::expression;
# ?E :: listBody :- E::expression;

# ?I of {?C} :: expression :- I::id :& C::listBody; 

# #(?E)#[?ix] :: expression :- E::expression :& ix::sliceExpression;

# #(?F)#:#(?C)# :: sliceExpression :- F::expression :& C::expression;
# #(?F)# : :: sliceExpression :- F::expression;
# ?E :: sliceExpression :- E::expression;

# ?E default ?D :: expression :- E::expression :& D::expression;

-- Record access expression

# ?L substitute ?E :: expression :- L::expression :& E::expression;
# ?L.?R :: expression :- L::expression :& R::expression;
-- # #(?L.#(?A)#:=?E)# ==> valof{ var #$"L" := L; #$"L".A := E; valis #$"L"};

-- Tuple expression
# tuple?T :: expression :- T:*expression;

-- Quote expression
# quote(?E) :: expression;
# unquote(?E) :: expression;

# symbol :: symbol;

-- conditions
# true :: condition;
# false :: condition;
# ?L and ?R :: condition :- L::condition :& R::condition;
# ?L or ?R :: condition :- L::condition :& R::condition;
# ?L otherwise ?R :: condition :- L::condition :& R::condition;
# not ?R :: condition :- R::condition;
# ?L implies ?R :: condition :- L::condition :& R::condition;
# ?L = ?R :: condition :- L::expression :& R::expression;  
# ?L != ?R :: condition :- L::expression :& R::expression;  
# ?L > ?R :: condition :- L::expression :& R::expression;  
# ?L < ?R :: condition :- L::expression :& R::expression;  
# ?L >= ?R :: condition :- L::expression :& R::expression;  
# ?L <= ?R :: condition :- L::expression :& R::expression;
# #(?L)#[?Ix] in ?R :: condition :- L::pattern :& Ix::expression :& R::expression;  
# ?L in ?R :: condition :- L::pattern :& R::expression;  
# ?E matches ?P :: condition :- P::pattern :& E::expression;  
# #( ?T ? ?Th | ?El)# :: condition :- T::condition :& Th::condition :& El::condition;
# (?C) :: condition :- C::condition;
# ?E :: condition :- E::expression;

-- move the [] term to safety to avoid premature macro-izing
# #(?L)#[?Ix] in ?R ==> [L,Ix] in R   

-- Simple patterns
# number :: pattern;
# char :: pattern;
# regexp :: pattern;
# string :: pattern;
# identifier :: pattern;
# true :: pattern;
# false :: pattern;

# regexp::regexp;

# ?V matching ?P :: pattern :- V::pattern :& P::pattern;

# ?P where ?C :: pattern :- P :: pattern :& C::condition;

# ?P cast ?Tp :: pattern :- Tp::typeExpression :& P::pattern;

-- hash pattern
# hash{ ?H } :: pattern :- H;*hashPattern ## {
  # ?K -> ?P :: hashPattern :- K::pattern :& P::pattern;
};

 -- List patterns

# ?I of {} :: pattern :- I::id;
# ?I of {?C} :: pattern :- I::id :& C::sequencePtnBody; 

-- sequence patterns
# #( ?S ; ?T )# :: sequencePtnBody :- S::pattern :& T::sequencePtnBody;
# #( ?S ;.. ?T )# :: sequencePtnBody :- S::pattern :& T::sequencePtnBody;  
# #( ?S ..; ?T )# :: sequencePtnBody :- S::pattern :& T::pattern;
# #( ?S ;)#::sequencePtnBody :- S::pattern;
# ?E :: sequencePtnBody :- E::pattern;
-- Special hack to detect an anonymous record pattern
# { identifier = ?Ptn; ?Rest} :: pattern :- Ptn::pattern :& Rest;*fieldPattern;
# { identifier = ?Ptn} :: pattern :- Ptn::pattern;
# { (identifier) = ?Ptn; ?Rest} :: pattern :- Ptn::pattern :& Rest;*fieldPattern;
# { (identifier) = ?Ptn} :: pattern :- Ptn::pattern;
 
# identifier{?A} :: pattern :- A;*fieldPattern;
# identifier{} :: pattern;

# ?I = ?P :: fieldPattern :- I::id :& P::pattern;
# ?I := ?P :: fieldPattern :- I::id :& P::pattern;
# type ?I = ?P :: fieldPattern :- I::id :& P::typeExpression; 

# identifier#@?A::pattern :- A:*pattern;

# tuple?T :: pattern :- T:*pattern;

-- Quote patterb
# quote(?E) :: pattern;
# unquote(?E) :: pattern;

-- @ patterns
# identifier @ identifier :: pattern;

# #(?A)# #@ ?E :: pattern :- A::expression :& E:*pattern;

# {} :: expression;
-- Special hack to detect an anonymous record
# { ?Content } :: expression :- Content ;* attributeExpression :| Content ;* statement;

# ?L 'n ?R :: names :- L::names :& R::names;
# ?I :: names :- I::id;

-- Query notation
# unique ?C of ?E where ?Q :: expression :- E::expression :& Q::queryConstraint;
# unique ?E where ?Q :: expression :- E::expression :& Q::queryConstraint;
# all ?E where ?Q :: expression :- E::expression :& Q::queryConstraint;
# any of ?E where ?Q default ?D :: expression :- E::expression :& Q::queryConstraint :& D::expression;
# any of ?E where ?Q :: expression :- E::expression :& Q::queryConstraint;
# anyof ?E where ?Q default ?D :: expression :- E::expression :& Q::queryConstraint :& D::expression;
# anyof ?E where ?Q :: expression :- E::expression :& Q::queryConstraint;
# ?C of ?E where ?Q :: expression :- C::expression :& E::expression :& Q::queryConstraint;

# reduction ?F of { ?Q } :: expression :- F::expression :& Q::queryForm;
# ?I of {?Q} :: expression :- I::id :& Q::queryForm;

# ?Q order descending by ?E :: queryConstraint :- Q::condition :& E::sortClause;
# ?Q order by ?E :: queryConstraint :- Q::condition :& E::sortClause;
# ?Q descending by ?E :: queryConstraint :- Q::condition :& E::sortClause;
# ?Q :: queryConstraint :- Q::condition;
# ?E using ?C :: sortClause :- E::expression :& C::expression;
# ?E :: sortClause :- E::expression;

# ?Q order descending by ?E :: expression :- Q::queryForm :& E::sortClause;
# ?Q order by ?E :: expression :- Q::queryForm :& E::sortClause;

# unique ?C of ?E where ?Q :: queryForm :- E::expression :& Q::queryConstraint;
# unique ?E where ?Q :: queryForm :- E::expression :& Q::queryConstraint;
# all ?E where ?Q :: queryForm :- E::expression :& Q::queryConstraint;
# anyof ?E where ?Q default ?D :: queryForm :- E::expression :& Q::queryConstraint :& D::expression;
# anyof ?E where ?Q :: queryForm :- E::expression :& Q::queryConstraint;
# any of ?E where ?Q default ?D :: queryForm :- E::expression :& Q::queryConstraint :& D::expression;
# any of ?E where ?Q :: queryForm :- E::expression :& Q::queryConstraint;
# ?C of ?E where ?Q :: queryForm :- C::expression :& E::expression :& Q::queryConstraint;

# ?Q order descending by ?E :: queryForm :- Q::queryForm :& E::sortClause;
# ?Q order by ?E :: queryForm :- Q::queryForm :& E::sortClause;

# ?E using ?C :: sortClause :- E::expression :& C::expression;
# ?E :: sortClause :- E::expression;  

-- Thread expressions
# spawn ?A :: expression :- A::action;
# waitfor ?T :: expression :- T::expression;

# sync(?E){ ?A } :: action :- E::expression :& A;*syncAction ## {
# when ?C do ?A :: syncAction :- C::condition :& A::action;
# ?A :: syncAction :- A::action;
};

-- Exception handling
# try ?A catch { ?E } :: action :- A::action :& E::action;
# try ?A catch { } :: action :- A::action ;
# try ?E catch ?Ex :: expression :- E::expression :& Ex::expression;

# try ?A catch { ?Ex } ==> try ?A on abort { _ do Ex};
# try ?A catch { } ==> try ?A on abort { _ do nothing};

# try ?A on abort  ?Cs  :: action :- A::action :& Cs::actionCases;

# raise ?C : ?E :: action :- C::expression :& E::expression;
# raise ?E :: action :- E::expression;
# raise ?C : ?E :: expression :- C::expression :& E::expression;
# raise ?E :: expression :- E::expression;

# perform ?A on abort ?Cs :: action :- A::expression :& Cs::actionCases;
# perform ?A :: action :- A::expression;

-- Action expressions
# valof{?A} ::expression :- A ;* action;

-- Computation expressions
# identifier computation {?A} :: expression :- A ;* action;
# valof ?E on abort ?F :: expression :- E :: expression :& F::expression;
# valof ?E :: expression :- E :: expression;

# valis ?E :: action :- E::expression;

-- Default case for application expression
# ?F#@?A :: expression :- ?F :: expression /*:& :! F::keyword */ :& ?A :* expression;

-- Default handling of actions
# ?P#@?A:: action :- P::expression :& A:*expression;