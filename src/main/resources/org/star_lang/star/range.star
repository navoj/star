/**
 * Implement a range operator
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

private import base;
private import arithmetic;
private import iterable;
private import sequences;
private import strings;
private import casting;

type range of t where arithmetic over t 'n comparable over t is range(t,t,t);

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
  _concat(range(F,I,St),range(I,To,St)) is range(F,To,St);
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