/**
 * 
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
diff is package{  
  contract hasDiff over %t is {
    diff has type (%t,%t) => ((%t)=>%t);
  }
  
  id(X) is X;
  
  K(X) is (function(_) is X);
  
 implementation hasDiff over integer is {
    diff = intDiff
  } using {
    intDiff(X,X) is id;
    intDiff(X1,X2) default is K(X2)
  };

  implementation hasDiff over (list of %t where hasDiff over %t 'n equality over %t) is{
    diff=listDiff
  } using {
    listDiff(X,X) is id; 
    listDiff(list{X;..L1},list{X;..L2}) is let{
      D is listDiff(L1,L2);
      differ(list{XX;..LL}) is list{XX;..D(LL)};
    } in differ;
    listDiff(list{X1;..L1},list{X2;..L2}) where X1!=X2 is 
      let{
        H is diff(X1,X2);
        D is listDiff(L1,L2);
        differ(list{A;..B}) is list{H(A);..D(B)};
      } in differ;
  }
  
  main() do {
    D1 is diff(list{1;3;3},list{1;2;3});
    
    logMsg(info,"d is $D1");
    logMsg(info,"apply to list{1;3;3} is $(D1(list{1;3;3}))");
    assert D1(list{1;3;3}) = list{1;2;3};
  }
}