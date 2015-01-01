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
actorupdate is package {

  type Foo is foo {
    b has type list of string;
  };

  main() do {
    X is let {
      type FooActor is alias of actor of {
        Y has type occurrence of integer;
        f has type ref list of Foo;
      }
      aoeu has type FooActor;

      aoeu is actor {
        var f := list of {
	      foo{b is list of {"fo";"bar"};};
	      foo{b is list of {"foo";"baz"};};
	    };
        on elt on Y do {
	      update (x matching foo{b=B} where "foo" in B) in f with x substitute{b=list of {}};
	      logMsg(info, "$f");
	    };
      }
    } in aoeu;
    notify X with 1 on Y;
  }
}