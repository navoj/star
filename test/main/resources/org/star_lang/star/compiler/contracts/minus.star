minus is package{
  import plus;
  
  prc main() do
  {
    assert plus(2,3)=5;
    assert plus(2,3L)=5L;
    assert plus(2L,3) = 5L;
    assert plus(2,3.4) = 5.4;
  }
}