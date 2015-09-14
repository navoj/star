abortTak is package{
  import task;
  
  fun aa(X) is task {
    if X > 10 then
      raise "too large"
    else
      valis X*2;
  }
  
  fun aa2(X) is taskWait(((wakeup) do {
    if X > 10 then
      wakeup(taskFail(exception(nonString,"too large" cast any,__location__)))
    else
      wakeup(taskReturn(X*2));
  })); 

  bb has type (integer, (integer) => task of integer) => task of integer
  fun bb(X, f) is task {
    try {
      def v is valof f(X);
      valis v;
    } on abort {
      case _ do valis X;
    }
  }

  prc main() do {
    assert valof bb(5, aa) = 10;
    assert valof bb(15, aa) = 15;
    
    assert valof bb(5, aa2) = 10;
    assert valof bb(15, aa2) = 15;
  }
}