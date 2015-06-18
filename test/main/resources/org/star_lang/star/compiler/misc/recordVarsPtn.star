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
recordVarsPtn is package {
  type Foo is foo {
    a has type integer;
    b has type integer;
  };

  type Tree is node1 {
    c has type integer;
  } or node2 {
    d has type integer;
  };

  x has type (integer, Tree) => (Foo, integer);
  fun x(y, node1{c=z}) is (foo{a=1;b=2}, z)
   |  x(y, node2{d=z}) is let {
        def (foo{a=bb;b=cc}, aa) is x(y, node1{c=42})
        def newZ is aa;
      } in (foo{a=1;b=2}, newZ);
  
  prc main() do {
    def dd is node1{def c is 30};
    def ee is node2{def d is 31};
    assert x(2, dd) = (foo{a=1;b=2}, 30);
    assert x(3, ee) = (foo{a=1;b=2}, 42);
  }

}