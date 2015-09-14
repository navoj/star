pascal is package{
  import beginEnd;
   
  procedure iFact(N)
    var F := 1;
    var Ix := 1;
    while Ix < N do 
    begin
      F := F*Ix;
      Ix := Ix+1;
    end;
    return F;
  end;
  
  procedure main()
    logMsg(info,"iFact(10)=$(iFact(10))")
    assert iFact(10)=362880
  end
  
}