worksheet{
  fun AA(f,x) is action computation {
    valis x+valof f(x)
  }
  
  fun idA(X) is action computation { valis X};
  
  def A is AA(idA,2);
  
  show valof A;
  
  assert valof A = 4
}