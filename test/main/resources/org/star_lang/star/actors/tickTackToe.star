/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
tickTackToe is package{  
  type mark is circle or cross or blank;
  
  implementation pPrint over mark is {
    fun ppDisp(circle) is ppStr(" o ")
     |  ppDisp(cross) is ppStr(" x ")
     |  ppDisp(blank) is ppStr("   ")
  }
     
  -- The board is numbered 0 through 8. 0 through 2 is row 1, 3 -> 5 is row 2, 6->8 is row 3.
  type board is board(list of mark);
  
  implementation pPrint over board is {
    fun ppDisp(board(B)) is dispBoard(B)
  } using {
    fun dispBoard(B) is valof {
      def [B0,B1,B2,B3,B4,B5,B6,B7,B8] is B
      valis ppSequence(0,cons of {ppDisp(B0);ppDisp(B1);ppDisp(B2);ppStr("\n");ppDisp(B3);ppDisp(B4);ppDisp(B5);ppStr("\n");ppDisp(B6);ppDisp(B7);ppDisp(B8)})
    }
  }
  
  implementation iterable over board determines ((mark,mark,mark)) is {
    fun _iterate(board(list of [C1,C2,C3,C4,C5,C6,C7,C8,C9]),Fn,St) is iter(C1,C2,C3,C4,C5,C6,C7,C8,C9,Fn,St);
  } using {
    fun iter(c1,c2,c3,c4,c5,c6,c7,c8,c9,Fn,Ste) is let{
      fun itRow(C1,C2,C3,NoMore(X)) is NoMore(X)
       |  itRow(C1,C2,C3,St) is Fn((C1,C2,C3),St);
      
      fun itRows(St) is itRow(c7,c8,c9,itRow(c4,c5,c6,itRow(c1,c2,c3,St)));
      
      fun itCols(St) is itRow(c1,c4,c7,itRow(c2,c5,c8,itRow(c3,c6,c9,St)));
      
      fun itDiags(St) is itRow(c1,c5,c9,itRow(c3,c5,c7,St));
    } in itDiags(itCols(itRows(Ste)));
  }
  
  implementation indexed_iterable over board determines ((integer,integer,integer),(mark,mark,mark)) is {
    fun _ixiterate(board(list of {C1;C2;C3;C4;C5;C6;C7;C8;C9}),Fn,St) is iter(C1,C2,C3,C4,C5,C6,C7,C8,C9,Fn,St);
  } using {
    fun iter(c1,c2,c3,c4,c5,c6,c7,c8,c9,Fn,Ste) is let{
      fun itRow(_,_,NoMore(X)) is NoMore(X)
       |  itRow(R,N,St) is Fn(N,R,St);
      
      fun itRows(St) is itRow((c7,c8,c9),(6,7,8),itRow((c4,c5,c6),(3,4,5),itRow((c1,c2,c3),(0,1,2),St)));
      
      fun itCols(St) is itRow((c1,c4,c7),(0,3,6),itRow((c2,c5,c8),(1,4,7),itRow((c3,c6,c9),(2,5,8),St)));
      
      fun itDiags(St) is itRow((c1,c5,c9),(0,4,8),itRow((c3,c5,c7),(2,4,6),St));
    } in itDiags(itCols(itRows(Ste)));
  }
  
  fun winMove(B,P) is list of { all M where R->L in B and win(R,L,P) matches some(M) }
  
  fun blockMove(B,P) is winMove(B,reverse(P));
  
  fun win((O1,O2,O3),(blank,C2,C3),MM) where C2=MM and C3=MM is some(O1)
   |  win((O1,O2,O3),(C1,blank,C3),MM) where C1=MM and C3=MM is some(O2)
   |  win((O1,O2,O3),(C1,C2,blank),MM) where C1=MM and C2=MM is some(O3)
   |  win(_,_,_) default is none
  
  fun winState(B,P) is L in B and isWinState(L,P);
  
  fun isWinState((C1,C2,C3),P) is C1=P and C2=P and C3=P; 
  
  fun forkMove(B,M) is list of { all P where P in range(0,9,1) and move(B,P,M) matches some(BB) and size(winMove(BB,M))>1 };
  
  fun blockFork(B,M) is forkMove(B,reverse(M));
  
  fun move(board(B),P,M) where B[P] has value blank is some(board(B[P->M]))
   |  move(_,_,_) default is none;
  
  fun play(board(B),Ps,P) is board(B[Ps->P]);
  
  fun oppositeCorner(board(B),Plyr) is list of { all P where P in list of {0; 2; 6; 8} and B[opposite(P)] = some(reverse(Plyr)) and B[P] has value blank };
  
  fun opposite(0) is 8
   |  opposite(8) is 0
   |  opposite(2) is 6
   |  opposite(6) is 2
  
  fun freeCorner(board(B),Plyr) is list of { all P where P in list of {0; 2; 6; 8} and B[P] has value blank };
  
  fun freeSide(board(B),Plyr) is list of { all P where P in list of {1; 3; 5; 7} and B[P] has value blank };
  
  def BB0 is board(list of {blank;blank;blank;blank;blank;blank;blank;blank;blank});
  def BB1 is board(list of {blank;blank;circle;blank;circle;circle;blank;blank;blank});
  def BB2 is board(list of {cross;blank;blank;blank;circle;blank;blank;blank;blank});
  def BB3 is board(list of {cross;blank;blank;blank;circle;blank;blank;blank;cross});
  
  implementation reversible over mark is {
    fun reverse(circle) is cross
     |  reverse(cross) is circle
     |  reverse(blank) is blank
  }
  
  fun perfectPlay(B,Plyr) is valof{
    if winMove(B,Plyr) matches list of [Ps,.._] then
      valis some(Ps);

    if blockMove(B,Plyr) matches list of [Ps,.._] then
      valis some(Ps);
    if forkMove(B,Plyr) matches list of [Ps,.._] then
      valis some(Ps);
    if blockFork(B,Plyr) matches list of [Ps,.._] then
      valis some(Ps);
    if move(B,4,Plyr) matches some(BB) then
      valis some(4);
    if oppositeCorner(B,Plyr) matches list of [Ps,.._] then
      valis some(Ps);
    if freeCorner(B,Plyr) matches list of [Ps,.._] then
      valis some(Ps);
    if freeSide(B,Plyr) matches list of [Ps,.._] then
      valis some(Ps)
    valis none;
  }
  
  fun freePositions(board(BB)) is list of { all Ps where Ps in range(0,9,1) and BB[Ps] has value blank };
  
  fun randomPlay(B,Plyr) is any of P where P in freePositions(B) and random(2) >1;
  
  prc playCycle() do {
    var B := BB0;
    var Plyr := cross;
    
    while not winState(B,Plyr) do {
      if perfectPlay(B,Plyr) matches some(Ps) then{
        logMsg(info,"$Plyr plays $Ps");
        B := play(B,Ps,Plyr);
        logMsg(info,"Board now\n$B");
        Plyr := reverse(Plyr);
      } else {
        logMsg(info,"draw");
        valis ();
      }
    }
    logMsg(info,"$Plyr wins");
  }
  
  prc main() do {
    logMsg(info,"W1=$(winMove(BB1,circle))");
    
    assert winMove(BB1,circle) = list of [3,8,6];
    assert winMove(BB1,circle) = blockMove(BB1,cross);
    
    logMsg(info,"W2=$(blockMove(BB2,cross))");
    logMsg(info,"B2=$(blockMove(BB2,circle))");
    
    logMsg(info,"F2=$(forkMove(BB3,cross))");
    
    logMsg(info,"M1=$(perfectPlay(BB0,circle))");
    
    assert not winState(BB0,circle) and not winState(BB0,cross);
    assert not winState(BB1,circle) and not winState(BB1,cross);
    
    playCycle();
  }
}