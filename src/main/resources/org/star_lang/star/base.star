/**
 * the base types and structures for the star standard.
 * imported directly only by the standard prelude packages themselves.
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

-- Define some standard contracts
contract equality over t is {
  (=) has type (t,t) => boolean;
};

implementation equality over boolean is {
  true=true is true;
  false=false is true;
  _ = _ default is false;
};

implementation equality over char is {
  char(X) = char(Y) is __char_eq(X,Y);
  nonChar = nonChar is true;
  _ = _ default is false;
};

implementation equality over integer is {
  integer(X) = integer(Y) is __integer_eq(X,Y);
  nonInteger=nonInteger is true;
  _ = _ default is false;
};

implementation equality over long is {
  long(X) = long(Y) is __long_eq(X,Y);
  nonLong = nonLong is true;
  _ = _ default is false;
};

implementation equality over float is {
  float(X) = float(Y) is __float_eq(X,Y);
  nonFloat = nonFloat is true;
  _ = _ default is false;
};

implementation equality over decimal is {
  decimal(X) = decimal(Y) is __decimal_eq(X,Y);
  nonDecimal = nonDecimal is true;
  _ = _ default is false;
};

implementation equality over string is {
  string(X) = string(Y) is __string_eq(X,Y);
  nonString = nonString is true;
  _ = _ default is false;
};

implementation equality over any is {
  X = Y is __equal(X,Y);
}

implementation equality over binary is {
  X=Y is __binary_equal(X,Y);
};

implementation equality over astLocation is {
  L=R is __equal(L,R);
};

implementation equality over %t default is {
  L=R is __equal(L,R);
};

contract largeSmall over %t is {
  largest has type %t;
  smallest has type %t;
}

contract comparable over %t is {
  (<) has type (%t,%t) =>boolean;
  (<=) has type (%t,%t) =>boolean;
  (>) has type (%t,%t) =>boolean;
  (>=) has type (%t,%t) =>boolean;
}

implementation comparable over integer is{
  integer(X)<integer(Y) is __integer_lt(X,Y);
  integer(X)<=integer(Y) is __integer_le(X,Y);
  integer(X)>integer(Y) is __integer_gt(X,Y);
  integer(X)>=integer(Y) is __integer_ge(X,Y);
 }

implementation comparable over long is{
  long(X)<long(Y) is __long_lt(X,Y);
  long(X)<=long(Y) is __long_le(X,Y);
  long(X)>long(Y) is __long_gt(X,Y);
  long(X)>=long(Y) is __long_ge(X,Y);
}

implementation comparable over float is{
  float(X)<float(Y) is __float_lt(X,Y);
  float(X)<=float(Y) is __float_le(X,Y);
  float(X)>float(Y) is __float_gt(X,Y);
  _ > _ default is false;
  float(X)>=float(Y) is __float_ge(X,Y);
}

implementation comparable over decimal is{
  decimal(X)<decimal(Y) is __decimal_lt(X,Y);
  decimal(X)<=decimal(Y) is __decimal_le(X,Y);
  decimal(X)>decimal(Y) is __decimal_gt(X,Y);
  decimal(X)>=decimal(Y) is __decimal_ge(X,Y);
}

implementation comparable over char is{
  char(X)<char(Y) is __char_lt(X,Y);
  char(X)<=char(Y) is __char_le(X,Y);
  char(X)>char(Y) is __char_gt(X,Y);
  char(X)>=char(Y) is __char_ge(X,Y);
}

implementation comparable over string is{
  string(X)<string(Y) is __string_lt(X,Y);
  string(X)<=string(Y) is __string_le(X,Y);
  string(X)>string(Y) is __string_gt(X,Y);
  string(X)>=string(Y) is __string_ge(X,Y);
}

implementation equality over () is {
  () = () is true;
  _ = _ default is false;
};

implementation comparable over () is {
  _ < _ is false;
  () <= () is true;
  _ > _ is false;
  ()>=() is true;
};

implementation equality over ((%l,%r) where equality over %l 'n equality over %r) is {
  (=) = pairEq;
} using {
  pairEq((L1,R1),(L2,R2)) is L1=L2 and R1=R2;
}

implementation comparable over ((%l,%r) where comparable over %l 'n equality over %l 'n comparable over %r 'n equality over %r) is {
  (<) = pairLt;
  (<=) = pairLe;
  (>) = pairGt;
  (>=) = pairGe;
} using {
  pairLt((L1,L2),(R1,R2)) where L1<R1 is true;
  pairLt((L1,L2),(R1,R2)) where L1=R1 is L2<R2;
  pairLt(_,_) default is false;
  
  pairLe((L1,L2),(R1,R2)) where L1<R1 is true;
  pairLe((L1,L2),(R1,R2)) where L1=R1 is L2<=R2;
  pairLe(_,_) default is false;
  
  pairGt(X,Y) is pairLt(Y,X);
  
  pairGe(X,Y) is pairLe(Y,X);
};

implementation equality over ((%l,%m,%r) where equality over %l 'n
 equality over %m 'n equality over %r) is {
  (=) = tripleEq;
} using {
  tripleEq((L1,M1,R1),(L2,M2,R2)) is L1=L2 and M1=M2 and R1=R2;
}

implementation comparable over ((%l,%m,%r) where comparable over %l 'n equality over %l 
                                              'n comparable over %m 'n equality over %m
                                              'n comparable over %r 'n equality over %r) is {
  (<) = tripleLt;
  (<=) = tripleLe;
  (>) = tripleGt;
  (>=) = tripleGe;
} using {
  tripleLt((L1,M1,R1),(L2,M2,R2)) where L1<L2 is true;
  tripleLt((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1<M2 is true;
  tripleLt((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1=M2 is R1<R2;
  tripleLt(_,_) default is false;
  
  tripleLe((L1,M1,R1),(L2,M2,R2)) where L1<=L2 is true;
  tripleLe((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1<=M2 is true;
  tripleLe((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1=M2 is R1<=R2;
  tripleLe(_,_) default is false;
  
  tripleGt(X,Y) is tripleLt(Y,X);
  
  tripleGe(X,Y) is tripleLe(Y,X);
};

-- handle sizeable
contract sizeable over t is {
  size has type (t) => integer;
  isEmpty has type (t) => boolean;
}

-- This function has a variety of uses ...
id(X) is X;
