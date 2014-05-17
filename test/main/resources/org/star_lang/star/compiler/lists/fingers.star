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
fingers is package{
  import reduceC;
  
  -- implement the 2-3 fingers tree data structure
  
  type fingerTree of %t is emptyFinger
    or singleFinger(%t)
    or deepTree(digit of %t,fingerTree of node23 of %t,digit of %t);
  
  type digit of %t is oneDigit(%t)
                   or twoDigit(%t,%t)
                   or threeDigit(%t,%t,%t)
                   or fourDigit(%t,%t,%t,%t);
  
  type node23 of %t is node2(%t,%t)
                    or node3(%t,%t,%t);
					
  implementation reduce over digit of %t determines %t is {
    reducer(f) is let{
      red(oneDigit(a),z) is f(a,z);
      red(twoDigit(a,b),z) is f(a,f(b,z));
      red(threeDigit(a,b,c),z) is f(a,f(b,f(c,z)));
      red(fourDigit(a,b,c,d),z) is f(a,f(b,f(c,f(d,z))));
    } in red;
    reducel(f) is let{
      red(z,oneDigit(a)) is f(z,a);
      red(z,twoDigit(a,b)) is f(f(z,a),b);
      red(z,threeDigit(a,b,c)) is f(f(f(z,a),b),c);
      red(z,fourDigit(a,b,c,d)) is f(f(f(f(z,a),b),c),d);
    } in red;
  }
                    
  implementation reduce over node23 of %t determines %t is {
    reducel(F) is let{
      red(z,node2(b,a)) is F(F(z,b),a);
	  red(z,node3(c,b,a)) is F(F(F(z,c),b),a);
	} in red;
	reducer(F) is let{
	  red(node2(a,b),z) is F(a,F(b,z));
	  red(node3(a,b,c),z) is F(a,F(b,F(c,z)));
	} in red;
  };
  
  -- These have to be here 'cos of scope rules.
  redl(F) is reducel(F);
  redr(F) is reducer(F);
  
  implementation reduce over fingerTree of %t determines %t is let{
    reducer(F) is let{
	  red(emptyFinger,z) is z;
	  red(singleFinger(x),z) is F(x,z);
	  red(deepTree(L,D,R),z) is let{
	    red1 is redr(F);
		red2 is redr(redr(F));
	  } in red1(L,red2(D,red1(R,z)));
	} in red;
	reducel(F) is let{
	  red(z,emptyFinger) is z;
	  red(z,singleFinger(x)) is F(z,x);
	  red(z,deepTree(L,D,R)) is let{
	    red1 is redl(F);
	    red2 is redl(redl(F));
	  } in red1(red2(red1(z,L),D),R);
    } in red;
  } in {reducer=reducer; reducel=reducel};
}