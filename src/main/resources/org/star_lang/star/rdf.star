/**
 *  Implement the N3/RDF functionality for SR programs. 
 * Copyright (C) 2013 Starview Inc
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
import base;
import folding;
import arrays;
import sequences;
import iterable;
import strings;
import cons;

/*
   Supports triples and quads -- in the form of named graph structures.
 */
 
 #right((!),500);
 #right(($),450);
 
 -- Validation rules for N3 graph expressions
 # graph{} :: expression;
 # graph{?G} :: expression :- G::triples ## {
 
 # #(?A;?B)# :: triples :- A::triples :& B::triples;
 # #(?A;)# :: triples :- A::triples;
 # ?T :: triples :- T::triple;
 
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
 # identifier :: concept;
 # ?C :: concept :- error("$C is not a recognized form of concept");
 };
 -- Implementation 
 
 -- Standard types to support N3 notation
 type n3Graph is alias of list of n3Triple;
 
 type n3Triple is n3Triple(n3Concept,n3Concept,n3Concept);
 
 type n3Concept is n3C(string,string) or n3S(string,string);
 
 implementation pPrint over n3Concept is {
   ppDisp(n3C("",C)) is ppStr(C)
   ppDisp(n3C(G,C)) is ppSequence(0,cons of {ppStr(G);ppStr(":");ppStr(C)})
   ppDisp(n3S(_,S)) is ppStr(display(S));
 }
 
 implementation pPrint over n3Triple is {
 	ppDisp(Tr) is displayTriple(Tr)
 } using {
 	displayTriple(n3Triple(Subj,Pred,Obj)) is ppSequence(0,cons of {ppDisp(Subj); ppSpace; ppDisp(Pred); ppSpace; ppDisp(Obj); ppNl})
 }
              
 -- Macros to convert graph expressions into regular Star structures.
 # graph{} ==> list of [] has type n3Graph;
 # graph{?Graph} ==> list of [triples(Graph) ] ## {

  #triples(A) is wrapComma(mapSemi(triple,A));     

  triple(SoFar,<| ?Sub ! ?VP |>) is SoFar++tripleJoin(trNounPhrase(list of [],Sub),trVerbPhrase(list of [],VP));
 
  trVerbPhrase(SoFar,<| [ ?VPs ] |>) is SoFar++mapComma(trVerbPhrase,VPs); 
  trVerbPhrase(SoFar,<| ?V $ ?O |>) is SoFar++pairJoin(trVerb(list of [],V),trNounPhrase(list of [],O));
  
  trVerb(SoFar,<| [?Vs] |>) is SoFar++mapComma(trVerb,Vs);
  trVerb(SoFar, C) is list of [SoFar..,trConcept(C)];
  
  trNounPhrase(SoFar,<| [ ?NPs ] |>) is SoFar++mapComma(trNounPhrase,NPs);
  trNounPhrase(SoFar, N ) is list of {SoFar..;trConcept(N) };
      
  trConcept(<| #(string?S)# : #(string?Lng)# |>) is <| n3S(?S,?Lng) |>; 
  trConcept(<| #(identifier?G)# : #(identifier ? C)# |>) is <| n3C(?nameString(G), ?nameString(C)) |>;
  trConcept(<| identifier?C |>) is <| n3C("", ?nameString(C)) |>;
  trConcept(<| string ?S |>) is <| n3S(?S,"") |>;
  
  nameString(nameAst(Lc,N)) is stringAst(Lc,N);
  
  unwrapSemi(<| ?L ; ?R |>,Lst) is unwrapSemi(R,unwrapSemi(L,Lst));
  unwrapSemi(El,Lst) is list of [Lst..,El];
 
  unwrap(A) is valof{
    var R is unwrapSemi(A,list of []);
    logMsg(info,"","Unwrap of $(display_quoted(A)) is");
    for E in R do
      logMsg(info,"",display_quoted(E));
    valis R
  };

  wrapComma(list of [El]) is <| ?El |>;
  wrapComma(list of [El,..More]) is <| ?El , ?wrapComma(More) |>;

  unwrapComma(<| ?L , ?R |>,Lst) is unwrapComma(R,unwrapComma(L,Lst));
  unwrapComma(El,Lst) is list of [Lst..,El];   
  
  mapComma(F,A) is valof{
    var R is leftFold(F,list of [],unwrapComma(A,list of []));
    logMsg(info,"","$R");
    valis R
  }
  
  mapSemi(F,A) is valof{
    var R is leftFold(F,list of [],unwrapSemi(A,list of []));
    logMsg(info,"","$R");
    valis R
  }
  
  

  pairJoin(L1,L2) is list of { (E1,E2) where E1 in L1 and E2 in L2 };
  
  tripleJoin(L1,L2) is list of { <| n3Triple(?S,?V,?O) |> where S in L1 and (V,O) in L2 };
};