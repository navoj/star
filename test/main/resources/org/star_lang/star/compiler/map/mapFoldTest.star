mapFoldTest is package{
  def H is dictionary of [ "A"->1, "B"->2, "C"->3, "D"->4 ];
  
  def L is leftFold((A,(K,V))=>(A+V),0,H);
  def R is rightFold(((K,V),A)=>(A*V),1,H);
  
  prc main() do {
    assert L=10;
    assert R=24;
  }
}
 