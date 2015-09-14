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
   
    #fun concepts(A) is  mapSemi(trConcept,A);
   
    fun unwrap(<| ?L ; ?R |>,Lst) is unwrap(R,unwrap(L,Lst))
     |  unwrap(El,Lst) is list of [Lst..,El]
   
    fun wrapRv(_pair(El,_empty())) is <| ?El |>
     |  wrapRv(_pair(El,More)) is <| ?El , ?wrapRv(More) |>;
   
    fun mapSemi(F,A) is wrapRv(leftFold(F,_nil(),unwrap(A,_nil())));

    fun trConcept(SoFar,<| #(string?S)# : #(string?Lng)# |>) is list of [SoFar..,<| n3S(?S,?Lng) |>] 
     |  trConcept(SoFar,<| #(identifier?G)# : #(identifier ? C)# |>) is list of [SoFar..,<| n3C(?nameString(G), ?nameString(C)) |>]
     |  trConcept(SoFar,<| identifier?C |>) is list of [SoFar..,<| n3C("", ?nameString(C)) |>]
     |  trConcept(SoFar,<| string ?S |>) is list of [SoFar..,<| n3S(?S,"") |>]
    
    fun nameString(nameAst(Lc,N)) is stringAst(Lc,N)
  }
  
  prc main() do {
    def G is graph{ foo; gr:concept; "a string"; "une string française":"fr" }
   
    logMsg(info,"concepts are $G");
    
    assert G=list of [n3C("", "foo"),
                      n3C("gr", "concept"),
                      n3S("a string", ""),
                      n3S("une string française", "fr")];
  }
}
      
 
 