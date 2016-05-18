letCast is package{
  isDigit has type (integer) => boolean;
  fun isDigit(integer(c)) is __is_numeric(c)
    
  prc main() do {
    assert isDigit(0c0);
    
    assert not isDigit(0c );
  }
}
