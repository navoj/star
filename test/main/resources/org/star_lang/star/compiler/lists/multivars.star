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
multivars is package{
  -- Test the simultaneous declaration and assignment of variables
 
  prc main() do {
    logMsg(info,"split of $testLists on element 3 is $((Ls,Rs))");
  };
  
  def testLists is list of [1,2,3,4,5,6];
  
  def (Ls,Rs) is split(testLists,3);
  
  split has type (list of %t,integer) => (list of %t, list of %t);
  fun split(L,Count) is let{
    splitter has type (list of %t, list of %t, integer) => (list of %t, list of %t)
    fun splitter(Ll,R,0) is (reverse(R,list of []), Ll)
     |  splitter(list of [E,..Ll],R,Cx) is splitter(Ll,list of [E,..R],Cx-1)
  } in splitter(L,list of [],Count);
  
  reverse has type (list of %t,list of %t) => list of %t;
  fun reverse(list of [],R) is R
   |  reverse(list of [E,..L],R) is reverse(L,list of [E,..R])
}