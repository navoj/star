condexptask is package{
  fun tt(X) is task{
    valis valof task { valis X+2 };
  }
  
  fun pos(X) is X>0;
  
  def alpha is "alpha";
  def beta is "beta";
  
  cc has type (integer)=>task of string;
  fun cc(X) is task{
    valis "$X"
  }
  
  fun S0(X) is task{ valis (pos(X) ? alpha : valof cc(X)) }

  fun S1(X) is task{ valis (pos(X) ? valof task{ valis "$X"}  : beta) }
  
  def L is list of [1,2,3];

  fun S2(X) is task{ valis X in L ? valof task { valis some(X)} : none };
  
  prc main() do{
    def R0a is valof S0(1);
    def R0b is valof S0(-1);
   
    def R1a is valof S1(2);
    def R1b is valof S1(-2);
   
    def R2a is valof S2(1);
    def R2b is valof S2(4);
   
    assert R0a=alpha;
    assert R0b="-1";
   
    assert R1a="2";
    assert R1b=beta;
   
    assert R2a=some(1);
    assert R2b=none;
  }
}