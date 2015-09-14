counter is package{
  private var id := 0;
  
  fun newCounterNo() is valof{
    id := id+1;
    valis id;
  }
}