/**
 * 
 * Copyright (C) 2013 Starview Inc
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
    ppDisp(circle) is ppStr(" o ");
    ppDisp(cross) is ppStr(" x ");
    ppDisp(blank) is ppStr("   ");
  }
     
  -- The board is numbered 0 through 8. 0 through 2 is row 1, 3 -> 5 is row 2, 6->8 is row 3.
  type board is board(list of mark);
  
  implementation pPrint over board is {
    ppDisp(board(B)) is dispBoard(B)
  } using {
    dispBoard(B) is ppSequence(0,cons of {ppDisp(B[0]);ppDisp(B[1]);ppDisp(B[2]);ppStr("\n");ppDisp(B[3]);ppDisp(B[4]);ppDisp(B[5]);ppStr("\n");ppDisp(B[6]);ppDisp(B[7]);ppDisp(B[8])})
  }
  
  implementation iterable over board determines ((mark,mark,mark)) is {
    _iterate(board(list of {C1;C2;C3;C4;C5;C6;C7;C8;C9}),Fn,St) is iter(C1,C2,C3,C4,C5,C6,C7,C8,C9,Fn,St);
  } using {
    iter(c1,c2,c3,c4,c5,c6,c7,c8,c9,Fn,Ste) is let{
      itRow(C1,C2,C3,NoMore(X)) is NoMore(X);
      itRow(C1,C2,C3,St) is Fn((C1,C2,C3),St);
      
      itRows(St) is itRow(c7,c8,c9,itRow(c4,c5,c6,itRow(c1,c2,c3,St)));
      
      itCols(St) is itRow(c1,c4,c7,itRow(c2,c5,c8,itRow(c3,c6,c9,St)));
      
      itDiags(St) is itRow(c1,c5,c9,itRow(c3,c5,c7,St));
    } in itDiags(itCols(itRows(Ste)));
  }
  
  implementation indexed_iterable over board determines ((integer,integer,integer),(mark,mark,mark)) is {
    _ixiterate(board(list of {C1;C2;C3;C4;C5;C6;C7;C8;C9}),Fn,St) is iter(C1,C2,C3,C4,C5,C6,C7,C8,C9,Fn,St);
  } using {
    iter(c1,c2,c3,c4,c5,c6,c7,c8,c9,Fn,Ste) is let{
      itRow(_,_,NoMore(X)) is NoMore(X);
      itRow(R,N,St) is Fn(N,R,St);
      
      itRows(St) is itRow((c7,c8,c9),(6,7,8),itRow((c4,c5,c6),(3,4,5),itRow((c1,c2,c3),(0,1,2),St)));
      
      itCols(St) is itRow((c1,c4,c7),(0,3,6),itRow((c2,c5,c8),(1,4,7),itRow((c3,c6,c9),(2,5,8),St)));
      
      itDiags(St) is itRow((c1,c5,c9),(0,4,8),itRow((c3,c5,c7),(2,4,6),St));
    } in itDiags(itCols(itRows(Ste)));
  }
  
  winMove(B,P) is relation of { all M where R->L in B and win(R,L,P) matches some(M) }
  
  blockMove(B,P) is winMove(B,reverse(P));
  
  win((O1,O2,O3),(blank,C2,C3),MM) where C2=MM and C3=MM is some(O1);
  win((O1,O2,O3),(C1,blank,C3),MM) where C1=MM and C3=MM is some(O2);
  win((O1,O2,O3),(C1,C2,blank),MM) where C1=MM and C2=MM is some(O3);
  win(_,_,_) default is none;
  
  winState(B,P) is L in B and isWinState(L,P);
  
  isWinState((C1,C2,C3),P) is C1=P and C2=P and C3=P; 
  
  forkMove(B,M) is relation of { all P where P in range(0,9,1) and move(B,P,M) matches some(BB) and size(winMove(BB,M))>1 };
  
  blockFork(B,M) is forkMove(B,reverse(M));
  
  move(board(B),P,M) where B[P]=blank is some(board(B[P->M]));
  move(_,_,_) default is none;
  
  play(board(B),Ps,P) is board(B[Ps->P]);
  
  oppositeCorner(board(B),Plyr) is relation of { all P where P in list of {0; 2; 6; 8} and B[opposite(P)]=reverse(Plyr) and B[P]=blank };
  
  opposite(0) is 8;
  opposite(8) is 0;
  opposite(2) is 6;
  opposite(6) is 2;
  
  freeCorner(board(B),Plyr) is relation of { all P where P in list of {0; 2; 6; 8} and B[P]=blank };
  
  freeSide(board(B),Plyr) is relation of { all P where P in list of {1; 3; 5; 7} and B[P]=blank };
  
  BB0 is board(list of {blank;blank;blank;blank;blank;blank;blank;blank;blank});
  BB1 is board(list of {blank;blank;circle;blank;circle;circle;blank;blank;blank});
  BB2 is board(list of {cross;blank;blank;blank;circle;blank;blank;blank;blank});
  BB3 is board(list of {cross;blank;blank;blank;circle;blank;blank;blank;cross});
  
  implementation reversible over mark is {
    reverse(circle) is cross;
    reverse(cross) is circle;
    reverse(blank) is blank;
  }
  
  perfectPlay(B,Plyr) is valof{
    if winMove(B,Plyr) matches relation of {Ps;.._} then
      valis some(Ps);

    if blockMove(B,Plyr) matches relation of {Ps;.._} then
      valis some(Ps);
    if forkMove(B,Plyr) matches relation of {Ps;.._} then
      valis some(Ps);
    if blockFork(B,Plyr) matches relation of {Ps;.._} then
      valis some(Ps);
    if move(B,4,Plyr) matches some(BB) then
      valis some(4);
    if oppositeCorner(B,Plyr) matches relation of {Ps;.._} then
      valis some(Ps);
    if freeCorner(B,Plyr) matches relation of {Ps;.._} then
      valis some(Ps);
    if freeSide(B,Plyr) matches relation of {Ps;.._} then
      valis some(Ps)
    valis none;
  }
  
  freePositions(board(BB)) is relation of { all Ps where Ps in range(0,9,1) and BB[Ps]=blank };
  
  randomPlay(B,Plyr) is any of P where P in freePositions(B) and random(2) >1;
  
  playCycle() do {
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
  
  main() do {
    logMsg(info,"W1=$(winMove(BB1,circle))");
    
    assert winMove(BB1,circle) = relation of {3;6;8};
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