good is package{
  type good of T is good(T) or noGood(string)

  implementation for all T,E such that pPrint over good of T where pPrint over T is {
    ppDisp=showGood
  } using {
    fun showGood(good(G)) is ppSequence(0,[ppStr("good"),ppSpace,ppDisp(G)])
     |  showGood(noGood(N)) is ppSequence(0,[ppStr("noGood"),ppSpace,ppDisp(N)])
  }

  fun more(good(G),F) is F(G)
   |  more(noGood(M),F) is noGood(M)

  implementation (computation) over good determines string is {
    fun _encapsulate(X) is good(X)
    fun _abort(M) is noGood(M)
    fun _handle(good(X),_) is good(X)
     |  _handle(noGood(M),F) is F(M)
    fun _combine(good(X),F) is F(X)
     |  _combine(noGood(M),_) is noGood(M)
  }

  implementation execution over good is {
    fun _perform(good(X)) is X
  }

  implementation injection over (good,good) is {
    fun _inject(C) is C;
  }

}