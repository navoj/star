package org.star_lang.star;

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
public class Tarai
{
  private static int tarai(int x, int y, int z)
  {
    if (x <= y)
      return y;
    else
      return tarai(tarai(x - 1, y, z), tarai(y - 1, z, x), tarai(z - 1, x, y));
  }

  private static class Tak
  {
    private int one = 1;
    
    int enter(Tak fun, int x, int y, int z)
    {
      if (x <= y)
        return y;
      else
        return fun.enter(fun, fun.enter(fun, x - one, y, z), fun.enter(fun, y - one, z, x), fun.enter(
            fun, z - one, x, y));
    }
  }

  public static void main(String[] args)
  {
    long start = System.nanoTime();
    Tak tak = new Tak();
    long res = tak.enter(tak, 19, 13, 5);
    long nanos = System.nanoTime() - start;

    System.out.println("tak.tarai(19,13,5) = " + res + " in " + ((double) nanos) / 1.0e9
        + " seconds");

    start = System.nanoTime();
    res = tarai(19, 13, 5);
    long onanos = System.nanoTime() - start;
    System.out.println("tarai(19,13,5) = " + res + " in " + ((double) onanos) / 1.0e9
        + " nanoseconds");

    System.out.println("penalty is " + ((double) onanos) / nanos);
  }
}
