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
anonFun is package {
  fun foo() is let {
    def bar is () => 3;
  } in bar;
  
  fun ffoo() is let{
    fun bar() is 3;
  } in bar;
  
  fun inc(X) is (Y) => X+Y;
 
  prc main() do {
    logMsg(info,"foo is $(foo()())");
    logMsg(info,"ffoo is $(ffoo()())");
    
    assert inc(3)(2)=5;
  }
}