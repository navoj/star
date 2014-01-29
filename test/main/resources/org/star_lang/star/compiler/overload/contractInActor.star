/**
 * 
 * Copyright (C) 2013 Starview Inc
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
contractInActorTest is package {
 contract C1 over %t is {
   cFun has type (%t) => %t;
 }

 implementation C1 over integer is {
   cFun(x) is x + 1;
 }

 X is memo actor {
   aFun(x) do {
     y is cFun(x);
     logMsg(info, "$y");
   }
 }

 main() do {
   request X()'s aFun to aFun(1);
 }
}