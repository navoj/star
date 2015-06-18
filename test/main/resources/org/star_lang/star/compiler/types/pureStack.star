pureStack is package{
  type stack of t is stack{
    push has type (t)=>stack of t;
    pop has type ()=>(t,stack of t)
  }
  
  fun ss(L) is stack{
    fun push(X) is ss(list of {X;..L});
    fun pop() where L matches list of {X;..LL} is (X,ss(LL))
  }
  
  def emptyStack is ss(list of {})
  
  prc main() do {
    assert emptyStack.push(1).pop() matches (1,_)
  }
}
 