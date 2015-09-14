/*
 * 
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
  *
 */
-- Defining sort

quick is package{

  quick has type (list of %s,((%s,%s) =>boolean)) => list of %s
  fun quick(list of [X],_) is list of [X]
   |  quick(list of [],_) is list of []
   |  quick(L,C) where size(L)>1 is let{
        var lftHalf := list of [];
        rgtHalf has type ref list of %s;
        var rgtHalf := list of [];
        def pivot is someValue(L[0]);
    
        split has type action();
        prc split() do {
	      -- logMsg(info,"pivot is $pivot");
          for Ix->el in L do {
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
