stringReverse is package{
  prc main() do {
    def S0 is "12345";
    
    assert reverse(S0)="54321";
    
    assert not reverse(S0)=S0;
    
    def S1 is "";
    logMsg(info,"reverse of S1 = $(reverse(S1))");
    
    assert reverse(S1)=S1;
    
    assert reverse("")="";
  }
}