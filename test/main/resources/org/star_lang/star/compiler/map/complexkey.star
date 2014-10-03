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
complexkey is package{
  type foo of %t is nf or foo(%t);
  
  main() do {
    var K := dictionary of {};
    
    K[foo(3)] := "aleph";
    assert K[foo(3)]="aleph"
    
    K[foo(4)] := "beta";
    assert K[foo(3)]="aleph"
    assert K[foo(4)]="beta"
    
    logMsg(info,"$K");
    
    K[foo(3)] := "garb";
    
    logMsg(info,"$K");
    assert K[foo(3)]="garb"
  }
}