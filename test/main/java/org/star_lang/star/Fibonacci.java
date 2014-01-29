package org.star_lang.star;

/**
 * nfib benchmarks function call overhead
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
