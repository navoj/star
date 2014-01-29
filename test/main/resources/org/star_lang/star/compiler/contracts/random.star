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
random is package{

  contract RandomGen over %g is {
    next has type (%g) => (integer, %g);
  }
  
  type StdGen is StdGen (integer, integer);

  implementation pPrint over StdGen is {
	ppDisp = dispStdGen;
  } using {
	dispStdGen(StdGen(s1,s2)) is ppSequence(0,cons of {ppDisp(s1);ppStr("~");ppDisp(s2)});
  }

  contract Random over (%a,%g) where RandomGen over %g is {
    randomR has type (%a, %a, %g) => (%a, %g);
  };

  randomIvalInteger has type (integer, integer, %g) => (integer, %g) where RandomGen over %g;
  randomIvalInteger(l, h, rng) where l > h is randomIvalInteger (h, l, rng)
  randomIvalInteger(l, h, rng) default is
	let {
		k is h - l + 1
        b is 2147483561
        n is iLogBase(b, k)

        f(0, acc, g) is (acc, g)
        f(n1, acc, g) is
           	let {
	   			(x, g1) is next(g)
	  		} in
	  			f(n1 - 1, x + acc * b, g1);
	  			
	  	(v, rng1) is f(n, 1, rng)
	} in
		(l + v % k, rng1)

  iLogBase has type (integer, integer) => integer
  iLogBase(b, i) is
	 (i < b) ? 1 | 1 + iLogBase(b, i / b)

  min32Bound is -2**32;
  max32Bound is 2**32-1;
  int32Range is max32Bound - min32Bound

  implementation Random over (integer,StdGen) is {
	randomR(lo, hi, g) is randomIvalInteger(lo, hi, g);
  };

  implementation RandomGen over StdGen is {
	next = stdNext;
  } using {
	stdRange has type (StdGen) => (integer, integer);
	stdRange(_) is (0, 2147483562);

	stdNext has type (StdGen) => (integer, StdGen);
	-- Returns values in the range stdRange
	stdNext (StdGen(s1, s2))  is (z1, StdGen(s1_2, s2_2))
		using {	
			z1 is (z < 1 ? z + 2147483562 | z);
			z  is s1_2 - s2_2;
	
			k    is s1 / 53668;
			s1_1  is 40014 * (s1 - k * 53668) - k * 12211;
			s1_2 is (s1_1 < 0 ? s1_1 + 2147483563 | s1_1);
	    
			k1   is s2 / 52774;
			s2_1  is 40692 * (s2 - k1 * 52774) - k1 * 3791;
			s2_2 is (s2_1 < 0 ? s2_1 + 2147483399 | s2_1);
		};
  };
}
