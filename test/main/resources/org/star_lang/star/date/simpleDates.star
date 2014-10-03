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
simpleDates is package{
  -- test simple dates and coercion
  
  N is now();
  T is today();
  
  main() do {
    logMsg(info,"now is #N");
    logMsg(info,"diff is $(timeDiff(N,T))");
    
    logMsg(info,"now is $(__display(T))");
    logMsg(info,"now is $(__display(T as string))");
    logMsg(info,"now is $(__display((T as string) as date))");
    
    assert ((T as string) as date) = T
    
    logMsg(info,"today is #T");
  }
}