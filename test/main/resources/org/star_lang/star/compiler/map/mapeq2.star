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
mapeq2 is package{
  type content is clSymbol(string);
  main() do {
   h1 is dictionary of {clSymbol("bar")-> clSymbol("baz");
              clSymbol("foo")-> clSymbol("bar")};
   h2 is dictionary of {clSymbol("bar")-> clSymbol("baz");
              clSymbol("foo")-> clSymbol("bar")};
   logMsg(info, "$h1 = $h2 => $(h1 = h2)");
   assert h1=h2;

   h3 is dictionary of {"bar"-> "baz";
              "foo"-> "bar"};
   h4 is dictionary of {"bar"-> "baz";
              "foo"-> "bar"};
   logMsg(info, "$h3 = $h4 => $(h3 = h4)");
   assert h3=h4;
  };
}