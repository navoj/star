package org.star_lang.star.data.value;

import java.util.Collection;
import java.util.Iterator;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.ArrayIterator;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

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
public class Array implements IArray, PrettyPrintable
{
  public static final String label = "list";

  private final ArrayBase base;
  private final int first;
  private final int last;

  private int hash;

  public Array(ArrayBase base, int first, int last)
  {
    this.base = base;
    this.first = first;
    this.last = last;

    assert last >= first;
    assert first >= base.firstUsed();
    assert last <= base.lastUsed();
  }

  public Array(Collection<? extends IValue> values)
  {
    this.base = new ArrayBase(values, 0);
    this.first = base.firstUsed();
    this.last = base.lastUsed();
  }

  public static Array nilArray = new Array(new ArrayBase(0), 0, 0);

  @Override
  public boolean isEmpty()
  {
    return last == first;
  }

  @Override
  public int size()
  {
    return last - first;
  }

  public ArrayBase getBase()
  {
    return base;
  }

  public int getFirst()
  {
    return first;
  }

  public int getLast()
  {
    return last;
  }

  @Override
  public IValue getCell(int index)
  {
    int ix = index + first;
    if (index < 0 || ix > last)
      throw new IndexOutOfBoundsException("array index " + index + " not in range 0.." + size());
    return base.getCell(ix);
  }

  @Override
  public Array tail()
  {
    if (isEmpty())
      throw new ArrayIndexOutOfBoundsException();
    else
      return removeCell(0);
  }

  @Override
  @Deprecated
  public void setCell(int index, IValue value)
  {
    int ix = index + first;
    if (index < 0 || ix > last)
      throw new IndexOutOfBoundsException("array index " + index + " not in range 0.." + size());

    base.setCell(ix, value);
  }

  @Override
  public IList substituteCell(int index, IValue value)
  {
    int ix = index + first;

    if (index >= 0 && ix < last) {
      ArrayBase base = this.base.cloneBase(first, last, overhang(first, last));
      base.setCell(base.firstUsed() + index, value);
      return new Array(base, base.firstUsed(), base.lastUsed());
    } else
      throw new IndexOutOfBoundsException("array index " + index + " not in range 0.." + size());
  }

  @Override
  public IArray spliceList(IArray sub, int index, int to)
  {
    if (index < 0)
      throw new IndexOutOfBoundsException("splice: index: " + index + " should be greater than or equal to zero");
    else if (to < index)
      throw new IndexOutOfBoundsException("splice: to: " + to + " should be greater than or equal to index: " + index);
    else if (to == 0 && index == 0)
      return sub.concatList(this);
    else if (index == size())
      return concatList(sub);
    else {
      int newLength = size() + sub.size() - (to - index);
      int overhang = overhang(newLength);
      IValue[] data = base.data();

      IValue[] newData = new IValue[newLength + 2 * overhang];

      int off = overhang;
      for (int ix = 0; ix < index; ix++)
        newData[ix + off] = data[ix + first];

      off += index;
      for (int ix = 0; ix < sub.size(); ix++)
        newData[ix + off] = sub.getCell(ix);

      off += sub.size();
      for (int ix = to; ix < last - first; ix++)
        newData[off++] = data[first + ix];
      return new Array(new ArrayBase(newData, overhang, newLength + overhang), overhang, overhang + newLength);
    }
  }

  @Override
  public IArray concatList(IArray sub)
  {
    synchronized (base) {
      int subSize = sub.size();
      if (last == base.lastUsed() && last + subSize < base.limit()) {
        for (IValue el : sub)
          base.append(el);
        return new Array(base, first, last + sub.size());
      } else {
        int size = size();
        int newLength = size + subSize;
        IValue[] data = base.data();
        int overhang = overhang(newLength);
        IValue[] newData = new IValue[newLength + 2 * overhang];
        int off = overhang;
        for (int ix = 0; ix < size; ix++)
          newData[ix + off] = data[ix + first];
        off += size;
        for (int ix = 0; ix < subSize; ix++)
          newData[ix + off] = sub.getCell(ix);
        return new Array(new ArrayBase(newData, overhang, newLength + overhang), overhang, newLength + overhang);
      }
    }
  }

  @Override
  public Array slice(int from, int to)
  {
    assert from >= 0 && to >= from;
    to = Math.min(to, size());
    if (from == 0 && to == size())
      return this;
    else
      return new Array(base, first + from, first + to);
  }

  @Override
  public Array slice(int from)
  {
    assert from >= 0;
    from = Math.min(from + first, last);
    return new Array(base, from, last);
  }

