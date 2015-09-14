listtests is package{

  def testLists is list of [1,2,3,4,5,6];
  

  dummyAction has type action(integer, list of integer);
  prc dummyAction(i, list of [s,..ss]) do {
    logMsg(info,"dummy head $s, tail is $ss");
  };
  
  discardOdds has type (list of integer)=>list of integer;
  fun discardOdds(list of []) is list of []
   |  discardOdds(list of [X,..L]) where odd(X) is discardOdds(L)
   |  discardOdds(list of [X,..L]) default is list of [X,..discardOdds(L)]
  
  odd has type (integer)=>boolean;
  -- fun odd(X) is not even(X);
  fun odd(X) is X%2!=0
  
  even has type (integer)=>boolean;
  fun even(X) is X%2=0;
  
  def L1 is list of [1, 2, 3];
  var L2 := L1;
  
  fun filter_(_empty(),_) is _nil()
   |  filter_(_pair(H,T),P) where P(H) is _cons(H,filter_(T,P))
   |  filter_(_pair(_,T),P) is filter_(T,P)
  
  onlyEven has type (list of integer)=>list of integer;
  fun onlyEven(L) is filter_(L,even)
  
  fun evenIndices(L) is all Ix where (Ix->e) in L and even(e)
    
  fun secondToLast(L) is L[size(L)-2]

  -- using the filterable contract
  fun evens(L) is filter(even,L)
    
  prc main() do {
    assert testLists matches list of [1,..X] and X=list of [2,3,4,5,6];
    
    assert testLists matches list of [1,2,3,..X] and X=list of [4,5,6];
    if testLists matches list of[1,2,..X] and X matches list of [3,4,5,6] then
      logMsg(info,"Tail is $X");

    assert testLists matches list of [1,2,3,4,5,6];
      
    var L:=list of [1,2,3,4,5];
    
    logMsg(info,"second from end $(secondToLast(L))"); 
    
    assert secondToLast(L) has value 4;
    
    dummyAction(23,L);
    
    logMsg(info,"discarding odds in list of [1,2,3,4,5] = $(discardOdds(list of [1,2,3,4,5]))");
    assert discardOdds(list of [1,2,3,4,5])=list of [2,4];
    assert size(discardOdds(list of [1,2,3,4,5]))=2;

--    logMsg(info,"L2=$L2");    
    L2[1] := 19;

    logMsg(info, "L1 and L2 are $L1 and $L2");
    assert L1[1] has value 2 and L2[1] has value 19;
    
    logMsg(info,"even of list of [1,2,3,4,5] = $(onlyEven(list of [1,2,3,4,5]))");
    assert onlyEven(list of [1,2,3,4,5])=list of[2,4];
    
    logMsg(info,"even indices of list of [1,2,3,4,5] = $(evenIndices(list of [1,2,3,4,5]))");
    assert evenIndices(list of [1,2,3,4,5]) = list of [1,3];
    
    assert evens(list of [1,2,3,4,5])=list of[2,4];
  }
}