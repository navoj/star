append is package{
  -- test simple type inference
  
  append has type (cons of %t, cons of %t) => cons of %t;
  fun append(nil,X) is X
   |  append(cons(H,T),X) is cons(H,appR(T,X))
  
  fun appR(nil,X) is X
   |  appR(cons(H,T),X) is cons(H,append(T,X))
  
  prc main() do {
    assert append(cons of [1,2,3],cons of [4,5,6]) = cons of [1,2,3,4,5,6]
    
    assert append(cons of ["a","b"], cons of ["c"]) = cons of ["a", "b", "c"]
  }
}