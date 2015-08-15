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
Person is package{
  type Person is someone { 
    name has type string;
    -- name default is "someone belonging to $spouse";
    spouse has type Person;
    spouse default is noone;
    
    gender has type gender;
    gender default is male;
    
    age has type float;
    age default is nonFloat;
  } or noone;
  
  type gender is male or female;
  
  implementation comparable over Person is {
    (<) = Person_less;
    (=<) = Person_le;
    (>) = Person_gt;
    (>=) = Person_ge;
  } using {
    fun Person_less(noone,someone{}) is true
     |  Person_less(someone{name=N1},someone{name=N2}) is N1<N2
     |  Person_less(_,_) default is false
    
    fun Person_le(X,X) is true
     |  Person_le(X,Y) default is Person_less(X,Y)
    
    fun Person_gt(X,Y) is Person_less(Y,X)
    
    fun Person_ge(X,Y) is Person_le(Y,X);
  }
  
  implementation equality over Person is {
    (=) = Person_eq;
  } using {
    fun Person_eq(someone{name=N1},someone{name=N2}) is N1=N2
     |  Person_eq(noone,noone) is true
     |  Person_eq(_,_) default is false;
  }
}