package org.star_lang.star;

import org.star_lang.star.compiler.util.ConsList;

/**
 * Implement a benchmark involving the sieve of Erastosthenes
 *
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

public class Erastosthenes
{

  private static ConsList<Integer> filter(ConsList<Integer> nums, Integer prime)
  {
    if (nums.isNil())
      return nums;
    else {
      Integer first = nums.head();
      if (first % prime == 0)
        return filter(nums.tail(), prime);
      else
        return filter(nums.tail(), prime).cons(first);
    }
  }

  private static ConsList<Integer> sieve(ConsList<Integer> nums)
  {
    if (!nums.isNil()) {
      Integer first = nums.head();
      nums = nums.tail();
      ConsList<Integer> primes = sieve(filter(nums, first));
      return primes.cons(first);
    } else
      return ConsList.nil();
  }

  private static ConsList<Integer> iota(Integer first, Integer last, Integer step)
  {
    if (first > last)
      return ConsList.nil();
    else {
      ConsList<Integer> iota = iota(first + step, last, step);
      return iota.cons(first);
    }
  }

  @SuppressWarnings("unused")
  private static ConsList<Integer> erastosthenes(Integer max)
  {
    ConsList<Integer> ints = iota(3, max, 2);
    ConsList<Integer> primes = sieve(ints);
    return primes;
  }

  private static void nullProc(ConsList<Integer> data)
  {

  }

  private static void run(ConsList<Integer> data)
  {
    sieve(data);
  }

  private static double bench(ConsList<Integer> data, int Runs)
  {
    long empty = System.nanoTime();
    for (int ix = 0; ix < Runs; ix++) {
      nullProc(data);
    }
    empty = System.nanoTime() - empty;
    System.out.println("empty time " + empty);
    long start = System.nanoTime();
    for (int ix = 0; ix < Runs; ix++) {
      run(data);
    }
    return ((double) System.nanoTime() - start - empty) / Runs;
  }

  public static void main(String args[])
  {
    ConsList<Integer> data = iota(3, 1000, 2);
    long count = sieve(data).length();
    double nanos = bench(data, 1000);
    double nanosPerPrime = nanos / count;

    System.out.println(count + " primes in " + nanos + " at " + nanosPerPrime + " nanos/prime");
  }
}
