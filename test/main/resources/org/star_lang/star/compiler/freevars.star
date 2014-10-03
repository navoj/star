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
freevars is package{

  outer has type (integer,integer) =>integer;
  outer(A,B) is let{
  
    inner has type (integer) =>integer;
    inner(X) is A+X;
    
    further has type (integer) =>integer;
    further(X) is let{
      inn has type (integer) =>integer;
      inn(U) is inner(U*B);
    } in inn(X);
    
  } in further(A);
  
  main has type action();
  main() do {
    logMsg(warning, "$(outer(10,4))");
  };

}