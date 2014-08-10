package org.star_lang.star.operators.arrays.runtime;

import java.util.List;

import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.Factory;

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
public class ValueSort
{
  public static List<IValue> quickSort(List<IValue> data, IFunction comparator) throws EvaluationException
  {
    int limit = data.size();

    sort(data, 0, limit, comparator);
    return data;
  }

  private static void sort(List<IValue> data, int from, int limit, IFunction comparator) throws EvaluationException
  {
    if (from < limit) {
      int pivotIx = partition(data, from, limit, (limit + from) / 2, comparator);
      sort(data, from, pivotIx, comparator);
      sort(data, pivotIx + 1, limit, comparator);
    }
  }

  private static int partition(List<IValue> data, int from, int limit, int pivotIx, IFunction comparator)
      throws EvaluationException
  {
    int stIx = from;

    IValue pivot = data.get(pivotIx);
    int end = limit - 1;
    swap(data, pivotIx, end); // move to end

    for (int ix = from; ix < end; ix++) {
      if (Factory.boolValue(comparator.enter(data.get(ix), pivot)))
        swap(data, stIx++, ix);
    }
    swap(data, stIx, end); // put pivot in right place

    return stIx;
  }

  private static <T> void swap(List<T> data, int lft, int rgt)
  {
    if (lft != rgt) {
      T el = data.get(lft);
      data.set(lft, data.get(rgt));
      data.set(rgt, el);
    }
  }
}
