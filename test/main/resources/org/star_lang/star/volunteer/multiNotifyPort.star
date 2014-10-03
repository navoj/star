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
import ports;
import volmultinotify;

multiNotifyPort is package{

  var Acount := 0;
  var Bcount := 0;
  
  Port_In is p0rt{
    on ((A has type string),B) on AA do {
      Acount := Acount+B;
      logMsg(info,"AA: A=$A,B=$B, Acount=$Acount");
    };
    
    on ((A has type string),B) on BB do {
      Bcount := Bcount+B;
      logMsg(info,"BB: A=$A,B=$B, Bcount=$Bcount");
    };
  };

  P1 is connectPort_Out(Port_In);
  
  main() do {
    notify P1 with ("main greeting on AA",1) on AA;
    notify P1 with ("greeting on BB",2) on BB;

    assert Acount=1;
    assert Bcount=2;
  }
}