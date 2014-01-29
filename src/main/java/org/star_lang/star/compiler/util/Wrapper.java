package org.star_lang.star.compiler.util;

/**
 * To force a way around a restriction in Java's anonymous classes
 * 
 * @param <T>
 * 
 * Copyright (C) 2013 Starview Inc
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
public class Wrapper<T> implements PrettyPrintable
{
  private T el;

  public Wrapper(T el)
  {
    this.set(el);
  }

  public static <T> Wrapper<T> create(T el)
  {
    return new Wrapper<T>(el);
  }

  public void set(T el)
  {
    this.el = el;
  }

  public T get()
  {
    return el;
  }

  public boolean isEmpty()
  {
    return el == null;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("{");
    if (el instanceof PrettyPrintable) {
      ((PrettyPrintable) el).prettyPrint(disp);
    } else if (el != null)
      disp.append(el.toString());
    else
      disp.append("null");
    disp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
