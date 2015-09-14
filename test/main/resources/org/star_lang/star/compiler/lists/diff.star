diff is package{
  contract hasDiff over %t is {
    diff has type (%t,%t) => ((%t)=>%t);
  }
  
  fun id(X) is X;
  
  fun K(X) is (_) => X;
  
 implementation hasDiff over integer is {
    diff = intDiff
  } using {
    fun intDiff(X,X) is id
     |  intDiff(X1,X2) default is K(X2)
  };

  implementation hasDiff over (list of %t where hasDiff over %t and equality over %t) is{
    diff=listDiff
  } using {
    fun listDiff(X,X) is id 
     |  listDiff(list of [X,..L1],list of [X,..L2]) is let{
          def D is listDiff(L1,L2);
          fun differ(list of [XX,..LL]) is list of [XX,..D(LL)];
        } in differ
     |  listDiff(list of [X1,..L1],list of [X2,..L2]) where X1!=X2 is let{
            def H is diff(X1,X2)
            def D is listDiff(L1,L2)
            fun differ(list of [A,..B]) is list of [H(A),..D(B)]
          } in differ
  }
  
  prc main() do {
    def D1 is diff(list of [1,3,3],list of [1,2,3]);
    
    logMsg(info,"d is $D1");
    logMsg(info,"apply to list of [1,3,3] is $(D1(list of [1,3,3]))");
    assert D1(list of [1,3,3]) = list of [1,2,3];
  }
}