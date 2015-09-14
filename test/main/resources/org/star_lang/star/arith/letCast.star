letCast is package{
  isDigit has type (char) => boolean;
  fun isDigit(c) is
    let {
      def sv is c as integer;
    } in ((sv >= 48) and (sv =< 57));
    
  prc main() do {
    assert isDigit('0');
    
    assert not isDigit(' ');
  }
}
