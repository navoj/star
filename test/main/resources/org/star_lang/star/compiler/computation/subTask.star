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
subTask is package{
  fun tt(X) is task{
    valis X+2;
  }
  
  fun uu(X) is task{
    logMsg(info,"We got $X");
    var Y := 1;
    Y:=Y+valof tt(3);
    valis X+Y;
  };
  
  prc main() do {    
    def YY is valof uu(3);
    assert YY=9;
  }
}
  