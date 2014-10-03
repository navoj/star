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
foo is package {
  type one of %a is None or One(%a);
  
  foo has type (one of integer)=>boolean;
  foo(None) is false;
  foo(One(x)) is true;

  main() do {
    z1 is foo(One(0));
    logMsg(info, "z1 is $z1");

    z2 is foo(None);
    logMsg(info, "z2 is $z2");      
  }
}