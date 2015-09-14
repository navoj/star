existenz is package{
  type grp is alias of { elem has kind type where pPrint over elem; op has type (elem,elem)=>elem };
  
  type xrec of %t is nox or xrec{ elem has kind type; val has type (elem)=>%t };
 
  grp has type grp;
  def grp is { 
    type integer counts as elem;
    op has type (integer,integer)=>integer;
    fun op(X,Y) is X+Y;
  }
  
  GF has type grp;
  def GF is {
    type elem = float;
    op = (+);
  };
  
  def XX is xrec{ type integer counts as elem; fun val(I) is I };
  
  YY has type {elem has kind type where pPrint over elem; op has type (elem,elem)=>elem; pp has type (elem)=>elem };
  def YY is {
    open grp;
    
    -- open xrec{ type string counts as elem; val("") is 3}
    fun pp(X) is op(X,X);
  }
    
  prc main() do {
    logMsg(info,"grp=$grp");
    
    def Y1 is YY.pp(2 cast YY.elem);
    logMsg(info,"Y1=$Y1");
    
    logMsg(info,"G1 = $(GF.op(1.0 cast GF.elem,2.0 cast GF.elem))");
  }
}
