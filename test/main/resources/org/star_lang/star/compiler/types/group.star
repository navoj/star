group is package{
  type group is group{
    elem has kind type where pPrint over elem;
    
    zero has type elem;
    inv has type (elem)=>elem;
    op has type (elem,elem)=>elem;
    
    el has type for all %t such that (%t)=>elem;
  };
  
  fun modGroup(K) is group{
    type integer counts as elem;
    def zero is 0;
    
    fun inv(X) is K-X;
    
    fun op(X,Y) is (X+Y)%K;
    
    fun el(X) is X cast elem;
    
    def pPrint\#elem is pPrint\#integer;
  };
  
  double has type (group)=>group;
  fun double(G) is group{
    type G.elem counts as elem;
    
    def zero is G.zero;
    
    fun inv(X) is G.inv(X);
    
    fun op(X,Y) is G.op(G.op(X,X),G.op(Y,Y));
    
    fun el(X) is G.el(X);
    
    def pPrint\#elem is G.pPrint\#elem
  }
  
  fun invGroup(G) is group{
    type G.elem counts as elem;
    
    def zero is G.zero;
    
    def inv is G.inv;
    
    fun op(X,Y) is G.inv(G.op(G.inv(X),G.inv(Y)));
    
    def el is G.el
    
    def pPrint\#elem is G.pPrint\#elem
  }
  
  prc main() do {
    def K is modGroup(7);
    logMsg(info,"K.zero=#(__display(K.zero))");
    logMsg(info,"3+5=#(__display(K.op(K.el(3),K.el(5))))");
    
    def D is double(K);
    logMsg(info,"D.zero = #(__display(D.zero))");
    logMsg(info,"3+5=#(__display(D.op(D.el(3),D.el(5))))");
  }
}