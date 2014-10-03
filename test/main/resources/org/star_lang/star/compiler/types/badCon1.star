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
badCon1 is package {
  contract foo over %%c is {
    fool has type for all a,b such that ((a, b) => b, %%c of a, b) => b;
  };

  bar has type (%%c of %a) => cons of %a where foo over %%c;
  bar(s) is f1(s, nil) using {
    -- f1 has type (%c,%%c of %a) => cons of %a;
    f1(a0,b0) is fool((function(a,b) is cons(a, b)), a0, b0);
  };
}