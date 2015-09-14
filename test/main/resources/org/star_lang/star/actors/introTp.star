introTp is package{
  AA has type actor of {
    IN has type occurrence of integer;
  };
  
  def AA is actor{
    private proc has type (integer)=>(integer,integer,integer);
    fun proc(I) is (I,I,I);
    
    on X on IN do{
      logMsg(info,"Got $X - $(proc(X))");
    }
  } 
  
  prc main() do {
    notify AA with 10 on IN;
  }
}