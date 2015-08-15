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
person is package{
  type person is someone { 
    name has type string;
    -- name default is "someone belonging to $spouse";
    spouse has type person;
    spouse default is noone;
    
    gender has type gender;
    gender default is male;
  } or noone;
  
  type gender is male or female;
  
  implementation comparable over person is {
    (<) = person_less;
    (=<) = person_le;
    (>) = person_gt;
    (>=) = person_ge;
  } using {
    fun person_less(noone,someone{}) is true
     |  person_less(someone{name=N1},someone{name=N2}) is N1<N2
     |  person_less(_,_) default is false
    
    fun person_le(X,X) is true
     |  person_le(X,Y) default is person_less(X,Y)
    
    fun person_gt(X,Y) is person_less(Y,X)
    fun person_ge(X,Y) is person_le(Y,X)
  }
  
  implementation equality over person is {
    (=) = person_eq;
  } using {
    fun person_eq(someone{name=N1},someone{name=N2}) is N1=N2
     |  person_eq(noone,noone) is true
     |  person_eq(_,_) default is false
  }
}