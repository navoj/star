konspp is package{

  type kk of %t is kn or kkons(%t,kk of %t);
  
  implementation sequence over kk of %t determines %t is {
    fun _cons(H,T) is kkons(H,T)
    fun _apnd(T,H) is konc(T,H)
    ptn _empty() from kn
    ptn _pair(H,T) from kkons(H,T)
    ptn _back(kn,E) from kkons(E,kn)
     |  _back(kkons(H,B),E) from kkons(H,B1) where B1 matches _back(B,E)
    fun _nil() is kn
  } using {
    fun konc(kn,H) is kkons(H,kn)
     |  konc(kkons(H,T),E) is kkons(H,konc(T,E));
  }
  
  prc main() do {
    logMsg(info,"try printing a sequence: $(kk of [1,2,3,4])");
    assert display(kk of [1,2,3,4]) = "kk of [1, 2, 3, 4]";
  }
}