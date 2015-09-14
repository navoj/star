commonStatic is package{
  import common;
  import updateCommon;
  
  prc main() do{
    def X is updateX();
    
    assert commonX()=X
  }
}