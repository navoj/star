miniRdf is package{
  #right((!),500);
  #right(($),450);
  
  type n3Graph is n3Graph(list of n3Triple);
  
  type n3Triple is n3Triple(n3Concept,n3Concept,n3Concept);
  
  type n3Concept is n3C(string);
  
  #graph{?B} :: expression :- B;*triple;
  
  #?S ! ?P $ ?O :: triple;
  
  #graph{?B} ==> n3Graph(collectSemis(B)) ## {
    # ?S ! ?P $ ?O ==> n3Triple(n3Concept(S),n3Concept(P),n3Concept(O));
    
    #n3Concept(identifier?I) ==> n3C($$I)
    
    #collectSemis(?Sq) ==> convert(Sq) ## {  
      #fun convert(S) is convertHeads(S,<| _nil() |>)
  
      fun convertHeads(<| ?F ; ?T |>, Tl) is <| _cons(?F, ?convertHeads(T,Tl)) |>
       |  convertHeads( F, Tl) is <| _cons(?F,?Tl) |>;
    }
  }
 
  prc main() do {
    def G is graph{
      john ! parent $ sam;
      sam ! parent $ joe
    };
    
    logMsg(info,display(G));
  }
}