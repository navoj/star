trees is package{
  -- The trees package tests out positional constructors and general recursion
 
  type tree of t is 
      nul
   or labeled(tree of t, t, tree of t);
   
  type weekday is monday or tuesday or wednesday or thursday or friday or saturday or sunday;
  
  leaves has type (tree of %t) => list of %t;
  fun leaves(nul) is list of []
   |  leaves(labeled(L,Lb,R)) is leaves(L)++list of [Lb,..leaves(R)]
  
  insert has type (tree of %t, %t ) => tree of %t where comparable over %t
  fun insert(nul,T) is labeled(nul,T,nul)
   |  insert(labeled(L,B,R),T) where B>T is labeled(insert(L,T),B,R)
   |  insert(labeled(L,B,R),T) where B<T is labeled(L,B,insert(R,T))
  
  rotate has type (tree of %t) => tree of %t
  fun rotate(nul) is nul
  
  fun locate(nul,_) is false
   |  locate(labeled(_,Lb,_),Lb) is true
   |  locate(labeled(L,Lb,_),LL) where LL<Lb is locate(L,LL)
   |  locate(labeled(_,Lb,R),LL) where LL>Lb is locate(R,LL)
  
  find has type (list of ((%s,%t)),%s ) =>option of %t where equality over %s
  fun find(_,_) default is none
   |  find(list of [(K,V),.._],Ky) where K=Ky is some(V)
   |  find(list of [_,..R],Ky) is find(R,Ky)
  
  prc main() do
  {
    def T is insert(insert(insert(insert(nul,"alpha"),"gamma"),"beta"),"delta");
    logMsg(info,"T is $T");
    
    assert locate(T,"alpha");
    assert locate(T,"delta");
    assert not locate(T,"eta");
    
    assert find(list of [("alpha",1),("beta",2)],"beta") = some(2)
    assert find(list of [("alpha",1),("beta",2)],"gamma") = none
  };
}