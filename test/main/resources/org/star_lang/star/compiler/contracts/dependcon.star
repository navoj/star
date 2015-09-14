dependcon is package{
  -- test simple contract with dependency
  
  contract Iter over %s determines %e is {
    iter has type for all %a such that (%s,(%e,%a)=>%a,%a)=>%a;
  }
  
  implementation Iter over cons of %e determines %e is {
    fun iter(nil,_,S) is S
     |  iter(cons(H,T),F,S) is iter(T,F,F(H,S))
  }
}