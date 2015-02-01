/**
 * Implement a range operator 
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

private import base;
private import arithmetic;
private import iterable;
private import sequences;
private import strings;
private import casting;
private import folding;

type range of t where arithmetic over t and comparable over t is range(t,t,t);

implementation sizeable over range of %t where coercion over (%t,integer) is {
  isEmpty(range(F,T,_)) is F>=T;
  size(range(F,T,S)) is ((T-F)/S) as integer;
}

implementation iterable over range of %t determines %t is {
  _iterate(range(Fr,To,Stp),Fn,St) is iotaIterate(Fr,To,Stp,Fn,St);
}

private iotaIterate(Fr,To,Stp,Fn,S) is valof{
  var St := S;
  var Ix := Fr;
  
  while Ix/Stp<To/Stp do{
    if St matches NoMore(X) then
      valis NoMore(X) -- cannot just use St 'because of handling of computation expressions
    else
      St := Fn(Ix,St);
    Ix := Ix+Stp;
  }
  valis St;
}

implementation for all t such that concatenate over range of t where equality over t is {
  range(F,I,St)++range(I,To,St) is range(F,To,St);
}

implementation for all t such that sequence over range of t determines t where coercion over (integer,t) and equality over t is {
    _empty() from range(F,T,I) where F*I>=T*I
    _pair(F,range(F+I,T,I)) from range(F,T,I) where F*I<T*I;
    _cons(H,range(F,T,I)) where F*I>=T*I is range(H,H+I,I)
    _cons(H,range(F,T,I)) where F-I=H is range(H,T,I);
    _apnd(range(F,T,I),E) where F*I>=T*I is range(E,E+I,I)
    _apnd(range(F,T,I),E) where T+I=E is range(F,E,I);
    _back(range(F,E-I,I),E) from range(F,E,I);
    _nil() is range(0 as t,0 as t,1 as t);
  }

implementation for all t such that foldable over range of t determines t 
  where arithmetic over t and comparable over t is {
  leftFold(F,I,range(Fr,To,Inc)) is  valof{
    var r := Fr
    var limit is To*Inc
    var st := I;
    while r*Inc< limit do {
      st := F(st,r);
      r := r+Inc
    }
    valis st;
  };
    
  rightFold(F,I,range(Fr,To,Inc)) is  valof{
    var r := To
    var limit is Fr*Inc
    var st := I;
    while r*Inc > limit do {
      r := r-Inc
      st := F(r,st);
    }
    valis st;
  };
    
  leftFold1(F,range(Fr,To,Inc)) where Fr*Inc<To*Inc is leftFold(F,Fr,range(Fr+Inc,To,Inc))
  leftFold1(F,_) is raise "range is empty";
  

  rightFold1(F,range(Fr,To,Inc)) where Fr*Inc<To*Inc is rightFold(F,To,range(Fr,To-Inc,Inc))
  rightFold1(F,_) is raise "range is empty";
}

-- macro out common use cases ...
# #(for #(identifier?C)# in range(?S,?L,1) do ?A)# ==> {
  var C := S;
  while C < L do{
    A;
    C := C+1;
  }
};
# #(for #(identifier?C)# in range(?S,?L,?Stp) do ?A)# ==> {
  var C := S;
  while C/Stp < L/Stp do{
    A;
    C := C+Stp;
  }
};