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
typealias is package{
  -- test out some of the type aliasing stuff

  type foo of %t is foo(string,%t);

  type bar is alias of foo of integer;

  type jar of %t is alias of foo of list of %t;

  F1 has type (foo of integer) => integer;
  F1(foo(_,X)) is X;

  F2 has type (bar) => integer;
  F2(foo(_,X)) is X;

  XX has type jar of integer;
  XX is foo("hi",list of [1,2,3]);

  FF has type (jar of %s) =>%s;
  FF(foo(_,list of [X])) is X;

  main has type action();
  main() do {
    logMsg(info,"$(FF(foo("",list of [2])))");
  };
}