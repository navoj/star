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
fact is package{
 /* three kinds of factorial, this one is float */
  fact has type (float)=>float;
  fact(0.0) is 1.0;
  
  fact(N) where N>0.0 is N*fact(N-1.0);
         -- a line comment

  fct has type (integer)=>integer;-- and one is integer
  fct(0) is 1;
  fct(N) where N>0 is N*fct(N-1);
  
  -- And this one is generic
  factorial(N) where N=zero is one;
  factorial(N) default is N*factorial(N-one);
  
  
}