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
except is package{
  -- Test exception handling  
  first(cons(H,_)) is H;
  
  simpleExcept() do
  {
    try{
      A is first(nil); -- Should raise an exception
      logMsg(info,"A is $A");
    } catch {
      logMsg(info,"Had an exception");
    }
    logMsg(info,"end simple except");
  }
  
  simpleNoExcept() do
  {
    try{
      A is first(cons of {"alpha"}); -- Should not raise an exception
      logMsg(info,"A is $A");
    } catch {
      logMsg(info,"Had an exception");
    }
    logMsg(info,"end simple noexcept");
  }
  
  main() do
  {
    simpleExcept();
    simpleNoExcept();
  }
  
}    