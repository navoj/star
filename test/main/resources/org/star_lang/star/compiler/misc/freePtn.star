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
freePtn is package{
  -- test references to free variables in pattern abstractions
  
  find(X,cons(U1,cons(U2,cons(U3,cons(Y,nil))))) is search((pattern(XX) from XX where XX=Y),X);
  
  search(Ptn,cons of {H;..T}) where H matches Ptn(XX) is XX;
  search(Ptn,cons of {_;..T}) is search(Ptn,T);
  
  main() do {
    XX is find(cons of {"one";"two";"three"},cons of {"alpha";"beta";"gamma";"two"});
    logMsg(info,"XX=$XX");
    assert XX="two";
  }
}