  @Override
  public int hashCode()
  {
    if (hash == 0) {
      hash = label.hashCode();
      for (IValue el : this) {
        hash = hash * 37 + el.hashCode();
      }
    }
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Array) {
      Array other = (Array) obj;
      if (other.size() == size()) {
        for (int ix = 0; ix < size(); ix++) {
          if (!getCell(ix).equals(other.getCell(ix)))
            return false;
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(IList other, IFunction test) throws EvaluationException
  {
    int arity = size();

    if (other instanceof IArray && other.size() == arity) {
      IArray otherAry = (IArray) other;
      for (int ix = 0; ix < arity; ix++) {
        if (!Factory.boolValue(test.enter(getCell(ix), otherAry.getCell(ix))))
          return false;
      }
      return true;
    } else
      return false;
  }

  @Override
  public Array deleteUsingPattern(IPattern filter) throws EvaluationException
  {
    int count = 0;
    IValue[] data = base.data();
    boolean[] flags = new boolean[data.length];

    for (int ix = first; ix < last; ix++) {
      if (filter.match(data[ix]) != null) {
        count++;
        flags[ix] = true;
      }
    }

    if (count > 0) {
      int overhang = overhang(size() - count);
      IValue[] nData = new IValue[size() + 2 * overhang];
      int dst = overhang;
      for (int ix = first; ix < last; ix++) {
        if (!flags[ix]) // marked for deletion?
          nData[dst++] = data[ix];
      }
      return new Array(new ArrayBase(nData, overhang, dst), overhang, dst);
    } else
      return this;
  }

  @Override
  public Array updateUsingPattern(IPattern filter, IFunction transform) throws EvaluationException
  {
    int count = 0;
    IValue[] data = base.data();
    IValue[] nData = null;
    ArrayBase nBase = null;
    int nOff = -1;

    for (int ix = first; ix < last; ix++) {
      IValue el = data[ix];
      if (filter.match(el) != null) {
        if (nBase == null) {
          int over = overhang(first, last);
          nBase = base.cloneBase(first, last, over);
          nData = nBase.data();
          nOff = over - first;
        }
        nData[nOff + ix] = transform.enter(el);
        count++;
      }
    }

    if (count > 0)
      return new Array(nBase, nBase.firstUsed(), nBase.lastUsed());
    else
      return this;
  }

  @Override
  public IArray mapOver(IFunction transform) throws EvaluationException
  {
    ArrayBase newBase = new ArrayBase(size());

    for (Iterator<IValue> it = iterator(); it.hasNext();)
      newBase.append(transform.enter(it.next()));

    return new Array(newBase, newBase.firstUsed(), newBase.lastUsed());
  }

  @Override
  public IArray filter(IFunction test) throws EvaluationException
  {
    ArrayBase newBase = new ArrayBase(size());

    for (Iterator<IValue> it = iterator(); it.hasNext();) {
      IValue el = it.next();
      if (Factory.boolValue(test.enter(el)))
        newBase.append(el);
    }

    return new Array(newBase, newBase.firstUsed(), newBase.lastUsed());
  }

  @Override
  public IValue leftFold(IFunction transform, IValue state) throws EvaluationException
  {
    for (IValue el : this)
      state = transform.enter(state, el);
    return state;
  }

  @Override
  public IValue leftFold1(IFunction transform) throws EvaluationException
  {
    Iterator<IValue> it = iterator();
    if (it.hasNext()) {
      IValue st = it.next();

      while (it.hasNext()) {
        st = transform.enter(st, it.next());
      }
      return st;
    } else
      throw new EvaluationException("array is empty");
  }

  @Override
  public IValue rightFold(IFunction transform, IValue state) throws EvaluationException
  {
    for (int ix = size() - 1; ix >= 0; ix--)
      state = transform.enter(getCell(ix), state);

    return state;
  }

  @Override
  public IValue rightFold1(IFunction transform) throws EvaluationException
  {
    if (!isEmpty()) {
      int ix = size() - 1;

      IValue st = getCell(ix--);

      while (ix >= 0)
        st = transform.enter(getCell(ix--), st);
      return st;
    } else
      throw new EvaluationException("array is empty");
  }

  private static int overhang(int size)
  {
    return (size >> 3) + 10;
  }

  private static int overhang(int first, int last)
  {
    return overhang(last - first);
  }

  @Override
  public IType getType()
  {
    if (last > first)
      return TypeUtils.arrayType(getCell(0).getType());
    else
      return TypeUtils.arrayType(new TypeVar());
  }

  public static IType conType()
  {
    TypeVar tv = new TypeVar();
    return new UniversalType(tv, TypeUtils.tupleConstructorType(tv, TypeUtils.arrayType(tv)));
  }

  @Override
  public IValue copy() throws EvaluationException
  {
    IValue[] data = base.data();
    int overhang = overhang(size());
    IValue[] newData = new IValue[size() + 2 * overhang];
    int off = overhang;
    int size = size();
    for (int ix = 0; ix < size; ix++)
      newData[ix + off] = data[ix + first].copy();
    return new Array(new ArrayBase(newData, overhang, size + overhang), overhang, size + overhang);
  }

  @Override
  public Array shallowCopy()
  {
    int overhang = overhang(size());
    return new Array(base.cloneBase(first, last, overhang), overhang, size() + overhang);
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitList(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("list of [");
    String sep = "";

    for (IValue arg : this) {
      disp.append(sep);
      sep = ", ";
      if (arg instanceof PrettyPrintable)
        ((PrettyPrintable) arg).prettyPrint(disp);
      else
        disp.append(arg.toString());
    }
    disp.append("]");
    disp.popIndent(mark);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public Array addCell(IValue el) throws EvaluationException
  {
    synchronized (base) {
      if (last == base.lastUsed() && base.lastUsed() < base.limit()) {
        base.append(el);
        return new Array(base, first, last + 1);
      } else {
        ArrayBase newBase = base.cloneBase(first, last, overhang(first, last + 1));
        newBase.append(el);
        return new Array(newBase, newBase.firstUsed(), newBase.lastUsed());
      }
    }
  }

  @Override
  public Array consCell(IValue el) throws EvaluationException
  {
    synchronized (base) {
      if (first == base.firstUsed() && base.firstUsed() > 0) {
        base.prepend(el);
        return new Array(base, first - 1, last);
      } else {
        ArrayBase newBase = base.cloneBase(first, last, overhang(first - 1, last));
        newBase.prepend(el);
        return new Array(newBase, newBase.firstUsed(), newBase.lastUsed());
      }
    }
  }

  @Override
  public Array addCells(Collection<IValue> values) throws EvaluationException
  {
    synchronized (base) {
      int vSize = values.size();
      if (last == base.lastUsed() && base.lastUsed() + vSize < base.limit()) {
        base.appendAll(values);
        return new Array(base, first, last + vSize);
      } else {
        ArrayBase newBase = base.cloneBase(first, last, overhang(size() + vSize));
        newBase.appendAll(values);
        return new Array(newBase, newBase.firstUsed(), newBase.lastUsed());
      }
    }
  }

  @Override
  public IList concat(IList sub) throws EvaluationException
  {
    synchronized (base) {
      int vSize = sub.size();
      if (last == base.lastUsed() && base.lastUsed() + vSize < base.limit()) {
        for (IValue el : sub)
          base.append(el);
        return new Array(base, first, last + vSize);
      } else {
        ArrayBase newBase = base.cloneBase(first, last, size() + overhang(size() + vSize));
        for (IValue el : sub)
          newBase.append(el);
        return new Array(newBase, newBase.firstUsed(), newBase.lastUsed());
      }
    }
  }

  @Override
  public Array removeCell(int ix)
  {
    int offset = ix + first;
    if (offset == first)
      return new Array(base, first + 1, last);
    else if (offset == last - 1)
      return new Array(base, first, last - 1);
    else {
      ArrayBase newBase = base.remove(offset);
      return new Array(newBase, newBase.firstUsed(), newBase.lastUsed());
    }
  }

  @Override
  public IArray reverse()
  {
    int overhang = overhang(size());
    ArrayBase newBase = new ArrayBase(size() + overhang);

    int arity = last - first;

    for (int ix = arity - 1; ix >= 0; ix--)
      newBase.append(getCell(ix));
    return new Array(newBase, newBase.firstUsed(), newBase.lastUsed());
  }

  public static void declare(ITypeContext cxt)
  {
    TypeVar tv = new TypeVar();
    cxt.defineType(new CafeTypeDescription(new UniversalType(tv, TypeUtils.arrayType(tv)), Array.class
        .getCanonicalName()));
  }

  public static Array newArray(Collection<IValue> data)
  {
    ArrayBase base = new ArrayBase(data, data.size() / 20);
    return new Array(base, base.firstUsed(), base.lastUsed());
  }

  public static Array newArray(Iterator<IValue> it, int approx)
  {
    ArrayBase base = new ArrayBase(overhang(approx));
    while (it.hasNext())
      base.append(it.next());
    return new Array(base, base.firstUsed(), base.lastUsed());
  }

  public static Array newArray(IValue[] data)
  {
    ArrayBase base = new ArrayBase(data, 0, data.length);
    return new Array(base, 0, data.length);
  }

  @Override
  public Iterator<IValue> iterator()
  {
    return new ArrayIterator<IValue>(base.data(), first, last);
  }
}
