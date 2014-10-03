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
anonFunInActor is package {
 AA is actor {
   fn1(x, y, f) is f(x, y);
   act1(x, act) do act(x);
 }
 main() do {
   c is 3;
   x is query AA's fn1 with fn1(1, 1, fn(a,b) => a+b*c);
   request AA's act1 to act1("hello", let{ proc(cx) do logMsg(info, cx)} in proc);
   assert x = 4;
 }
}