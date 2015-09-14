badfield is package{
  type foo is bar{ baz has type string};
  
  def FF is bar{baz="hi"};
  
  prc main() do {
    def KK is FF.foo;  -- should report an error
    
    logMsg(info,KK);
  }
}