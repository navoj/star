-- Defining sort

quick is package{

  quick has type (list of %s,((%s,%s) =>boolean)) => list of %s;
  quick(list of [X],_) is list of [X];
  quick(list of [],_) is list of [];
   
  quick(L,C) where size(L)>1 is let{
    var lftHalf := list of [];
    list of %s var rgtHalf := list of [];
    pivot is L[0];
    
    split has type action();
    split()
    {
	    -- logMsg(info,"pivot is $pivot");
      for el in L[1:$] do
      {
        if C(el,pivot) then
          lftHalf[$:] := list of el] -- list concatenate
        else
          rgtHalf[$:] := list of [el];
      };
			-- logMsg(info,"left half is $lftHalf");
      -- logMsg(info,"right half is $rgtHalf");
    };

    } in valof{
      split();
      valis quick(lftHalf,C)++list of [pivot,..quick(rgtHalf,C)];
    }
}
