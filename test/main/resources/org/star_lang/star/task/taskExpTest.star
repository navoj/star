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
taskExpTest is package{
  import task;
  
  equal has type (integer,integer)=>boolean;
  equal(A,B) is valof{
    logMsg(info,"is $A=$B, $(A=B)");
    valis A=B;
  };
  
  tt(X) is task{
    def Y is 2;
    valis X+Y;
  }
  
  uu(X) is task{
    logMsg(info,"We got $X");
    var Y := 1;
    Y:=Y*2;
    logMsg(info,"Y is $Y");
    logMsg(info,"returning $(X*Y)");
    valis X*Y;
  };
  
  ww is task {
    var v := 1;
 --   logMsg(info, "v is $v");
    if equal(v,2) then {
      perform task { v := 3; valis () }
    }
 --   logMsg(info, "v is now $v");
    valis v;
  };
  
  main() do {
    def XX is valof tt(3);
    assert XX=5;
    
    def YY is valof uu(3);
    assert YY=6;
    
    -- __stop_here();
    assert valof ww = 1;
  }
}