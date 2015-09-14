simplestTask is package{
  import task;
  
  tt has type (task of integer,task of integer)=>task of integer;
  fun tt(S,T) is task{
    def X is valof S;
    def Y is valof T;
    valis X+Y;
  };
  
  prc main() do {
    assert valof tt(task{valis 2},task{valis 3}) = 5
  }
}