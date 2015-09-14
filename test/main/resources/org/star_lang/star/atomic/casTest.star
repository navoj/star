casTest is package{
  private import atomics;
  
  -- test the test_and_swap
  
  prc main() do {
    def I is 34;
    def A is atomic(I);
    
    assert __atomic_test_n_set(A,I,56);
    
    assert _get(A)=56;
    
    assert not __atomic_test_n_set(A,I,23);
    
    assert _get(A)=56;
    
    -- test integer atomics
    
    II has type atomic_int;
    def II is _atomic(34);
    
    def IR is 56_;
    
    assert __atomic_int_test_n_set(II,34_,IR);
    
    assert _get(II)=56;
    
    assert not __atomic_int_test_n_set(II,34_,23_);
    
    assert _get(II)=56;
  }
}