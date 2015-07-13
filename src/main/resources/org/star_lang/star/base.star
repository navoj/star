/**
 * the base types and structures for the star standard.
 * imported directly only by the standard prelude packages themselves.
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
 */

-- Define some standard contracts
contract equality over t is {
  (=) has type (t,t) => boolean;
};

implementation equality over boolean is {
  fun true=true is true
   |  false=false is true
   |  _ = _ default is false
};

implementation equality over char is {
  fun char(X) = char(Y) is __char_eq(X,Y)
   |  _ = _ default is false
};

implementation equality over integer is {
  fun integer(X) = integer(Y) is __integer_eq(X,Y)
   |  _ = _ default is false
};

implementation equality over long is {
  fun long(X) = long(Y) is __long_eq(X,Y)
   |  _ = _ default is false
};

implementation equality over float is {
  fun float(X) = float(Y) is __float_eq(X,Y)
   |  _ = _ default is false
};

implementation equality over decimal is {
  fun decimal(X) = decimal(Y) is __decimal_eq(X,Y)
   |  _ = _ default is false
};

implementation equality over string is {
  fun string(X) = string(Y) is __string_eq(X,Y) 
   | _ = _ default is false
};

implementation equality over any is {
  fun X = Y is __equal(X,Y);
}

implementation equality over binary is {
  fun X=Y is __binary_equal(X,Y);
};

implementation equality over astLocation is {
  fun L=R is __equal(L,R);
};

implementation equality over %t default is {
  fun L=R is __equal(L,R);
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
  fun integer(X)<integer(Y) is __integer_lt(X,Y);
  fun integer(X)<=integer(Y) is __integer_le(X,Y);
  fun integer(X)>integer(Y) is __integer_gt(X,Y);
  fun integer(X)>=integer(Y) is __integer_ge(X,Y);
 }

implementation comparable over long is{
  fun long(X)<long(Y) is __long_lt(X,Y);
  fun long(X)<=long(Y) is __long_le(X,Y);
  fun long(X)>long(Y) is __long_gt(X,Y);
  fun long(X)>=long(Y) is __long_ge(X,Y);
}

implementation comparable over float is{
  fun float(X)<float(Y) is __float_lt(X,Y);
  fun float(X)<=float(Y) is __float_le(X,Y);
  fun float(X)>float(Y) is __float_gt(X,Y)
   |  _ > _ default is false;
  fun float(X)>=float(Y) is __float_ge(X,Y);
}

implementation comparable over decimal is{
  fun decimal(X)<decimal(Y) is __decimal_lt(X,Y);
  fun decimal(X)<=decimal(Y) is __decimal_le(X,Y);
  fun decimal(X)>decimal(Y) is __decimal_gt(X,Y);
  fun decimal(X)>=decimal(Y) is __decimal_ge(X,Y);
}

implementation comparable over char is{
  fun char(X)<char(Y) is __char_lt(X,Y);
  fun char(X)<=char(Y) is __char_le(X,Y);
  fun char(X)>char(Y) is __char_gt(X,Y);
  fun char(X)>=char(Y) is __char_ge(X,Y);
}

implementation comparable over string is{
  fun string(X)<string(Y) is __string_lt(X,Y);
  fun string(X)<=string(Y) is __string_le(X,Y);
  fun string(X)>string(Y) is __string_gt(X,Y);
  fun string(X)>=string(Y) is __string_ge(X,Y);
}

implementation equality over () is {
  fun () = () is true
   |  _ = _ default is false;
};

implementation comparable over () is {
  fun _ < _ is false;
  fun () <= () is true;
  fun _ > _ is false;
  fun ()>=() is true;
};

implementation equality over ((%l,%r) where equality over %l and equality over %r) is {
  (=) = pairEq;
} using {
  fun pairEq((L1,R1),(L2,R2)) is L1=L2 and R1=R2;
}

implementation comparable over ((%l,%r) where comparable over %l and equality over %l and comparable over %r and equality over %r) is {
  (<) = pairLt;
  (<=) = pairLe;
  (>) = pairGt;
  (>=) = pairGe;
} using {
  fun pairLt((L1,L2),(R1,R2)) where L1<R1 is true
   |  pairLt((L1,L2),(R1,R2)) where L1=R1 is L2<R2
   |  pairLt(_,_) default is false
  
  fun pairLe((L1,L2),(R1,R2)) where L1<R1 is true
   |  pairLe((L1,L2),(R1,R2)) where L1=R1 is L2<=R2
   |  pairLe(_,_) default is false
  
  fun pairGt(X,Y) is pairLt(Y,X);
  
  fun pairGe(X,Y) is pairLe(Y,X);
};

implementation equality over ((%l,%m,%r) where equality over %l and
 equality over %m and equality over %r) is {
  (=) = tripleEq;
} using {
  fun tripleEq((L1,M1,R1),(L2,M2,R2)) is L1=L2 and M1=M2 and R1=R2;
}

implementation comparable over ((%l,%m,%r) where comparable over %l and equality over %l 
                                              and comparable over %m and equality over %m
                                              and comparable over %r and equality over %r) is {
  (<) = tripleLt;
  (<=) = tripleLe;
  (>) = tripleGt;
  (>=) = tripleGe;
} using {
  fun tripleLt((L1,M1,R1),(L2,M2,R2)) where L1<L2 is true
   |  tripleLt((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1<M2 is true
   |  tripleLt((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1=M2 is R1<R2
   |  tripleLt(_,_) default is false
  
  fun tripleLe((L1,M1,R1),(L2,M2,R2)) where L1<=L2 is true
   |  tripleLe((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1<=M2 is true
   |  tripleLe((L1,M1,R1),(L2,M2,R2)) where L1=L2 and M1=M2 is R1<=R2
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
