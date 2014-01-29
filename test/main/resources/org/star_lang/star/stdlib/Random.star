
Random is package {
import Prelude
-- |
-- Module      :  System.Random
 -- Copyright   :  (c) The University of Glasgow 2001
-- License     :  BSD-style (see the file libraries/base/LICENSE)
-- 
-- Maintainer  :  libraries@haskell.org
-- Stability   :  stable
-- Portability :  portable
--
-- This library deals with the common task of pseudo-random number
-- generation. The library makes it possible to generate repeatable
-- results, by starting with a specified initial random number generator,
-- or to get different results on each run by using the system-initialised
-- generator or by supplying a seed from some other source.
--
-- The library is split into two layers: 
--
-- * A core /random number generator/ provides a supply of bits.
--   The class 'RandomGen' provides a common interface to such generators.
--   The library provides one instance of 'RandomGen', the abstract
--   data type 'StdGen'.  Programmers may, of course, supply their own
--   instances of 'RandomGen'.
--
-- * The class 'Random' provides a way to extract values of a particular
--   type from a random number generator.  For example, the 'Float'
--   instance of 'Random' allows one to generate random values of type
--   'Float'.
--
-- This implementation uses the Portable Combined Generator of L'Ecuyer
-- ["System.Random\#LEcuyer"] for 32-bit computers, transliterated by
-- Lennart Augustsson.  It has a period of roughly 2.30584e18.
--

-- | The class 'RandomGen' provides a common interface to random number
-- generators.
--
-- Minimal complete definition: 'next' and 'split'.

contract RandomGen of %g is {

   -- |The 'next' operation returns an 'Int' that is uniformly distributed
   -- in the range returned by 'genRange' (including both end points),
   -- and a new generator.
   next has type (%g) => (integer, %g);

   -- |The 'split' operation allows one to obtain two distinct random number
   -- generators. This is very useful in functional programs (for example, when
   -- passing a random number generator down to recursive calls), but very
   -- little work has been done on statistically robust implementations of
   -- 'split' (["System.Random\#Burton", "System.Random\#Hellekalek"]
   -- are the only examples we know of).
   split has type (%g) => (%g, %g)

   -- |The 'genRange' operation yields the range of values returned by
   -- the generator.
   --
   -- It is required that:
   --
   -- * If @(a,b) = 'genRange' g@, then @a < b@.
   --
   -- * 'genRange' always returns a pair of defined 'Int's.
   --
   -- The second condition ensures that 'genRange' cannot examine its
   -- argument, and hence the value it returns can be determined only by the
   -- instance of 'RandomGen'.  That in turn allows an implementation to make
   -- a single call to 'genRange' to establish a generator's range, without
   -- being concerned that the generator returned by (say) 'next' might have
   -- a different range to the generator passed to 'next'.
   --
   -- The default definition spans the full range of 'Int'.
   genRange has type (%g) => (integer, integer)

   -- default method
--   genRange(_) is (-1000, 1000) -- ####
}
/*	
/* |
The 'StdGen' instance of 'RandomGen' has a 'genRange' of at least 30 bits.

The result of repeatedly using 'next' should be at least as statistically
robust as the /Minimal Standard Random Number Generator/ described by
["System.Random\#Park", "System.Random\#Carta"].
Until more is known about implementations of 'split', all we require is
that 'split' deliver generators that are (a) not identical and
(b) independently robust in the sense just given.

The 'Show' and 'Read' instances of 'StdGen' provide a primitive way to save the
state of a random number generator.
It is required that @'read' ('show' g) == g@.

In addition, 'read' may be used to map an arbitrary string (not necessarily one
produced by 'show') onto a value of type 'StdGen'. In general, the 'read'
instance of 'StdGen' has the following properties: 

* It guarantees to succeed on any string. 

* It guarantees to consume only a finite portion of the string. 

* Different argument strings are likely to result in different results.

*/

type StdGen is StdGen (integer, integer);

/*
COMPILER BUG
implementation display of stdGen is {
  display = displayStdGen;
} using {
  displayStdGen(stdGen(s1, s2)) is display(s1) + display(s2);
};
*/

/*
instance Read StdGen where
  readsPrec _p = \ r ->
     case try_read r of
       r'@[_] -> r'
       _   -> [stdFromString r] -- because it shouldn't ever fail.
    where 
      try_read r = do
         (s1, r1) <- readDec (dropWhile isSpace r)
	 (s2, r2) <- readDec (dropWhile isSpace r1)
	 return (StdGen s1 s2, r2)

{-
 If we cannot unravel the StdGen from a string, create
 one based on the string given.
-}
stdFromString         :: String -> (StdGen, String)
stdFromString s        = (mkStdGen num, rest)
	where (cs, rest) = splitAt 6 s
              num        = foldl (\a x -> x + 3 * a) 1 (map ord cs)


{- |
The function 'mkStdGen' provides an alternative way of producing an initial
generator, by mapping an 'Int' into a generator. Again, distinct arguments
should be likely to produce distinct generators.
-}
mkStdGen :: Int -> StdGen -- why not Integer ?
mkStdGen s = mkStdGen32 $ fromIntegral s

mkStdGen32 :: Int32 -> StdGen
mkStdGen32 s
 | s < 0     = mkStdGen32 (-s)
 | otherwise = StdGen (s1+1) (s2+1)
      where
	(q, s1) = s `divMod` 2147483562
	s2      = q `mod` 2147483398

createStdGen :: Integer -> StdGen
createStdGen s = mkStdGen32 $ fromIntegral s

-- FIXME: 1/2/3 below should be ** (vs@30082002) XXX

*/

/* |
With a source of random number supply in hand, the 'Random' class allows the
programmer to extract random values of a variety of types.

Minimal complete definition: 'randomR' and 'random'.

*/

contract Random of %a is {
  -- | Takes a range /(lo,hi)/ and a random number generator
  -- /g/, and returns a random value uniformly distributed in the closed
  -- interval /[lo,hi]/, together with a new generator. It is unspecified
  -- what happens if /lo>hi/. For continuous types there is no requirement
  -- that the values /lo/ and /hi/ are ever produced, but they may be,
  -- depending on the implementation and the interval.
  randomR has type (%a, %a, %g) => (%a, %g) where RandomGen over %g;

  -- | The same as 'randomR', but using a default range determined by the type:
  --
  -- * For bounded types (instances of 'Bounded', such as 'Char'),
  --   the range is normally the whole type.
  --
  -- * For fractional types, the range is normally the semi-closed interval
  -- @[0,1)@.
  --
  -- * For 'Integer', the range is (arbitrarily) the range of 'Int'.
  random has type (%g) => (%a, %g) where RandomGen over %g;
};


/*

instance Random Char where
  randomR (a,b) g = 
      case (randomIvalInteger (toInteger (ord a), toInteger (ord b)) g) of
        (x,g') -> (chr x, g')
  random g	  = randomR (minBound,maxBound) g


mkStdRNG :: Integer -> IO StdGen
mkStdRNG o = do
    ct          <- getCPUTime
    (sec, psec) <- getTime
    return (createStdGen (sec * 12345 + psec + ct + o))

*/

randomIvalInteger has type (integer, integer, %g) => (integer, %g) where RandomGen over %g
randomIvalInteger(l, h, rng) where l > h is randomIvalInteger (h, l, rng)
randomIvalInteger(l, h, rng) default is
	let {
		k is h - l + 1
        b is 2147483561
        n is iLogBase(b, k)

        f(0, acc, g) is (acc, g)
        f(nʼ, acc, g) is
           	let {
	   			(x, gʼ) is next(g)
	  		} in
	  			f(nʼ - 1, x + acc * b, gʼ);
	  			
	  	(v, rngʼ) is f(n, 1, rng)
	} in
		(l + v % k, rngʼ)

iLogBase has type (integer, integer) => integer
iLogBase(b, i) is
	 (i < b) ? 1 | 1 + iLogBase(b, i / b)

min32Bound is -2**32;
max32Bound is 2**32-1;
int32Range is max32Bound - min32Bound

implementation Random over integer is {
	randomR = randomIvalInteger;
	random = (function (g) is randomIvalInteger(min32Bound, max32Bound, g)); -- had to pick something
};

randomIvalFloat has type (float, float, %g) => (float, %g) where RandomGen over %g
randomIvalFloat(l, h, rng) where l > h is randomIvalFloat(h, l, rng)
randomIvalFloat(l, h, rng) default is
	let {
		(x, rngʼ) is randomIvalInteger(min32Bound, max32Bound, rng);
		scaled_x is (l+h)/2 + ((h-l) / int32Range) * x;
	} in (scaled_x, rngʼ)

implementation Random over float is {
	randomR = randomIvalFloat;
	random = (function (g) is randomIvalFloat(0.0, 1.0, g));
}

implementation Random over boolean is {
	randomR = boolRandomR;
	random = boolRandom;
} using {
  boolRandomR(a, b, g) is
  	let {
  		bool2Int(false) is 0;
  		bool2Int(true) is 1;
  		int2Bool(0) is false;
  		int2Bool(_) default is true;
  		(x, gʼ) is randomIvalInteger(bool2Int(a), bool2Int(b), g) 
  	} in (int2Bool(x), gʼ)
  boolRandom(g) is boolRandomR(false, true, g)
};

/* ####
 * This works around two bugs in Star:
 * - inverse constructors are not implemented
 * - In "P is E" - P can't be a constructor pattern for an algebraic datatype, 
 * only tuples work.
 */
unpackStdGen(StdGen(t1, t2)) is (t1, t2);

implementation RandomGen over StdGen is {
	next = stdNext;
	split = stdSplit;
	genRange = stdRange;
} using {
	stdRange has type (StdGen) => (integer, integer);
	stdRange(_) is (0, 2147483562);

	stdNext has type (StdGen) => (integer, StdGen);
	-- Returns values in the range stdRange
	stdNext (StdGen(s1, s2))  is (zʼ, StdGen(s1ʼʼ, s2ʼʼ))
		using {	
			zʼ is (z < 1 ? z + 2147483562 | z);
			z  is s1ʼʼ - s2ʼʼ;
	
			k    is s1 / 53668;
			s1ʼ  is 40014 * (s1 - k * 53668) - k * 12211;
			s1ʼʼ is (s1ʼ < 0 ? s1ʼ + 2147483563 | s1ʼ);
	    
			kʼ   is s2 / 52774;
			s2ʼ  is 40692 * (s2 - kʼ * 52774) - kʼ * 3791;
			s2ʼʼ is (s2ʼ < 0 ? s2ʼ + 2147483399 | s2ʼ);
		};
		
	stdSplit has type (StdGen) => (StdGen, StdGen);
	stdSplit(std) where std matches StdGen(s1, s2) is
	  (left, right)
	 	using {
	 	  -- no statistical foundation for this!
	      new_s1 is (s1 = 2147483562 ? 1 | s1 + 1);
	      new_s2 is (s2 = 1 ? 2147483398 | s2 - 1);
	      (t1, t2) is unpackStdGen(snd(stdNext(std)));
	      left  is StdGen(new_s1, t2);
	      right is StdGen(t1, new_s2);
	    }; 
	};

/*


-- The global random number generator

{- $globalrng #globalrng#

There is a single, implicit, global random number generator of type
'StdGen', held in some global variable maintained by the 'IO' monad. It is
initialised automatically in some system-dependent fashion, for example, by
using the time of day, or Linux's kernel random number generator. To get
deterministic behaviour, use 'setStdGen'.
-}

-- |Sets the global random number generator.
setStdGen :: StdGen -> IO ()
setStdGen sgen = writeIORef theStdGen sgen

-- |Gets the global random number generator.
getStdGen :: IO StdGen
getStdGen  = readIORef theStdGen

theStdGen :: IORef StdGen
theStdGen  = unsafePerformIO $ do
   rng <- mkStdRNG 0
   newIORef rng

-- |Applies 'split' to the current global random generator,
-- updates it with one of the results, and returns the other.
newStdGen :: IO StdGen
newStdGen = atomicModifyIORef theStdGen split

{- |Uses the supplied function to get a value from the current global
random generator, and updates the global generator with the new generator
returned by the function. For example, @rollDice@ gets a random integer
between 1 and 6:

>  rollDice :: IO Int
>  rollDice = getStdRandom (randomR (1,6))

-}

getStdRandom :: (StdGen -> (a,StdGen)) -> IO a
getStdRandom f = atomicModifyIORef theStdGen (swap . f)
  where swap (v,g) = (g,v)

{- $references

1. FW #Burton# Burton and RL Page, /Distributed random number generation/,
Journal of Functional Programming, 2(2):203-212, April 1992.

2. SK #Park# Park, and KW Miller, /Random number generators -
good ones are hard to find/, Comm ACM 31(10), Oct 1988, pp1192-1201.

3. DG #Carta# Carta, /Two fast implementations of the minimal standard
random number generator/, Comm ACM, 33(1), Jan 1990, pp87-88.

4. P #Hellekalek# Hellekalek, /Don\'t trust parallel Monte Carlo/,
Department of Mathematics, University of Salzburg,
<http://random.mat.sbg.ac.at/~peter/pads98.ps>, 1998.

5. Pierre #LEcuyer# L'Ecuyer, /Efficient and portable combined random
number generators/, Comm ACM, 31(6), Jun 1988, pp742-749.

The Web site <http://random.mat.sbg.ac.at/> is a great source of information.

-}
*/
}