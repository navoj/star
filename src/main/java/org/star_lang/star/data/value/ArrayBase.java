package org.star_lang.star.data.value;

import java.util.Collection;

/**
 * The ArrayBase is used to contain the actual elements -- potentially of many arrays.
 */


import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.IValue;

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
@SuppressWarnings("serial")
public class ArrayBase implements PrettyPrintable
{
  private final IValue[] data;
  private int firstUsed;
  private int firstFree;

  public ArrayBase(int initSize)
  {
    data = new IValue[initSize];
    firstUsed = 0;
    firstFree = 0;
  }

  public ArrayBase(IValue data[], int firstUsed, int lastUsed)
  {
    this.data = data;
    this.firstUsed = firstUsed;
    this.firstFree = lastUsed;
  }

  public ArrayBase(Collection<? extends IValue> values, int overhang)
  {
    int size = values.size();
    this.data = new IValue[size + overhang * 2];
    int ix = overhang;
    for (IValue cell : values)
      data[ix++] = cell;
    this.firstUsed = overhang;
    this.firstFree = firstUsed + size;
  }

  public IValue getCell(int ix)
  {
    if (ix < firstUsed || ix >= firstFree)
      throw new IndexOutOfBoundsException((ix - firstUsed) + " not in range");
    else
      return data[ix];
  }

  public void setCell(int ix, IValue value)
  {
    if (ix < firstUsed || ix >= firstFree)
      throw new IndexOutOfBoundsException((ix - firstUsed) + " not in range");
    else
      data[ix] = value;
  }

  public IValue[] data()
  {
    return data;
  }

  public int firstUsed()
  {
    return firstUsed;
  }

  public int lastUsed()
  {
    return firstFree;
  }

  public int limit()
  {
    return data.length - 1;
  }

  public void append(IValue cell)
  {
    assert firstFree + 1 <= data.length;
    data[firstFree++] = cell;
  }

  public void appendAll(Collection<IValue> cells)
  {
    assert firstFree + cells.size() < data.length;
    for (IValue cell : cells)
      data[firstFree++] = cell;
  }

  public void prepend(IValue cell)
  {
    assert firstUsed > 0 && cell != null;
    data[--firstUsed] = cell;
  }

  public ArrayBase remove(int where)
  {
    IValue data[] = new IValue[this.data.length];
    for (int ix = firstUsed; ix < where; ix++)
      data[ix] = this.data[ix];
    for (int ix = where + 1; ix < firstFree; ix++)
      data[ix - 1] = this.data[ix];
    return new ArrayBase(data, firstUsed, firstFree - 1);
  }

  public ArrayBase cloneBase(int from, int to, int overhang)
  {
    assert to >= from;

    IValue data[] = new IValue[to - from + 2 * overhang];
    int off = overhang - from;
    for (int ix = from; ix < to; ix++)
      data[ix + off] = this.data[ix];
    return new ArrayBase(data, overhang, overhang + to - from);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("[");
    String sep = "";
    int mark = disp.markIndent(2);
    for (int ix = firstUsed; ix < firstFree; ix++) {
      disp.append(sep);
      sep = ", ";
      if (data[ix] instanceof PrettyPrintable)
        ((PrettyPrintable) data[ix]).prettyPrint(disp);
      else
        disp.append(data[ix].toString());
    }
    disp.popIndent(mark);
    disp.append("]");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
