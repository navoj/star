simpleCombine is package{
  import task;
  
  def tst is task { valis 0; }
  
  def mona is task {
    def b is 42;
    def c is valof tst;
    valis b;
  }
  
  prc main() do {
    assert valof mona = 42
  }
}