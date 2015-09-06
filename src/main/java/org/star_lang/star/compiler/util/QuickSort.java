package org.star_lang.star.compiler.util;

import java.util.Comparator;
import java.util.List;

/*
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
