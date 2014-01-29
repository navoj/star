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
-- Defining sort

quick is package{

  quick has type (list of %s,((%s,%s) =>boolean)) => list of %s;
  quick(list{X},_) is list{X};
  quick(list{},_) is list{};
   
  quick(L,C) where size(L)>1 is let{
    var lftHalf := list{};
    list of %s var rgtHalf := list{};
    pivot is L[0];
    
    split has type action();
    split()
    {
	    -- logMsg(info,"pivot is $pivot");
      for el in L[1:$] do
      {
        if C(el,pivot) then
          lftHalf[$:] := list{el} -- list concatenate
        else
          rgtHalf[$:] := list{el};
      };
			-- logMsg(info,"left half is $lftHalf");
      -- logMsg(info,"right half is $rgtHalf");
    };

    } in valof{
      split();
      valis quick(lftHalf,C)<>list{pivot;..quick(rgtHalf,C)};
    }
}
