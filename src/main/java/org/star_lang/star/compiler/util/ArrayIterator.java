package org.star_lang.star.compiler.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

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
public class ArrayIterator<T> implements ListIterator<T>
{
  int ix = 0;
  int from;
  int limit;
  T[] data;

  public ArrayIterator(T[] data)
  {
    this.data = data;
    this.ix = 0;
    this.from = 0;
    this.limit = data.length;
  }

  public ArrayIterator(T[] data, int from, int to)
  {
    this.data = data;
    this.ix = from;
    this.from = from;
    this.limit = to;
  }

  @Override
  public boolean hasNext()
  {
    return ix < limit;
  }

  @Override
  public T next()
  {
    if (ix < limit)
      return data[ix++];
    else
      throw new NoSuchElementException("iterator past end of array");
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public void add(T e)
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public boolean hasPrevious()
  {
    return ix > from;
  }

  @Override
  public int nextIndex()
  {
    return ix;
  }

  @Override
  public T previous()
  {
    if (ix > from)
      return data[--ix];
    else
      throw new NoSuchElementException("iterator past end of array");
  }

  @Override
  public int previousIndex()
  {
    return ix - 1;
  }

  @Override
  public void set(T e)
  {
    throw new UnsupportedOperationException("not permitted");
  }

}
