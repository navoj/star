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
doubleSel is package{
  fun foo(X) is valof{
    logMsg(info,"foo of $X");
    valis X+2;
  }
  
  fun bar(A) is switch foo(A) in {
    case X matching 4 is X;
    case _ default is -1
  };
  
  prc main() do {
    logMsg(info,"bar = $(bar(2))");
    assert bar(2)=4;
  }
}