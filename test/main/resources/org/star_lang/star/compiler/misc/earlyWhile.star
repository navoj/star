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
earlyWhile is package {

  type List of %a is Nil or Cons(%a, List of %a);

  prc loop1() do {
    var l := Cons(1, Cons(2, Cons(3, Nil)));
    while ((l matches Cons(head, tail)) and true) do {
      l := tail;
      logMsg(info, "loop1: l=$(__display(l))");
    };
    assert l=Nil;
  }

  prc loop2() do {
    var l := Cons(1, Cons(2, Cons(3, Nil)));
    while (l matches Cons(head, tail)) do {
      l := tail;
      logMsg(info, "loop2: l=$(__display(l))");
    }
    assert l=Nil;
  }

  prc main() do {
    loop1();
    loop2();
  }
}
