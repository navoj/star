multiop is package{
  #infix("this op",700);
  
  fun X this op Y is X+Y;
  
  prc main() do
  {
    assert 2 this op 3 = 5;
  }
}