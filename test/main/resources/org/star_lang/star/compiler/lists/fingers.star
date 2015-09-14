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
    fun reducer(f) is let{
      fun red(oneDigit(a),z) is f(a,z)
       |  red(twoDigit(a,b),z) is f(a,f(b,z))
       |  red(threeDigit(a,b,c),z) is f(a,f(b,f(c,z)))
       |  red(fourDigit(a,b,c,d),z) is f(a,f(b,f(c,f(d,z))))
    } in red;
    fun reducel(f) is let{
      fun red(z,oneDigit(a)) is f(z,a)
       |  red(z,twoDigit(a,b)) is f(f(z,a),b)
       |  red(z,threeDigit(a,b,c)) is f(f(f(z,a),b),c)
       |  red(z,fourDigit(a,b,c,d)) is f(f(f(f(z,a),b),c),d)
    } in red;
  }
                    
  implementation reduce over node23 of %t determines %t is {
    fun reducel(F) is let{
      fun red(z,node2(b,a)) is F(F(z,b),a)
	   |  red(z,node3(c,b,a)) is F(F(F(z,c),b),a)
	} in red;
	fun reducer(F) is let{
	  fun red(node2(a,b),z) is F(a,F(b,z))
	   |  red(node3(a,b,c),z) is F(a,F(b,F(c,z)))
	} in red;
  };
  
  -- These have to be here 'cos of scope rules.
  fun redl(F) is reducel(F)
  fun redr(F) is reducer(F)
  
  implementation reduce over fingerTree of %t determines %t is let{
    fun reducer(F) is let{
	  fun red(emptyFinger,z) is z
	   |  red(singleFinger(x),z) is F(x,z)
	   |  red(deepTree(L,D,R),z) is let{
	        def red1 is redr(F);
		    def red2 is redr(redr(F));
	      } in red1(L,red2(D,red1(R,z)));
	} in red;
	fun reducel(F) is let{
	  fun red(z,emptyFinger) is z
	   |  red(z,singleFinger(x)) is F(z,x)
	   |  red(z,deepTree(L,D,R)) is let{
	        def red1 is redl(F)
	        def red2 is redl(redl(F))
	      } in red1(red2(red1(z,L),D),R);
    } in red;
  } in {reducer=reducer; reducel=reducel};
}