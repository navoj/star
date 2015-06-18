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
defltTest is package{
  positive has type (integer) <= integer;
  ptn positive(N) from N where N > 0;
	
  firstNChars has type (integer, string) => string;
  fun firstNChars(positive(N), S) where S matches`(.:C)(.*:restS)` is C ++ firstNChars(N - 1, restS)
   |  firstNChars(_, _) default is "";

  prc main() do let {
    def fstNCh is firstNChars(10, "abcdefghi");
  } in logMsg(info, "fstNCh is $fstNCh"); 
    
}
