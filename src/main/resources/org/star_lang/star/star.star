/**
 * Standard Star prelude 
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

import base;         -- stuff that is core to all star base
import validate;     -- Standard validation rules
import formatter;
import macrosupport; -- Support for compiled macros

-- Standard macros

import updateable;
import arithmetic;
import casting;
import iterable;
import actors;
import arrays;
import queue;
import cons;
import dateNtime;
import sequences;
import strings;
import folding;
import threads;
import maps;
import compute;
import quoteable;
import range;
import option;
import worksheet;

-- used in processing ordered queries
contract _project0 over %%c is {
  __project0 has type for all k, v such that (%%c of ((k,v))) => %%c of k;
  __unique has type for all e such that (%%c of e,(e,e)=>boolean) => %%c of e;
}

implementation _project0 over list is {
  __project0(A) is __array_project_0(A);
  __unique(A,E) is __array_unique(A,E);
}

contract iotaC over (%%r,%t) is {
  iota has type (%t,%t,%t) => %%r of %t
};

implementation iotaC over (list,integer) is {
  iota(integer(F),integer(T),integer(S)) is __integer_array_iota(F,T,S);
}

implementation iotaC over (list,long) is {
  iota(long(F),long(T),long(S)) is __long_array_iota(F,T,S);
}

implementation iotaC over (list,float) is {
  iota(float(F),float(T),float(S)) is __float_array_iota(F,T,S);
}

implementation iotaC over (cons,integer) is {
  iota(F,T,S) where S>0 is iotaF(F,T,S);
  iota(F,T,S) default is iotaB(F,T,S);
  
  private iotaF(F,T,S) where F>T is nil;
  iotaF(F,T,S) is cons(F,iotaF(F+S,T,S));
  
  private iotaB(F,T,S) where F<T is nil;
  iotaB(F,T,S) is cons(F,iotaB(F+S,T,S));
}

-- macro out common use case ...
# #(for #(identifier?C)# in iota(?S,?L,?St) do ?A)# ==> {
  var C := S;
  while C<= L do{
    A;
    C := C+St;
  }
};