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
otherwiseTest is package {

  type T1 is T1 {
    a has type integer;
    b has type integer;
    c has type integer;
  };

  fun Q1(t1, t2, t3) is all T1{a=A;b=B;c=C} where T1{a=A} in t1 and
                        (T1{a=A;b=B} in t2 otherwise 1 matches B) and 
                        (T1{a=A;c=C} in t3 otherwise 1 matches C);

  prc main() do {
    def t1s is list of [T1{a=1;b=2;c=1},T1{a=3;b=0;c=2}];
    def t2s is list of [T1{a=1;b=3;c=3},T1{a=2;b=4;c=4}];
    def t3s is list of [T1{a=1;b=3;c=5},T1{a=2;b=4;c=6}];
    def res is Q1(t1s, t2s, t3s);
    logMsg(info, "$res");
  }
}