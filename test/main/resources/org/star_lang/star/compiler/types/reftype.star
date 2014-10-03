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
reftype is package{
  -- test simple variants of the reference type
  
  apply(P,X,Y) do P(X,Y);
  
  assign(ref X,Y) do X:=Y;
  
  type person is someone{
    name has type string;
    age has type ref integer;
  };
  
  ageRef(P) is ref P.age;
  
  main() do {
    var A := 3;
    
    assign(ref A, A+2);
    
    assert A=5;
    
    apply(assign,ref A,7);
    
    assert A=7;
    
    P is someone{ name="fred"; age := 23 };
    
    assert P.age=23;
    
    P.age := 34;
    
    assert P.age = 34;
    
    assign(ref P.age,45);
    assert P.age = 45;
    
    apply(assign,ref P.age,56);
    
    assert P.age = 56;
    
    AR is ageRef(P);
    AR := 74;
    
    assert P.age=74;
    
    logMsg(info,"P is now $P");
  }
}