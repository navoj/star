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
 */
 constructorExp is package{
 
  type c1 of t is c1(integer,t);
  
  type c2 of t is c2(integer,t);
  
  type encap is encap{
    el has kind type where pPrint over el and equality over el;
    c has type for all t such that (integer,t)<=>el
  }
  
  C1 is encap{
    type c1 of string counts as el;
    c is c1;
    
    implementation pPrint over el is {
      ppDisp = ppDisp
    }
    
    implementation equality over el is {
      (=) = (=)
    }
  }
  
  C2 is encap{
    type c2 of integer counts as el;
    c is c2;
    
    implementation pPrint over el is {
      ppDisp = ppDisp
    }
    
    implementation equality over el is {
      (=) = (=)
    }
  }
  
  main() do {
    R1 is C1.c(34,"peter");
    R2 is C2.c(23,56);
    
    logMsg(info,"R1=$R1");
    logMsg(info,"R2=$R2");
    
    assert R1 matches C1.c(34,"peter")
  }
}