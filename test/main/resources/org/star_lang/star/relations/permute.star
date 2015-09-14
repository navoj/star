permute is package{
  fun permute(list of []) is list of []
   |  permute(list of [E]) is list of [list of [E]]
   |  permute(list of [E,..Mr]) is foldIn(E,permute(Mr))
  
  fun foldIn(E,list of []) is list of []
   |  foldIn(E,list of [Tpl,..Tpls]) is shuffle(E,Tpl,list of []) ++ foldIn(E,Tpls)
    
  fun shuffle(E,list of [],Pre) is list of [list of [Pre..,E]]
   |  shuffle(E,list of [H,..T],Pre) is list of [Pre++list of [E,H,..T],.. shuffle(E,T,list of [H,..Pre])]
  
  fun NofM(0,_) is list of []
   |  NofM(1,L) is list of {all (list of [E]) where E in L}
   |  NofM(K,list of [E1,..Mr]) where K>0 is glue(E1,NofM(K-1,Mr))++NofM(K,Mr)
   |  NofM(K,list of []) is list of []
  
  private
  fun glue(E,L) is map((X)=>list of [E,..X],L)
  
  multicat has type (list of list of %t)=>list of %t
  fun multicat(L) is rightFold((++),list of [],L)
}