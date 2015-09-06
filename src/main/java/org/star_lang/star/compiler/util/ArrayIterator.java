package org.star_lang.star.compiler.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

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
