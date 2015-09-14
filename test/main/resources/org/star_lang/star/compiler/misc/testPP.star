testPP is package{
  import person;
  
  type fooPair is foo((string,integer));
  
  implementation pPrint over ((%a,%b)) where pPrint over %a and pPrint over %b is {
    def ppDisp is dispPair;
  } using {
    fun dispPair((L,R)) is ppSequence(2,cons of [ ppStr("!("), ppDisp(L), ppStr(", "), ppDisp(R), ppStr(")")]);
  }
  
  type tree of %t is empty or node(tree of %t,%t,tree of %t);
  
  implementation pPrint over tree of %t where pPrint over %t is {
    fun ppDisp(T) is ppSequence(2,cons of [ppStr("["), treeDisplay(T), ppStr("]")]);
  } using {
    fun treeDisplay(empty) is ppSpace
     |  treeDisplay(node(L,Lb,R)) is ppSequence(0,cons of [treeDisplay(L), ppDisp(Lb), treeDisplay(R) ])
  } 
  
  prc main() do {
    def Jack is someone{name="Jack"; gender=male};
    
    def DD is ppDisp(Jack);
    logMsg(info,"pp = $DD");
    logMsg(info,"flat = #(display(Jack))");
    
    logMsg(info,"foo = $(foo(("alpha",56)))");
    
    def TT is node(node(empty,"alpha",empty),"beta",node(empty,"gamma",empty));
    logMsg(info,"TT = $(ppDisp(TT))");
    logMsg(info,"TT = $TT");
  }
}

    