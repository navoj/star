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
perftest is package{
  Size is 10000;
  
  def Base is indexed of {all ("$Ix",Ix) where Ix in (iota(1,Size,1) has type array of integer)};
  
  var ind := indexed{};
  
  {
    merge ind with Base;
  }
  
  main() do {
    -- logMsg(info,"base is $Base");
    
    def startBase is nanos();
    for Cx in iota(1,Size,3) do
    {
      def XX is all Ix where (Ix,Cx) in Base; 
      -- logMsg(info,"$XX");
    }
    def baseTime is nanos()-startBase;
    logMsg(info,"base time is $(baseTime/1000000L) milli-seconds");
    
    -- make sure index is seeded
    def firstInd is nanos();
    for Cx in iota(1,Size,3) do
    {
      def XX is all Ix where (Ix,Cx) in ind; 
      -- logMsg(info,"$Cx->$XX");
    }
    def firstIndTime is nanos()-firstInd;
    logMsg(info,"first indexed time is $(firstIndTime/100000L) milli-seconds");
    
    def startInd is nanos();
    for Cx in iota(1,Size,3) do
    {
      def XX is all Ix where (Ix,Cx) in ind; 
      -- logMsg(info,"$Cx->$XX");
    }
    def indTime is nanos()-startInd;
    logMsg(info,"indexed time is $(indTime/100000L) milli-seconds");
        
    -- logMsg(info,"indexed is $ind");
  }
}
  