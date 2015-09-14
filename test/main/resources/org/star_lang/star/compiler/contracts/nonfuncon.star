nonfuncon is package{
  -- a test of non-function contract elements
  
  type Gen of %a is Gen((integer) => %a)

  contract Arb over %a is {
    arb has type Gen of %a;
  }
  
  implementation Arb over string is {
    def arb is Gen((Ix) => display(Ix));
  }
  
  XX has type Gen of string;
  def XX is arb;
  
  xGen has type (Gen of string,integer)=>string;
  fun xGen(Gen(F),Ix) is F(Ix);
  
  prc main() do {    
    assert xGen(arb,3)="3";
  }
}