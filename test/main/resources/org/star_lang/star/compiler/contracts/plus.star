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
plus is package{
  contract pluss over (l,r) determines s is {
    plus has type (l,r)=>s;
  };
  
  implementation pluss over (integer,integer) determines integer is {
    plus(integer(L),integer(R)) is integer(__integer_plus(L,R));
  }
  
  implementation pluss over (integer,long) determines long is {
    plus(integer(L),long(R)) is long(__long_plus(__integer_long(L),R));
  }
  
  implementation pluss over (long,integer) determines long is {
    plus(long(L),integer(R)) is long(__long_plus(L,__integer_long(R)));
  }
  
  implementation pluss over (integer,float) determines float is {
      plus(integer(L),float(R)) is float(__float_plus(__integer_float(L),R));
  }
  
  implementation pluss over (float,float) determines float is {
      plus(float(L),float(R)) is float(__float_plus(L,R));
  }
}