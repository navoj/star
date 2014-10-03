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
conceptMacros is package{
  -- some of the macros from the rdf package 
  import folding;
  #right("!",500);
  #right("$",450);
 
 -- Validation rules for N3 graph expressions
  # graph{} :: expression;
  # graph{?G} :: expression :- G;*concept ## { 
    -- # identifier @ identifier : identifier :: concept;
    -- # identifier @ identifier :: concept;
    # ( ?P $ ?O ) :: concept :- P::verb :& O::nounPhrase;
    # identifier : identifier :: concept;
    # identifier :: concept;
    # string :: concept;
    # string : string :: concept;
    # ?C :: concept :- error("$C is not a recognized form of concept");
  };
 
  type n3Graph is alias of list of fact;
 
  type fact is triple(n3Concept,n3Concept,n3Concept);
 
  type n3Concept is n3C(string,string)
                 or n3S(string,string);
                
  # graph{} ==> list of [] has type n3Graph;
  # graph{?Graph} ==> list of [ concepts(Graph) ] ## {
   
    #concepts(A) is  mapSemi(trConcept,A);
   
    unwrap(<| ?L ; ?R |>,Lst) is unwrap(R,unwrap(L,Lst));
    unwrap(El,Lst) is list of {Lst..;El};
   
    wrapRv(_pair(El,_empty())) is <| ?El |>;
    wrapRv(_pair(El,More)) is <| ?El , ?wrapRv(More) |>;
   
    mapSemi(F,A) is wrapRv(leftFold(F,_nil(),unwrap(A,_nil())));

    trConcept(SoFar,<| #(string?S)# : #(string?Lng)# |>) is list of {SoFar..;<| n3S(?S,?Lng) |>}; 
    trConcept(SoFar,<| #(identifier?G)# : #(identifier ? C)# |>) is list of {SoFar..;<| n3C(?nameString(G), ?nameString(C)) |>};
    trConcept(SoFar,<| identifier?C |>) is list of {SoFar..;<| n3C("", ?nameString(C)) |>};
    trConcept(SoFar,<| string ?S |>) is list of {SoFar..;<| n3S(?S,"") |>};
    
    nameString(nameAst(Lc,N)) is stringAst(Lc,N);
  }
  
  main() do {
    G is graph{ foo; gr:concept; "a string"; "une string française":"fr" }
   
    logMsg(info,"concepts are $G");
    
    assert G=list of [n3C("", "foo"),
                      n3C("gr", "concept"),
                      n3S("a string", ""),
                      n3S("une string française", "fr")];
  }
}
      
 
 