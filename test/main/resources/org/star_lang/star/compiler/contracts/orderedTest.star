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
orderedTest is package {

  type ordering is lt or gt or eq;

  contract ordered over %t where equality over %t is {
    compare has type (%t, %t) => ordering;
    minimum has type (%t, %t) => %t;
    fun minimum(x, y) default is
	  switch compare(x, y) in {
	    case lt is x;
	    case eq is x;
	    case gt is y;
	  };
    maximum has type (%t, %t) => %t;
    fun maximum(x, y) default is
 	  switch compare(x, y) in {
	    case lt is y;
	    case eq is x;
	    case gt is x;
	  };
  };
  
  implementation ordered over integer is {
    fun compare(X,X) is eq
     |  compare(X,Y) where X>Y is gt
     |  compare(_,_) default is lt
  };

  foo has type (%t) => boolean where ordered over %t;
  fun foo(x) is x = x;

  prc main() do {
    assert minimum(3,4)=3;
  }
}