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
dependtype is package{
  type foo of %t where bar over %t is nofuss(%t);
  
  contract bar over %t is {
    bore has type (%t)=>string;
  }
  
  implementation bar over string is {
    bore(X) is X;
  }
  
  alpha has type (foo of %t) => string;
  alpha(nofuss(X)) is bore(X);
  
  main() do
  {
    assert alpha(nofuss("fred"))="fred";
  }
}