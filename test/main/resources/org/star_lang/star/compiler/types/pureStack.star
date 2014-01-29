pureStack is package{
  type stack of t is stack{
    push has type (t)=>stack of t;
    pop has type ()=>(t,stack of t)
  }
  
  ss(L) is stack{
    push(X) is ss(list of {X;..L});
    pop() where L matches list of {X;..LL} is (X,ss(LL))
  }
  
  emptyStack is ss(list of {})
  
  main() do {
    assert emptyStack.push(1).pop() matches (1,_)
  }
}
 