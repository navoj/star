anytest is package{

  type foo is foo{
    KK has type any;
  };
  
  prc main() do {
    I has type ref any;
    var I := 12 cast any;
    
    logMsg(info,"I is $I");
    
    logMsg(info,"unpack is $(unpack(I))");
  }
  
  unpack has type (any)=>integer;
  fun unpack(X cast integer) is X;
}