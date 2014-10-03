package org.star_lang.star.compiler.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
    return new Singleton<S>(data);
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
    return new SingleIterator<E>(data);
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
