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
maptest is package{

  var H := dictionary of { "A"->1; "B"->2; "C"->3; "D"->4 };
  
  #mget(?H,?P,?D) ==> (__hashGet(H,P) matches some(V) ? V |  D);

  main has type action();
  main() do
  {
    BV is H["B"];
    logMsg(info,"get H[B] is $BV");
    assert BV has value 2;
    
    assert H["D"] has value 4;
    
    assert H["D"]  has value  4;
    
    assert not H["E"] has value  _;
    
    try{
      assert someValue(H["E"]) = 1
    } on abort {
      E do logMsg(info,"We got the exception $E");
    };
    
    H["E"] := 45;
    
    logMsg(info,"$(mget(H,"D", nonInteger))");
    
    logMsg(info,"$(mget(H,"E", nonInteger))");
    
    remove H["A"];
    
    logMsg(info,"$H");
    
    assert not "A" -> _ in H;
    
    if "A"->_ in H then
      logMsg(info,"$H contains A")
    else
      logMsg(info,"$H does not contain A");
      
    if H["A"] matches _ then
      logMsg(info,"$H contains A")
    else
      logMsg(info,"$H does not contain A");
      
    if "D"->_ in H then
      logMsg(info,"$H contains D")
    else
      logMsg(info,"$H does not contain D");
      
    assert "D" -> _ in H;

    for K->V in H do{
	    logMsg(info,"K=$K, V=$V");
    }
  }
}