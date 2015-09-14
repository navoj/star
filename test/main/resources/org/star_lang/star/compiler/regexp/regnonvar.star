regnonvar is package{
  prc main() do {
    def X is "mySymbol.myVenue";
       
    def found is (X matches `(.*:symbol)\.(.*:venue)`);
    logMsg(info, "$found $symbol $venue"); -- this will raise a compile error
  }
}