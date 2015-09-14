reduceC is package{
  contract reduce over %t determines %a is {
    reducer has type for all b such that ((%a,b)=>b) => ((%t, b)=>b);
    reducel has type for all b such that ((b,%a)=>b) => ((b, %t)=>b);
  }
 
  implementation reduce over cons of %t determines %t is {
    fun reducer(f) is (x,z) => foldr(f,z,x);
    fun reducel(f) is (x,z) => foldl(f,x,z);
  }
  
  fun foldr(f,z,nil) is z
   |  foldr(f,z,cons(x,xs)) is f(x,foldr(f,z,xs))
  
  fun foldl(f,z,nil) is z
   |  foldl(f,z,cons(x,xs)) is foldl(f,f(z,x),xs)
}