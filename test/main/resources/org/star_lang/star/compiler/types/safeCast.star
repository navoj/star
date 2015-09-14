safeCast is package{
  -- test safe casting
  
  type group is group{
    elem has kind type where equality over elem;
    
    zero has type elem;
    inv has type (elem)=>elem;
    op has type (elem,elem)=>elem;
    
    eq has type (elem,elem)=>boolean;
    
    el has type for all %t such that (%t)=>elem;
  };
  
  def G is group{
    type integer counts as elem;
    
    def zero is 0;
    fun inv(X) is -X;
    fun op(X,Y) is X+Y;
    
    fun el(X) is X cast elem;
    
    fun eq(X,Y) is X=Y;
  }
  
  prc main() do {
    def Z is G.op(3 cast G.elem,2 cast G.elem);
    
    -- assert G.eq(Z,5 cast G.elem)
    
    assert Z=5 cast G.elem
  }
}