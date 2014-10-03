package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
    iterators = new ArrayList<Iterator<T>>();
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
