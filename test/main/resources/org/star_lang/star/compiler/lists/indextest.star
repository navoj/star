-- test the list indexing functions
indextest is package{
  def LL is list of ["a","b","c","d","e","f"]
  
  def Ag is agg{
    LL = list of ["a","b","c","d","e","f"];
  };
  
  type Agg is agg{
    LL has type list of string;
  };
  
  def CC is cons of ["a","b","c","d"];
  
  def MM is dictionary of ["a"->1,"b"->2,"c"->3];
  
  prc main() do {
    logMsg(info,"$LL");
    def XX is LL[size(LL)-1];
    assert XX has value "f";
    logMsg(info,"LL[\$]=$XX");
    
    def EE is someValue(LL[size(LL)-2]);
    logMsg(info,"EE = $EE");
    
    def AA is LL[0];
    logMsg(info,"AA = $AA");
    assert AA has value "a";
        
    def BB is Ag.LL[1];
    logMsg(info,"BB=$BB");
    
    def DD is Ag.LL[size(LL)-3];
    logMsg(info,"DD=$DD");
    
    assert (LL[10] or else "none") = "none";
    
    assert MM["a"] has value 1;
    assert MM["b"] has value 2;
    assert MM["c"] has value 3;
    assert MM["e"] = none;
    
    assert CC[0] has value "a";
    assert CC[1] has value "b";
  }
}