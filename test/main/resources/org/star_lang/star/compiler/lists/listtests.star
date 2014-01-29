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
listtests is package{

  testLists is list{1;2;3;4;5;6};
  

  dummyAction has type action(integer, list of integer);
  dummyAction(i, list{s;..ss}) do {
    logMsg(info,"dummy head $s, tail is $ss");
  };
  
  discardOdds has type (list of integer)=>list of integer;
  discardOdds(list{}) is list{};
  discardOdds(list{X;..L}) where odd(X) is discardOdds(L);
  discardOdds(list{X;..L}) default is list{X;..discardOdds(L)};
  
  odd has type (integer)=>boolean;
  -- odd(X) is not even(X);
  odd(X) is X%2!=0;
  
  even has type (integer)=>boolean;
  even(X) is X%2=0;
  
  L1 is list{1; 2; 3};
  var L2 := L1;
  
  filter_(_empty(),_) is _nil();
  filter_(_pair(H,T),P) where P(H) is _cons(H,filter_(T,P));
  filter_(_pair(_,T),P) is filter_(T,P);
  
  onlyEven has type (list of integer)=>list of integer;
  onlyEven(L) is filter_(L,even);
  
  evenIndices(L) is all Ix where (Ix->e) in L and even(e);
    
  secondToLast(L) is L[size(L)-2];

  -- using the filterable contract
  evens(L) is filter(even,L);
    
  main has type action();
  main() do {
    assert testLists matches list{1;..X} and X=list{2;3;4;5;6};
    
    assert testLists matches list{1;2;3;..X} and X=list{4;5;6};
    if testLists matches list{1;2;..X} and X matches list{3;4;5;6} then
      logMsg(info,"Tail is $X");

    assert testLists matches list{1;2;3;4;5;6};
      
    var L:=list{1;2;3;4;5};
    
    logMsg(info,"second from end $(secondToLast(L))"); 
    
    assert secondToLast(L)=4;
    
    dummyAction(23,L);
    
    logMsg(info,"discarding odds in list{1;2;3;4;5} = $(discardOdds(list{1;2;3;4;5}))");
    assert discardOdds(list{1;2;3;4;5})=list{2;4};
    assert size(discardOdds(list{1;2;3;4;5}))=2;

--    logMsg(info,"L2=$L2");    
    L2[1] := 19;

    logMsg(info, "L1 and L2 are $L1 and $L2");
    assert L1[1]=2 and L2[1]=19;
    
    logMsg(info,"even of list{1;2;3;4;5} = $(onlyEven(list{1;2;3;4;5}))");
    assert onlyEven(list{1;2;3;4;5})=list{2;4};
    
    logMsg(info,"even indices of list{1;2;3;4;5} = $(evenIndices(list{1;2;3;4;5}))");
    assert evenIndices(list{1;2;3;4;5}) = array of {1;3};
    
    assert evens(list{1;2;3;4;5})=list{2;4};
  }
}