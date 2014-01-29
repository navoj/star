package org.star_lang.star.compiler.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
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


public class SingleIterator<T> implements Iterator<T>
{
  private T value;

  public SingleIterator(T value)
  {
    this.value = value;
  }

  @Override
  public boolean hasNext()
  {
    return value!=null;
  }

  @Override
  public T next()
  {
    if(value!=null){
      T val = value;
      value = null;
      return val;
    }
    throw new NoSuchElementException();
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}
