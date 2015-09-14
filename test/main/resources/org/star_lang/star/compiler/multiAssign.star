multiAssign is package{

  fun t2(A,B) is (A,B);
  
  prc main() do {    
    -- test multiple assignment
    var A := "fred";
    var B := 2;
    
    assert A="fred";
    assert B=2;
    
    (A,B) := ("peter",B)
    
    assert A="peter" and B=2;
    
    (A,B) := t2("john",3);
    
    assert A="john" and B=3;
  }
}