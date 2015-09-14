redblack is package{
  -- inspired by Chris Okasaki's Purely FUnctional Data Structures

  type rbTree counts as redblack;

  private type color is red or black;

  private type rbTree of t where comparable over t is 
      rbEmpty or
      rbNode(color,rbTree of t,t,rbTree of t);

  private
  fun rbMember(_,rbEmpty) is false
   |  rbMember(x,rbNode(_,L,y,R)) is
      x<y ? rbMember(x,L) :
      x>y ? rbMember(x,R) :
      false;

  private
  fun rbInsert(x,S) is let{
    fun ins(rbEmpty) is rbNode(black,rbEmpty,x,rbEmpty)
     |  ins(s matching rbNode(C,L,y,R)) is
	      x<y ? balance(C,ins(L),y,R) :
	      x>y ? balance(C,L,y,ins(R)) :
	      s;
    def rbNode(_,A,Y,B) is ins(S);
  } in rbNode(black,A,Y,B);

  private
  fun balance(black,rbNode(red,rbNode(red,A,X,B),Y,C),Z,D) is
        rbNode(red,rbNode(black,A,X,B),Y,rbNode(black,C,Z,D))
   |  balance(black,rbNode(red,A,X,rbNode(red,B,Y,C)),Z,D) is
        rbNode(red,rbNode(black,A,X,B),Y,rbNode(black,C,Z,D))
   |  balance(black,A,X,rbNode(red,rbNode(red,B,Y,C),Z,D)) is
        rbNode(red,rbNode(black,A,X,B),Y,rbNode(black,C,Z,D))
   |  balance(black,A,X,rbNode(red,B,Y,rbNode(red,C,Z,D))) is
        rbNode(red,rbNode(black,A,X,B),Y,rbNode(black,C,Z,D))
   |  balance(C,A,Y,B) is
        rbNode(C,A,Y,B)

  private 
  fun foldRbLeft(F,St,rbEmpty) is St
   |  foldRbLeft(F,St,rbNode(_,L,X,R)) is foldRbLeft(F,F(foldRbLeft(F,St,L),X),R)

  private
  fun foldRbRight(F,St,rbEmpty) is St
   |  foldRbRight(F,St,rbNode(_,L,X,R)) is foldRbRight(F,F(X,foldRbRight(F,St,R)),L)


  -- Some tests
  prc main() do {
    def T is rbInsert("beta",rbInsert("alpha",rbInsert("gamma",rbInsert("delta",rbEmpty))));
    logMsg(info,"T=$T");
  }
}