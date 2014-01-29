package org.star_lang.star.array;

import org.junit.Test;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.value.Array;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.SkewList;

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
public class JavaArrayTests
{
  @Test
  public void testSkew() throws EvaluationException
  {
    int elCount = 100000;
    SkewList list = SkewList.empty();
    final IValue i0 = Factory.newInt(elCount);

    long skewStart = System.nanoTime();
    for (int ix = 0; ix < elCount; ix++)
      list = list.consCell(Factory.newInt(elCount - ix));
    long skewCount = System.nanoTime() - skewStart;

    final SkewList skew = list;

    long skewIndexCount = measure(new Tester() {
      @Override
      public void test(long elCount) throws EvaluationException
      {
        for (int ix = 0; ix < elCount; ix++) {
          IValue el = skew.getCell(ix);
          assert Factory.intValue(el) == ix + 1;
        }
      }
    }, elCount);

    // System.out.println(skew);

    long skewReverseStart = System.nanoTime();
    skew.reverse();
    long skewRevCount = System.nanoTime() - skewReverseStart;

    long skewUpdateStart = System.nanoTime();
    for (int ix = 0; ix < elCount / 2; ix++) {
      list = (SkewList) list.substituteCell(ix, i0);
    }
    long skewUpdateCount = System.nanoTime() - skewUpdateStart;

    long skewIterStart = System.nanoTime();
    for (IValue el : list) {
      assert el != null;
    }
    long skewIterCount = System.nanoTime() - skewIterStart;

    long skewConcatStart = System.nanoTime();
    list.concatenate(list);
    long skewContCount = System.nanoTime() - skewConcatStart;

    Double dbl = new Double();

    long skewMapStart = System.nanoTime();
    list = list.mapOver(dbl);
    long skewDbleCount = System.nanoTime() - skewMapStart;

    Array array = Array.nilArray;

    long arrayStart = System.nanoTime();
    for (int ix = 0; ix < elCount; ix++)
      array = array.consCell(Factory.newInt(elCount - ix));
    long arrayCount = System.nanoTime() - arrayStart;

    long arrayIndexStart = System.nanoTime();
    for (int ix = 0; ix < elCount; ix++) {
      IValue el = array.getCell(ix);
      assert Factory.intValue(el) == ix + 1;
    }
    long arrayIndexCount = System.nanoTime() - arrayIndexStart;

    long arrayReverseStart = System.nanoTime();
    array.reverse();
    long arrayRevCount = System.nanoTime() - arrayReverseStart;

    long arrayUpdateStart = System.nanoTime();
    for (int ix = 0; ix < elCount / 2; ix++) {
      array = (Array) array.substituteCell(ix, i0);
    }
    long arrayUpdateCount = System.nanoTime() - arrayUpdateStart;

    long arrayIterStart = System.nanoTime();
    for (IValue el : array) {
      assert el != null;
    }
    long arrayIterCount = System.nanoTime() - arrayIterStart;

    long arrayConcatStart = System.nanoTime();
    array = (Array) array.concat(array);
    long arrayContCount = System.nanoTime() - arrayConcatStart;

    long arrayMapStart = System.nanoTime();
    array = (Array) array.mapOver(dbl);
    long arrayDbleCount = System.nanoTime() - arrayMapStart;

    System.out
        .println("     type  :     build    :   getting    :    update    :   iterate    :      map     :    concat     :   reverse");
    String format = "%10s :  %10d  :  %10d  :  %10d  :  %10d  :  %10d  :  %10d   :   %10d";
    System.out.println(String.format(format, "skew", skewCount, skewIndexCount, skewUpdateCount, skewIterCount,
        skewDbleCount, skewContCount, skewRevCount));
    System.out.println(String.format(format, "array", arrayCount, arrayIndexCount, arrayUpdateCount, arrayIterCount,
        arrayDbleCount, arrayContCount, arrayRevCount));
  }

  private interface Tester
  {
    void test(long amount) throws EvaluationException;
  }

  private static long measure(Tester test, long amount) throws EvaluationException
  {
    long start = System.nanoTime();
    test.test(amount);
    return System.nanoTime() - start;
  }

  private static class Double implements IFunction
  {
    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }

    @Override
    public IType getType()
    {
      // TODO Auto-generated method stub
      return null;
    }

  }
}
