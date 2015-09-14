complexMacroLets is package{

 -- test some macro lets
 
  #iden(?I) ==> I;
  
  -- Macro that processes lists
  
  # processList(?List,?App,?Deflt,?Init) ==> procList(List,Init) ## { 
    # procList((),?SoFar) ==> #*Deflt(SoFar);
    # procList((?L,?R),?SoFar) ==> procList(R,#*procList(L,SoFar));
    # procList(?X,?SoFar) ==> #*App(X,SoFar);
  };
  
  -- Some example processors
  #glue(?List) ==> list of [ processList(List,procl,iden,()) ] ## {
    #procl(?Elt,()) ==> Elt ;
    #procl(?Elt, ?SoFar) ==> #(Elt,SoFar)#;
  }
  
  assert glue(("A",("B",("C",())))) = glue(((((),"A"),"B"),"C"));
}
  