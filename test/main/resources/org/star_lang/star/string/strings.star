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
strings is package{
  conc has type (string,string) =>string;
  conc(A,B) is A++B;
  
  foo has type () => string;
  foo() is let{
    AA is "one";
    BB is "two";
  } in 
   valof{
     logMsg(info,"conc(AA,BB)=$(conc(AA,BB))");
     valis AA;
   }
  
  main has type action();
  main() do {
    logMsg(info,"strings test: $(foo())");
    assert conc("one","two")="onetwo";
  };
}
