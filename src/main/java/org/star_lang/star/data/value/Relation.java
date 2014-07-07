package org.star_lang.star.data.value;

import java.util.Collection;
import java.util.Iterator;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.ArrayIterator;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IRelation;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;

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
public class Relation implements IRelation, PrettyPrintable
{
  public static final String label = "relation";

  private final ArrayBase base;
  private final int first;
  private final int last;

  public Relation(ArrayBase base, int first, int last)
  {
    this.base = base;
    this.first = first;
    this.last = last;

    assert last >= first;
    assert first >= base.firstUsed();
    assert last <= base.lastUsed();
  }

  public Relation(Collection<? extends IValue> elements)
  {
    ArrayBase base = this.base = new ArrayBase(elements, (int) (elements.size() / 20));
    first = base.firstUsed();
    last = base.lastUsed();
  }

  public Relation(Iterator<IValue> it)
  {
    ArrayBase base = this.base = new ArrayBase(10);
    while (it.hasNext()) {
      IValue el = it.next();
      base.append(el);
    }
    first = base.firstUsed();
    last = base.lastUsed();
  }

  public static Relation create(IValue... elements)
  {
    ArrayBase base = new ArrayBase(elements, 0, elements.length);
    return new Relation(base, base.firstUsed(), base.lastUsed());
  }

  public static Relation create(Collection<? extends IValue> elements)
  {
    return new Relation(elements);
  }

  public IValue getCell(int ix) throws EvaluationException
  {
    if (size() == 0)
      throw new EvaluationException("no elements in relation");
    else
      return base.getCell(first);
  }

  public Relation slice(int from, int to) throws EvaluationException
  {
    if (from < 0 || from > size() || to < from || to > size())
      throw new EvaluationException("slice out of range");
    else
      return new Relation(base, first + from, first + to);
  }

  public Relation splice(Relation sub, int index, int to) throws EvaluationException
  {
    if (index < 0)
      throw new IndexOutOfBoundsException("splice: index: " + index + " should be greater than or equal to zero");
    else if (to < index)
      throw new IndexOutOfBoundsException("splice: to: " + to + " should be greater than or equal to index: " + index);
    else if (to == 0 && index == 0)
      return sub.concat(this);
    else if (index == size())
      return concat(sub);
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
      return new Relation(new ArrayBase(newData, overhang, newLength + overhang), overhang, overhang + newLength);
    }
  }

  @Override
  public Relation addCell(IValue tuple) throws EvaluationException
  {
    synchronized (base) {
      if (last == base.lastUsed() && base.lastUsed() < base.limit()) {
        base.append(tuple);
        return new Relation(base, first, last + 1);
      } else {
        ArrayBase newBase = base.cloneBase(first, last, overhang(first, last + 1));
        newBase.append(tuple);
        return new Relation(newBase, newBase.firstUsed(), newBase.lastUsed());
      }
    }
  }

  @Override
  public boolean isEmpty()
  {
    return first == last;
  }

  @Override
  public int size()
  {
    return last - first;
  }

  @Override
  public synchronized Iterator<IValue> iterator()
  {
    return new ArrayIterator<IValue>(base.data(), first, last);
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitRelation(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("relation of {");
    String sep = "";

    for (IValue arg : this) {
      disp.append(sep);
      sep = "; ";
      if (arg instanceof PrettyPrintable)
        ((PrettyPrintable) arg).prettyPrint(disp);
      else
        disp.append(arg.toString());
    }
    disp.append("}");
    disp.popIndent(mark);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public IType getType()
  {
    if (last > first)
      return TypeUtils.relationType(base.getCell(first).getType());
    else
      return TypeUtils.relationType(new TypeVar());
  }

  @Override
  public synchronized IValue copy()
  {
    return shallowCopy();
  }

  @Override
  public synchronized IRelation shallowCopy()
  {
    return new Relation(base, first, last);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    else if (obj instanceof IRelation) {
      IRelation other = (IRelation) obj;
      if (other.size() == size()) {
        eqLoop: for (IValue el : this) {
          for (IValue ot : other) {
            if (el.equals(ot))
              continue eqLoop;
          }
          return false;
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(IRelation other, IFunction test) throws EvaluationException
  {
    if (other.size() == size()) {
      eqLoop: for (IValue el : this) {
        for (IValue ot : other) {
          if (Factory.boolValue(test.enter(el, ot)))
            continue eqLoop;
        }
        return false;
      }
      return true;
    } else
      return false;
  }

  @Override
  public Relation concat(IRelation sub) throws EvaluationException
  {
    synchronized (base) {
      int subSize = sub.size();
      if (last == base.lastUsed() && last + subSize < base.limit()) {
        for (IValue el : sub)
          base.append(el);
        return new Relation(base, first, last + sub.size());
      } else {
        int size = size();
        int newLength = size + subSize;
        IValue[] data = base.data();
        int overhang = overhang(newLength);
        IValue[] newData = new IValue[newLength + 2 * overhang];
        int off = overhang;
        for (int ix = 0; ix < size; ix++)
          newData[ix + off] = data[ix + first];

        int ix = size + off;
        for (IValue el : sub)
          newData[ix++] = el;
        return new Relation(new ArrayBase(newData, overhang, newLength + overhang), overhang, newLength + overhang);
      }
    }
  }

  @Override
  public Relation deleteUsingPattern(IPattern filter) throws EvaluationException
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
      return new Relation(new ArrayBase(nData, overhang, dst), overhang, dst);
    } else
      return this;
  }

  @Override
  public Relation updateUsingPattern(IPattern filter, IFunction transform) throws EvaluationException
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
      return new Relation(nBase, nBase.firstUsed(), nBase.lastUsed());
    else
      return this;
  }

  @Override
  public IRelation mapOver(IFunction transform) throws EvaluationException
  {
    ArrayBase base = new ArrayBase(size());

    for (Iterator<IValue> it = iterator(); it.hasNext();)
      base.append(transform.enter(it.next()));

    return new Relation(base, base.firstUsed(), base.lastUsed());
  }

  @Override
  public IRelation filter(IFunction test) throws EvaluationException
  {
    ArrayBase newBase = new ArrayBase(size());

    for (Iterator<IValue> it = iterator(); it.hasNext();) {
      IValue el = it.next();
      if (Factory.boolValue(test.enter(el)))
        newBase.append(el);
    }

    return new Relation(newBase, newBase.firstUsed(), newBase.lastUsed());

  }

  @Override
  public IValue leftFold(IFunction transform, IValue init) throws EvaluationException
  {
    for (IValue el : this)
      init = transform.enter(init, el);
    return init;
  }

  @Override
  public IValue rightFold(IFunction transform, IValue init) throws EvaluationException
  {
    for (int ix = size() - 1; ix >= 0; ix--)
      init = transform.enter(getCell(ix), init);

    return init;
  }

  @Override
  public int hashCode()
  {
    int hash = label.hashCode();
    for (int ix = first; ix < last; ix++)
      hash = hash * 37 + base.getCell(ix).hashCode();
    return hash;
  }

  private static int overhang(int size)
  {
    return (size >> 3) + 10;
  }

  private static int overhang(int first, int last)
  {
    return overhang(last - first);
  }
}
