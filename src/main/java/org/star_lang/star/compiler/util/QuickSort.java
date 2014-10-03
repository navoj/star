/**
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
package org.star_lang.star.compiler.util;

import java.util.Comparator;
import java.util.List;

public class QuickSort
{
  public static <T> List<T> quickSort(List<T> data, Comparator<T> comparator)
  {
    int limit = data.size();

    sort(data, 0, limit, comparator);
    return data;
  }

  private static <T> void sort(List<T> data, int from, int limit, Comparator<T> comparator)
  {
    if (from < limit) {
      int pivotIx = partition(data, from, limit, (limit + from) / 2, comparator);
      sort(data, from, pivotIx, comparator);
      sort(data, pivotIx + 1, limit, comparator);
    }
  }

  private static <T> int partition(List<T> data, int from, int limit, int pivotIx, Comparator<T> comparator)
  {
    int stIx = from;

    T pivot = data.get(pivotIx);
    int end = limit - 1;
    swap(data, pivotIx, end); // move to end

    for (int ix = from; ix < end; ix++) {
      if (comparator.compare(data.get(ix), pivot) < 0)
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
