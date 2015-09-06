package org.star_lang.star.compiler.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

@SuppressWarnings("serial")
public class Singleton<E> implements List<E>, PrettyPrintable
{
  private final E data;

  public Singleton(E data)
  {
    this.data = data;
  }

  public static <S> List<S> create(S data)
  {
    return new Singleton<>(data);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("[");
    if (data instanceof PrettyPrintable)
      ((PrettyPrintable) data).prettyPrint(disp);
    else
      disp.append(data.toString());

    disp.append("]");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public boolean add(E e)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public void add(int index, E element)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public boolean addAll(Collection<? extends E> c)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public void clear()
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public boolean contains(Object o)
  {
    return data.equals(o);
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    for (Object cx : c)
      if (!contains(cx))
        return false;
    return true;
  }

  @Override
  public E get(int index)
  {
    if (index == 0)
      return data;
    else
      throw new ArrayIndexOutOfBoundsException(index);
  }

  @Override
  public int indexOf(Object o)
  {
    if (data.equals(o))
      return 0;

    return -1;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  @Override
  public Iterator<E> iterator()
  {
    return new SingleIterator<>(data);
  }

  @Override
  public int lastIndexOf(Object o)
  {
    return indexOf(o);
  }

  @Override
  public ListIterator<E> listIterator()
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public ListIterator<E> listIterator(int index)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public boolean remove(Object o)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public E remove(int index)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public E set(int index, E element)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public int size()
  {
    return 1;
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex)
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public Object[] toArray()
  {
    return new Object[] { data };
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    throw new UnsupportedOperationException("not implemented");
  }

}
