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
actorOverload is package{
  contract FooAble over %t is {
    getFoo has type (%t) => integer;
  };

  implementation FooAble over integer is {
    getFoo(x) is 42;
  };

  type AA is alias of actor of {
    foo has type occurrence of integer;
    signaled has type ()=>boolean;
  };

  main() do {
    X has type AA;
    def X is actor {
      private var S := false;
      on elt on foo do {
        var y := getFoo(elt);
        S := true;
      };
      signaled() is S;
    };
    notify X with 1 on foo;
    assert query X with signaled();
  }
}