overDefActor is package{

  fun plus(X) is X+X;
  
  fun f(X) is let{
    def A is actor{
      def pl is plus(X);
    }
  } in (query A's pl with pl);
  
  prc main() do {
    assert f(2)=4;
  }
}    