floatHash is package{
  prc main () do {
    def a is dictionary of [17000.0 -> 1.81];
    def b is dictionary of [17000.0 -> 1.81];
    assert a=b;
    assert a=a;
    assert 17000.0 = 17000.0;
    assert 1.81 = 1.81;

    def c is dictionary of [170000 -> 181];
    def d is dictionary of [170000 -> 181];
    assert c=d;
  }
}