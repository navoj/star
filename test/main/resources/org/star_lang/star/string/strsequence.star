strsequence is package{

  shChar has type (integer,IterState of cons of integer)=>IterState of cons of integer;
  fun shChar(X,ContinueWith(st)) is valof{
    logMsg(info,"codepoint: $X");
    valis ContinueWith(cons(X,st))
  };
  
  fun concat([],X) is X
   |  concat([H,..T],X) is [H,..concat(T,X)];
  
  prc main() do {
    def SS is "a string";
    
    def R is __string_iter(SS,shChar,ContinueWith(nil));
    
    logMsg(info,"R=$R");
    
    assert R=ContinueWith(cons of [0cg, 0cn, 0ci, 0cr, 0ct, 0cs, 0c , 0ca]);
    
    def TT is concat(SS," and more");
    assert TT="a string and more";
  }
}