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
taskCondTest is package{
  import task;
  
  fun tt(X) is task{
    valis X+2;
  }
  
  fun cc(X) is task{
    valis X<10;
  }
  
  fun ww(X) is task{
    var C:=0;
    
    while valof tt(C)<10 do{
      def XX is valof tt(X);
      C := XX+C;
     --  logMsg(info,"C=$C");
    }
    valis C+1;
  }
  
  fun vv() is task {
    var Z := 42;
    __stop_here();
    def t is task { Z := 21; valis 0; };
    __stop_here();
    valis false ? valof t : Z;
  };  
  
  prc main() do{
   def ZZ is valof ww(1);
   logMsg(info,"ZZ=$ZZ");
     
   assert ZZ = 10;
   
   def VV is valof vv();
   logMsg(info,"VV=$VV");
   assert VV=42;
  }
}
  
  