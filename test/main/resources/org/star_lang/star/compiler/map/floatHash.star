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
floatHash is package{
  prc main () do {
    def a is dictionary of [17000.0 -> 1.81];
    def b is dictionary of [17000.0 -> 1.81];
    assert a=b;
    assert a=a;
    assert 17000.0 = 17000.0;
    assert 1.81 = 1.81;

    def c is dictionary of [170000 -> 181];
    def d is dictionary of [170000 -> 181];
    assert c=d;
  }
}