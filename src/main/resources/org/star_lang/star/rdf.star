/**
 *  Implement the N3/RDF functionality for SR programs. 
 *
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
private import arithmetic;
private import folding;
private import arrays;
private import sequences;
private import iterable;
private import strings;
private import cons;

/*
   Supports triples and quads -- in the form of named graph structures.
 */
 
 #right("!",500);
 #right("$",450);
 #prefix(":",100);
 
 -- Validation rules for N3 graph expressions
 # graph{} :: expression;
 # graph{?G} :: expression :- G;*triple ## {
 
 # ?S ! ?V :: triple :- S::nounPhrase :& V::verbPhrase;
 # ?T :: triple :- error("$T is not a valid triple");
 
 # ?P $ ?O :: verbPhrase :- P::verb :& O::nounPhrase;
 # [ ?VP ] :: verbPhrase :- VP::verbPhrases;
 # ?VP :: verbPhrase :- error("$VP must have at least one predicate and one object");
 
 # ?A,?B :: verbPhrases :- A::verbPhrase :& B::verbPhrases;
 # ?A :: verbPhrases :- A::verbPhrase;
 
 # [?V] :: verb :- V::verbs;
 # ?V :: verb :- V::concept;
 
 # #(?V1,?Vr)# :: verbs :- V1::concept :& Vr::verbs;
 # ?V :: verbs :- V::concept;

 # [ ?NP ] :: nounPhrase :- NP :: nounPhrases;
 # string : string :: nounPhrase;
 # string :: nounPhrase;
 # ?C :: nounPhrase :- C::concept;
 
 # #(?NP1,?NPr)# :: nounPhrases :- NP1::nounPhrase :& NPr::nounPhrases;
 # ?NP :: nounPhrases :- NP::nounPhrase;
 
 -- # identifier @ identifier : identifier :: concept;
 -- # identifier @ identifier :: concept;
 # ( ?P $ ?O ) :: concept :- P::verb :& O::nounPhrase;
 # identifier : identifier :: concept;
 # : identifier :: concept;
 # ?C :: concept :- error("$C is not a recognized form of concept");
 };
 -- Implementation 
 
 -- Standard types to support N3 notation
 type n3Graph is alias of list of n3Triple;
 
 type n3Triple is n3Triple(n3Concept,n3Concept,n3Concept);
 
 type n3Concept is n3C(string,string) or n3S(string,string);
 
 implementation pPrint over n3Concept is {
   fun ppDisp(n3C(G,C)) is ppSequence(0,cons of [ppStr(G),ppStr(":"),ppStr(C)])
    |  ppDisp(n3S(_,S)) is ppStr(display(S));
 }
 
 implementation pPrint over n3Triple is {
 	fun ppDisp(Tr) is displayTriple(Tr)
 } using {
 	fun displayTriple(n3Triple(Subj,Pred,Obj)) is ppSequence(0,cons of [ppDisp(Subj), ppSpace, ppDisp(Pred), ppSpace, ppDisp(Obj), ppNl])
 }
              
 -- Macros to convert graph expressions into regular Star structures.
 # graph{} ==> list of [] has type n3Graph;
 # graph{?Graph} ==> list of [triples(Graph) ] ## {

  #fun triples(A) is wrapComma(mapSemi(triple,A));     

  fun triple(SoFar,<| ?Sub ! ?VP |>) is SoFar++tripleJoin(trNounPhrase(list of [],Sub),trVerbPhrase(list of [],VP));
 
  fun trVerbPhrase(SoFar,<| [ ?VPs ] |>) is SoFar++mapComma(trVerbPhrase,VPs)
   |  trVerbPhrase(SoFar,<| ?V $ ?O |>) is SoFar++pairJoin(trVerb(list of [],V),trNounPhrase(list of [],O));
  
  fun trVerb(SoFar,<| [?Vs] |>) is SoFar++mapComma(trVerb,Vs)
   |  trVerb(SoFar, C) is list of [SoFar..,trConcept(C)];
  
  fun trNounPhrase(SoFar,<| [ ?NPs ] |>) is SoFar++mapComma(trNounPhrase,NPs)
   |  trNounPhrase(SoFar, N ) is list of [SoFar..,trConcept(N) ];
      
  fun trConcept(<| #(string?S)# : #(string?Lng)# |>) is <| n3S(?S,?Lng) |>
   |  trConcept(<| #(identifier?G)# : #(identifier ? C)# |>) is <| n3C(?nameString(G), ?nameString(C)) |>
   |  trConcept(<| : #(identifier?C)# |>) is <| n3C("", ?nameString(C)) |>
   |  trConcept(<| string ?S |>) is <| n3S(?S,"") |>;
  
  fun nameString(nameAst(Lc,N)) is stringAst(Lc,N);
  
  fun unwrapSemi(<| ?L ; ?R |>,Lst) is unwrapSemi(R,unwrapSemi(L,Lst))
   |  unwrapSemi(El,Lst) is list of [Lst..,El];
 
  fun showQ(A) is valof{
    def R is unwrapSemi(A,list of []);
    logMsg(info,"","Unwrap of $(display_quoted(A)) is");
    for E in R do
      logMsg(info,"",display_quoted(E));
    valis R
  };

  fun wrapComma(list of [El]) is <| ?El |>
   |  wrapComma(list of [El,..More]) is <| ?El , ?wrapComma(More) |>;

  fun unwrapComma(<| ?L , ?R |>,Lst) is unwrapComma(R,unwrapComma(L,Lst))
   |  unwrapComma(El,Lst) is list of [Lst..,El];   
  
  fun mapComma(F,A) is valof{
    def R is leftFold(F,list of [],unwrapComma(A,list of []));
    logMsg(info,"","$R");
    valis R
  }
  
  fun mapSemi(F,A) is valof{
    def R is leftFold(F,list of [],unwrapSemi(A,list of []));
    logMsg(info,"","$R");
    valis R
  }
  
  fun pairJoin(L1,L2) is list of { (E1,E2) where E1 in L1 and E2 in L2 };
  
  fun tripleJoin(L1,L2) is list of { <| n3Triple(?S,?V,?O) |> where S in L1 and (V,O) in L2 };
};