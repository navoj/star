/**
 * Standard Star prelude
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
import sets;
import compute;
import quoteable;
import range;
import option;
import worksheet;
import concurrency;

-- used in processing ordered queries
contract _project0 over %%c is {
  __project0 has type for all k, v such that (%%c of ((k,v))) => %%c of k;
  __unique has type for all e such that (%%c of e,(e,e)=>boolean) => %%c of e;
}

implementation _project0 over list is {
  fun __project0(A) is __array_project_0(A);
  fun __unique(A,E) is __array_unique(A,E);
}

contract iotaC over (%%r,%t) is {
  iota has type (%t,%t,%t) => %%r of %t
};

implementation iotaC over (list,integer) is {
  fun iota(integer(F),integer(T),integer(S)) is __integer_array_iota(F,T,S);
}

implementation iotaC over (list,long) is {
  fun iota(long(F),long(T),long(S)) is __long_array_iota(F,T,S);
}

implementation iotaC over (list,float) is {
  fun iota(float(F),float(T),float(S)) is __float_array_iota(F,T,S);
}

implementation iotaC over (cons,integer) is {
  fun iota(F,T,S) where S>0 is iotaF(F,T,S)
   |  iota(F,T,S) default is iotaB(F,T,S);
  
  private
  fun iotaF(F,T,S) where F>T is nil
   |  iotaF(F,T,S) is cons(F,iotaF(F+S,T,S));
  
  private
  fun iotaB(F,T,S) where F<T is nil
   |  iotaB(F,T,S) is cons(F,iotaB(F+S,T,S));
}

-- macro out common use case ...
# #(for #(identifier?C)# in iota(?S,?L,?St) do ?A)# ==> {
  var C := S;
  while C<= L do{
    A;
    C := C+St;
  }
};