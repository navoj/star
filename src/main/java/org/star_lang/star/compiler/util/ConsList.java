package org.star_lang.star.compiler.util;

import java.util.Iterator;

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
