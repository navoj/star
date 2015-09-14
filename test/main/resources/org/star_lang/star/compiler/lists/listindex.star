listindex is package{
  prc main() do {
    def LL is list of [1, 2, 3, 4, 5, 6, 7];
    var KL := LL;
    
    assert LL[0] has value 1;
    
    KL[0] := -1;
    
    assert LL[0] has value 1;
    assert KL[0] has value -1;
    
    logMsg(info,"$(LL[2:4])");
    
    assert LL[4:]=list of [5, 6, 7];
    
    KL[4:6] := list of [10, 20];
    logMsg(info,"KL=$KL, KL[4:6]=$(KL[4:6])");
    assert KL=list of [-1,2,3,4,10,20,7];
    assert KL[4:6] = list of [10,20];
    
    def CC is cons of [1, 2, 3, 4, 5, 6, 7];
    var KC := CC;
    
    assert CC[0] has value 1;
    KC[0] := -1;
    
    assert CC[0] has value 1;
    assert KC[0] has value -1;
    
    logMsg(info,"$(CC[2:4])");
    KC[4:5] := cons of [10, 20];
    logMsg(info,"KC=$KC");
    assert KC=cons of [-1,2,3,4,10,20,6,7];
    assert KC[4:6] = cons of [10, 20];
  }
}