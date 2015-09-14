allqueryports is package{
  import volallquery;
  import ports;
  
  def RR is p0rt{
    var Folder := list of [
      ("alpha",true),
      ("beta",false),
      ("gamma",true)
    ];
    
    fun DELETE(K) where (K,_) in Folder is valof{
          delete ((Kk,_) where K=Kk) in Folder;
          valis true;
        }
     |  DELETE(K) default is false
    
    prc report() do logMsg(info,"Folder is $Folder");
  };
  
  def PP is connectPort_0(RR);
  
  prc main() do {
    def A1 is query PP's Folder with list of {all X where (X,true) in Folder};
    
    logMsg(info,"$A1");
    assert A1=list of ["alpha","gamma"];
  }
}