package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
public class ArraySet<E> implements Set<E>, PrettyPrintable
{
  private final List<E> els;

  public ArraySet()
  {
    this(new ArrayList<E>());
  }

  public ArraySet(List<E> els)
  {
    this.els = els;
  }

  @Override
  public int size()
  {
    return els.size();
  }

  @Override
  public boolean isEmpty()
  {
    return els.isEmpty();
  }

  @Override
  public boolean contains(Object o)
  {
    return els.contains(o);
  }

  @Override
  public Iterator<E> iterator()
  {
    return els.iterator();
  }

  @Override
  public Object[] toArray()
  {
    return els.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    return els.toArray(a);
  }

  @Override
  public boolean add(E e)
  {
    return els.add(e);
  }

  @Override
  public boolean remove(Object o)
  {
    return els.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    return els.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c)
  {
    return els.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    return els.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    return els.removeAll(c);
  }

  @Override
  public void clear()
  {
    els.clear();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("{");
    int mark = disp.markIndent(2);
    String sep = "";
    for (E el : els) {
      disp.append(sep);
      sep = ", ";
      if (el instanceof PrettyPrintable)
        ((PrettyPrintable) el).prettyPrint(disp);
      else
        disp.append(el.toString());
    }
    disp.popIndent(mark);
    disp.append("}");
  }
}
