doubleSel is package{
  fun foo(X) is valof{
    logMsg(info,"foo of $X");
    valis X+2;
  }
  
  fun bar(A) is switch foo(A) in {
    case X matching 4 is X;
    case _ default is -1
  };
  
  prc main() do {
    logMsg(info,"bar = $(bar(2))");
    assert bar(2)=4;
  }
}