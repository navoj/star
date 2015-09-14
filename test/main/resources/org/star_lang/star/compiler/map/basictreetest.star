basictreetest is package{
  import treemap;
  
  prc main() do {
    var M := trEmpty;
    
    M[9] := "nine";
    M[10] := "ten";
    M[25] := "twentyfive";
    M[26] := "twentysix";
    
   -- logMsg(info,"M=$M");
    logMsg(info,"M=#(__display(M))");
  }
}