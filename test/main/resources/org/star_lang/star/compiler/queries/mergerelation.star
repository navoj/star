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
mergerelation is package{
        
  var Scores := list of [
    {name="j"; amount=1},
    {name="p"; amount=2},
    {name="m"; amount=0}
  ];      
  prc main() do {
    logMsg(info, "Test the merge relation function");
    merge Scores with list of [{name="X"; amount=9}];
    
    assert size(Scores)=4;
    assert {name="X"} in Scores;
    assert {name="j";amount=1} in Scores;
    assert {name="p";amount=M} in Scores and M<=2 and M>1;
    assert {name="m";amount=0} in Scores;
    assert not {name="j";amount=2} in Scores;
    
    logMsg(info,"Scores is $Scores");
  }
}