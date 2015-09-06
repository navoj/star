package org.star_lang.star.compiler.util;

import java.util.Iterator;

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
public class ConsList<T> implements PrettyPrintable, Iterable<T>
{
  private final T head;
  private final ConsList<T> tail;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final ConsList<?> nil = new ConsList(null, null) {

    @Override
    public int length()
    {
      return 0;
    }
  };

  public ConsList(T head, ConsList<T> tail)
  {
    this.head = head;
    this.tail = tail;
  }

  public ConsList<T> cons(T el)
  {
    return new ConsList<>(el, this);
  }

  public T head()
  {
    return head;
  }

  public ConsList<T> tail()
  {
    return tail;
  }

  @SuppressWarnings("unchecked")
  public ConsList<T> reverse()
  {
    return reverse(this, (ConsList<T>) nil);
  }

  private static <T> ConsList<T> reverse(ConsList<T> list, ConsList<T> soFar)
  {
    if (list.isNil())
      return soFar;
    else
      return reverse(list.tail(), cons(list.head, soFar));
  }

  public int length()
  {
    return tail.length() + 1;
  }

  public T find(Checker<T> chkr)
  {
    if (chkr.check(head))
      return head;
    else if (tail != null)
      return tail.find(chkr);
    else
      return null;
  }

  public interface Checker<T>
  {
    boolean check(T el);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    String sep = "";
    ConsList<T> list = this;

    while (list != nil) {
      disp.append(sep);
      sep = " ";
      if (list.head instanceof PrettyPrintable)
        ((PrettyPrintable) list.head).prettyPrint(disp);
      else
        disp.append(list.head.toString());
      list = list.tail;
    }
    disp.append(")");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @SuppressWarnings("unchecked")
  public static <T> ConsList<T> nil()
  {
    return (ConsList<T>) nil;
  }

  public static <T> ConsList<T> cons(T head, ConsList<T> tail)
  {
    return new ConsList<>(head, tail);
  }

  public boolean isNil()
  {
    return this == nil;
  }

  @Override
  public Iterator<T> iterator()
  {
    return new Iterator<T>() {
      ConsList<T> first = ConsList.this;

      @Override
      public boolean hasNext()
      {
        return !first.isNil();
      }

      @Override
      public T next()
      {
        ConsList<T> next = first;
        first = first.tail();
        return next.head();
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException("cannot remove from a Cons list");
      }
    };
  }
}
