complexCons is package{
  -- test a complex cons expression
    
  fun dbl(X) is X+X;
  
  maxx has type (integer)=>integer;
  fun maxx(X) where X>10 is X
   |  maxx(X) default is 10
  
  def L0 is nil
  
  def L1 is cons(id,L0)
  
  def L2 is cons(dbl,L1)
  
  def L3 is cons(maxx,L2)
  
  assert size(L3)=3;
}