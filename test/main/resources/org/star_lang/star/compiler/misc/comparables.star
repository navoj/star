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
comparables is package {
    foo has type (%a, %a)=>%a where comparable over %a;
    fun foo(x, y) is (x>=y) ? x : bar(y,x);

    bar has type (%a,%a)=>%a where comparable over %a; 
    fun bar(y, x) is foo(y,x);

    prc main() do {
      logMsg(info, "$(foo(1,2))");
      assert foo(1,2) = 2;
      logMsg(info, "$(foo(2,1))");
      assert foo(2,1) = 2;
      logMsg(info, "$(foo(1,1))");
      assert foo(1,1) = 1;
    }
}