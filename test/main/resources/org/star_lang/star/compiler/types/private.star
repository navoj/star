privatetest is package{

  private type Split of (%f) is Split(%f);

  private s has type () => Split of (integer);
  fun s() is Split(1) ;
  
  prc main() do {
    assert s()=Split(1);
  }
}