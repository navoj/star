actorgen is package{
  
  #genActor(?XX) ==> actor{
    #*genRules(?XX);
    var foo := 10;
  };
  
  #genRules(#(?L;?R)#) ==> #(genRules(L);genRules(R))#;
  #genRules(KK(?E,?C,?A)) ==> on E on C do A;
  
  def gen is genActor(#(KK(A,C,logMsg(info,"Got $A on C"));KK(B,D,logMsg(info,"hello")))#);
  
  prc main() do {
    notify gen with 1 on C;
    notify gen with 2 on D;
  }
}
  