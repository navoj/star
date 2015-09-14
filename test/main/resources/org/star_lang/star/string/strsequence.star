strsequence is package{

  shChar has type (char,IterState of cons of char)=>IterState of cons of char;
  fun shChar(X,ContinueWith(st)) is valof{
    logMsg(info,"char: $X");
    valis ContinueWith(cons(X,st))
  };
  
  fun concat([],X) is X
   |  concat([H,..T],X) is [H,..concat(T,X)];
  
  prc main() do {
    def SS is "a string";
    
    def R is __string_iter(SS,shChar,ContinueWith(nil));
    
    logMsg(info,"R=$R");
    
    assert R=ContinueWith(cons of ['g', 'n', 'i', 'r', 't', 's', ' ', 'a']);
    
    def TT is concat(SS," and more");
    assert TT="a string and more";
  }
}