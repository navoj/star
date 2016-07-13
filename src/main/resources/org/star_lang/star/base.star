/**
 * the base types and structures for the star standard.
 * imported directly only by the standard prelude packages themselves.
 *
 * Copyright (c) 2015. Francis G. McCabe
 *
 * The TypeChecker implements the type inference module for Star
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


-- Define some standard contracts
contract equality over t is {
  (=) has type (t,t) => boolean;
  hashCode has type (t)=>integer;
};

implementation equality over boolean is {
  fun true=true is true
   |  false=false is true
   |  _ = _ default is false
  fun hashCode(true) is 1
   |  hashCode(false) is 0
};

implementation equality over integer is {
  fun integer(X) = integer(Y) is __integer_eq(X,Y)
   |  _ = _ default is false
  fun hashCode(X) is X
};

implementation equality over long is {
  fun long(X) = long(Y) is __long_eq(X,Y)
   |  _ = _ default is false
  fun hashCode(X) is integer(__hashCode(X))
};

implementation equality over float is {
  fun float(X) = float(Y) is __float_eq(X,Y)
   |  _ = _ default is false
  fun hashCode(X) is integer(__hashCode(X))
};

implementation equality over string is {
  fun string(X) = string(Y) is __string_eq(X,Y) 
   | _ = _ default is false
  fun hashCode(S) is integer(__hashCode(S))
};

implementation equality over any is {
  fun X = Y is __equal(X,Y);
  fun hashCode(X) is integer(__hashCode(X))
}

implementation equality over binary is {
  fun X=Y is __binary_equal(X,Y);
  fun hashCode(X) is integer(__hashCode(X))
};

implementation equality over astLocation is {
  fun L=R is __equal(L,R);
  fun hashCode(L) is integer(__hashCode(L))
};

implementation equality over %t default is {
  fun L=R is __equal(L,R);
  fun hashCode(L) is integer(__hashCode(L))
};

contract largeSmall over %t is {
  largest has type %t;
  smallest has type %t;
}

contract comparable over %t is {
  (<) has type (%t,%t) =>boolean;
  (=<) has type (%t,%t) =>boolean;
  (>) has type (%t,%t) =>boolean;
  (>=) has type (%t,%t) =>boolean;
}

implementation comparable over integer is{
  fun integer(X)<integer(Y) is __integer_lt(X,Y);
  fun integer(X)=<integer(Y) is __integer_le(X,Y);
  fun integer(X)>integer(Y) is __integer_gt(X,Y);
  fun integer(X)>=integer(Y) is __integer_ge(X,Y);
 }

implementation comparable over long is{
  fun long(X)<long(Y) is __long_lt(X,Y);
  fun long(X)=<long(Y) is __long_le(X,Y);
  fun long(X)>long(Y) is __long_gt(X,Y);
  fun long(X)>=long(Y) is __long_ge(X,Y);
}

implementation comparable over float is{
  fun float(X)<float(Y) is __float_lt(X,Y);
  fun float(X)=<float(Y) is __float_le(X,Y);
  fun float(X)>float(Y) is __float_gt(X,Y)
   |  _ > _ default is false;
  fun float(X)>=float(Y) is __float_ge(X,Y);
}

implementation comparable over string is{
  fun string(X)<string(Y) is __string_lt(X,Y);
  fun string(X)=<string(Y) is __string_le(X,Y);
  fun string(X)>string(Y) is __string_gt(X,Y);
  fun string(X)>=string(Y) is __string_ge(X,Y);
}

implementation equality over () is {
  fun () = () is true
   |  _ = _ default is false;
  fun hashCode(()) is 0
};

implementation comparable over () is {
  fun _ < _ is false;
  fun () =< () is true;
  fun _ > _ is false;
  fun ()>=() is true;
};

implementation equality over ((%l,%r) where equality over %l and equality over %r) is {
  (=) = pairEq;
  hashCode = pairHash
} using {
  fun pairEq((L1,R1),(L2,R2)) is L1=L2 and R1=R2;
  fun pairHash(L) is integer(__hashCode(L))
}

implementation comparable over ((%l,%r) where comparable over %l and equality over %l and comparable over %r and equality over %r) is {
  (<) = pairLt;
  (=<) = pairLe;
  (>) = pairGt;
  (>=) = pairGe;
} using {
  fun pairLt((L1,L2),(R1,R2)) where L1<R1 is true
   |  pairLt((L1,L2),(R1,R2)) where L1=R1 is L2<R2
   |  pairLt(_,_) default is false
  
  fun pairLe((L1,L2),(R1,R2)) where L1<R1 is true
   |  pairLe((L1,L2),(R1,R2)) where L1=R1 is L2=<R2
   |  pairLe(_,_) default is false
  
  fun pairGt(X,Y) is pairLt(Y,X);
  
  fun pairGe(X,Y) is pairLe(Y,X);
};

implementation equality over ((%l,%m,%r) where equality over %l and
 equality over %m and equality over %r) is {
  (=) = tripleEq;
  hashCode = tripleHash
} using {
  fun tripleEq((L1,M1,R1),(L2,M2,R2)) is L1=L2 and M1=M2 and R1=R2;
  fun tripleHash(L) is integer(__hashCode(L))
}

implementation comparable over ((%l,%m,%r) where comparable over %l and equality over %l 
                                              and comparable over %m and equality over %m
                                              and comparable over %r and equality over %r) is {
  (<) = tripleLt;
  (=<) = tripleLe;
  (>) = tripleGt;
  (>=) = tripleGe;
} using {
  fun tripleLt((L1,M1,R1),(L2,M2,R2)) where L1<L2 is true
   |  tripleLt((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1<M2 is true
   |  tripleLt((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1=M2 is R1<R2
   |  tripleLt(_,_) default is false
  
  fun tripleLe((L1,M1,R1),(L2,M2,R2)) where L1=<L2 is true
   |  tripleLe((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1=<M2 is true
   |  tripleLe((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1=M2 is R1=<R2
   |  tripleLe(_,_) default is false
  
  fun tripleGt(X,Y) is tripleLt(Y,X);
  
  fun tripleGe(X,Y) is tripleLe(Y,X);
};

-- handle sizeable
contract sizeable over t is {
  size has type (t) => integer;
  isEmpty has type (t) => boolean;
}

-- This function has a variety of uses ...
fun id(X) is X;
