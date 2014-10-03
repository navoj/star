/*
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
  quick(list of [X],_) is list of [X];
  quick(list of [],_) is list of [];
   
  quick(L,C) where size(L)>1 is let{
    var lftHalf := list of [];
    rgtHalf has type ref list of %s;
    var rgtHalf := list of [];
    pivot is L[0];
    
    split has type action();
    split() do {
	  -- logMsg(info,"pivot is $pivot");
      for Ix->el in L do
      {
        if Ix>0 then{
          if C(el,pivot) then
            lftHalf := lftHalf++list of [el] -- list concatenate
          else
            rgtHalf := rgtHalf++list of [el];
        };
	    -- logMsg(info,"left half is $lftHalf");
        -- logMsg(info,"right half is $rgtHalf");
      }
    };
  } in valof{
      split();
      valis quick(lftHalf,C)++list of [pivot,..quick(rgtHalf,C)];
    }
}
