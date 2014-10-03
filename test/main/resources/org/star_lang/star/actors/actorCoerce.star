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
actorCoerce is package{
  A is actor{
    strFyA(X) is X as string;
  };
  
  B is actor{
    rr is {
      strFyB(X) is X as string
    }
  }
  
  C() is actor{
    cc is {
      strFyC(X) is X as string
    }
  }
  
  main() do {
    XX is query A's strFyA with strFyA(12);
   
    logMsg(info,"XX=$XX");
    assert XX="12";
    
    YY is query B's rr with rr.strFyB(12);
    logMsg(info,"YY=$YY");
    
    assert YY="12";
    
    ZZ is query C()'s cc with cc.strFyC(12);
    logMsg(info,"ZZ=$ZZ");
    
    assert ZZ="12";
  }
}