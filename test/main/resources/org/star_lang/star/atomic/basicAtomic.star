basicAtomic is package{

  private import atomics;
  
  prc main() do {
    def A is atomic(34);
    
    assert _get(A)=34;
    
    _assign(A,45);
    
    assert _get(A)=45;
    
    I has type atomic_int;
    def I is _atomic(23);
    
    assert _get(I)=23;
    
    _assign(I,45);
    
    assert _get(I)=45;
  }
}