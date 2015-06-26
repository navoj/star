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
hashSetTest is package{
  import treemap;
  
  fun buildTree(Count) is valof{
    var H := treemap of [];
    
    var C := 1;
    while C<Count do{
      -- K is random(C);
      def K is C;
      H[K] := "val=$K";
      C := C+1;
    }
    valis H;
  };
  
  prc main() do
  {
    def Count is 100000;
    
    var B1 := nanos();
    var M := dictionary of [];
    
    for ix in range(1,Count,1) do 
    {
      M[ix] := "val=$ix"
    }
    
    B1 := nanos()-B1;
    logMsg(info,"dictionary: M has $(size(M)) elements");
    
    var B2 := nanos();
    def T is buildTree(Count);
    B2 := nanos()-B2;
    
    def B2Secs is B2 as float/1.0e9;
    logMsg(info,"build time is $(B2Secs), build factor is $(B2 as float/B1 as float)");
    logMsg(info,"dictionary depth is $(tree_depth(T))");
 
    var S0 := nanos();
    for ix2 in range(1,Count,1) do
      assert true;
    S0 := nanos()-S0;
 
    var S1 := nanos();
    for i in range(1,Count,1) do
      assert present M[i];
    S1 := nanos()-S1;
    
    logMsg(info,"time for hash is $((S1-S0) as float/1.0e9) secs");
    
    var S2 := nanos();
    for ix in range(1,Count,1) do
      ignore T[ix];
    
    S2 := nanos()-S2;
    
    logMsg(info,"time for dictionary is $((S2-S0) as float/1.0e9) secs");
    logMsg(info,"search factor is $((S2-S0)as float/(S1-S0) as float)");
    
    var TT := T;
    for ix in range(1, Count, 1) do{
      remove TT[ix];
--      assert (NoMore(TT[ix]) default NoneFound) matches NoneFound;
--      assert (NoMore(T[ix]) default NoneFound) matches NoMore(_);
--      logMsg(info,"TT after removing $ix is $(__display(TT))");
    };
    assert isEmpty(TT);
    logMsg(info,"T has $(size(T)) elements, Count=$Count");
    assert size(T)=Count-1;
    
    TT := T;
    var DS := nanos();
    for ix in range(0, Count, 1) do{
      remove TT[ix]
    };
    DS := nanos()-DS;
    logMsg(info,"time for clearing tree map is $(DS as float/1.0e9) secs");
    
    var DM := nanos();
    for ix in range(0,Count,1) do{
      remove M[ix]
    };
    DM := nanos()-DM;
    assert size(M)=0;
    logMsg(info,"time for clearing hash dictionary is $(DM as float/1.0e9) secs");
    logMsg(info,"delete time factor is $(DS as float/DM as float)");
  }
}