dotslash is package{
  #probe(?X./wrapRv(?A)) ==> X./A;
  
 -- #probeFree(?Res,?Specs./respond(?Res),?Chnl) ==> (Res,Chnl);  
  
  prc main() do
  {
    assert probe(list of [1,wrapRv(2)]) = list of [1,2];
    
  --  assert probeFree("Jim", KK(JJ of [ respond("Jim")]) ,nonInteger) = ("Jim",nonInteger);
  }  
}
