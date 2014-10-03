package org.star_lang.star;

import org.star_lang.star.compiler.util.ConsList;

/**
 * Implement a benchmark involving the sieve of Erastosthenes
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
