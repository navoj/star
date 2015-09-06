package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

public class ComboIterator<T> implements Iterator<T>
{
  private final List<Iterator<T>> iterators;
  private int ix = 0;

  public ComboIterator(List<Iterator<T>> iterators)
  {
    this.iterators = iterators;
    checkNext();
  }

  public ComboIterator(Collection<? extends Iterable<T>> list)
  {
    iterators = new ArrayList<>();
    for (Iterable<T> el : list)
      iterators.add(el.iterator());

    checkNext();
  }
  
  @SafeVarargs
  public ComboIterator(Iterator<T>... iterators)
  {
    this(FixedList.create(iterators));
  }

  private boolean checkNext()
  {
    while (ix < iterators.size() && !iterators.get(ix).hasNext()) {
      ix++;
    }
    return ix < iterators.size();
  }

  @Override
  public boolean hasNext()
  {
    while (ix < iterators.size() && !iterators.get(ix).hasNext())
      ix++;

    return ix < iterators.size() && iterators.get(ix).hasNext();
  }

  @Override
  public T next()
  {
    if (ix < iterators.size()) {
      T next = iterators.get(ix).next();
      checkNext();
      return next;
    } else
      throw new IllegalAccessError();
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException("not permitted");
  }
}
