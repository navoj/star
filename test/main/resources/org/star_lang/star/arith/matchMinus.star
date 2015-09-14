matchMinus is package {
  fun foo(a) is switch a in {
     case (-1) is "neg";
     case 0 is "zero";
     case 1 is "pos";
  };
  
  prc main() do{
    assert foo(-1) = "neg";
    assert foo(0) = "zero";
    assert foo(1) = "pos";
  }
}
