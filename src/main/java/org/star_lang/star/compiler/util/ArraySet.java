package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
public class ArraySet<E> implements Set<E>, PrettyPrintable
{
  private final List<E> els;

  public ArraySet()
  {
    this(new ArrayList<>());
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

  @SuppressWarnings("SuspiciousToArrayCall")
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
