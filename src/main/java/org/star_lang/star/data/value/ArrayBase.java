package org.star_lang.star.data.value;

import java.util.Collection;

/**
 * The ArrayBase is used to contain the actual elements -- potentially of many arrays.
 */


import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.IValue;

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
