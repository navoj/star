package org.star_lang.star;

/**
 * nfib benchmarks function call overhead
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
 * 
 */

public class Fibonacci
{

  // native nfib
  public static long nfib(long N)
  {
    if (N < 2)
      return 1;
    else
      return nfib(N - 1) + nfib(N - 2) + 1;
  }

  // native fib
  private static long fib(long N)
  {
    if (N < 2)
      return 1;
    else
      return fib(N - 1) + fib(N - 2);
  }

  static int ifib(int n)
  {
    if (n == 0)
      return 0;
    if (n == 1)
      return 1;

    int prevPrev = 0;
    int prev = 1;
    int result = 0;

    for (int i = 2; i <= n; i++) {
      result = prev + prevPrev;
      prevPrev = prev;
      prev = result;
    }
    return result;
  }

  // nfib in the projected style of Cafe
  private interface IFun
  {
    long enter(long arg);
  }

  private static class NFib implements IFun
  {
    IFun free;

    @Override
    public long enter(long N)
    {
      if (N < 2)
        return 1;
      else
        return free.enter(N - 1) + free.enter(N - 2) + 1;
    }
  }

  public static void main(String[] args)
  {
    long arg = Long.parseLong(args[0]);
    long start = System.nanoTime();
    long res = fib(arg);
    long nanos = System.nanoTime() - start;

    System.out.println("nfib(" + arg + ") = " + res + " in " + (nanos / 1.0e9) + " seconds");
    System.out.println("call time = " + (double) nanos / res);

    long arithStart = System.nanoTime();
    long discard = 0;

    for (long ix = 0; ix < res; ix++)
      discard = discard + ix + 1;
    long arithNanos = System.nanoTime() - arithStart;

    System.out.println("arithmetic time is " + (arithNanos / 1.0e9) + " seconds");
    System.out.println("adjusted call time = " + (double) (nanos - arithNanos) / res);
    NFib nfib = new NFib();
    nfib.free = nfib;

    start = System.nanoTime();
    res = nfib.enter(arg);
    nanos = System.nanoTime() - start;

    System.out.println("nfib(" + arg + ") = " + res + " in " + (nanos / 1.0e9) + " seconds");
    System.out.println("call time = " + (double) nanos / res);
    System.out.println("adjusted call time = " + (double) (nanos - arithNanos) / res);
  }
}
