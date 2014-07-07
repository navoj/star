package org.star_lang.star.data.value;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

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
public abstract class NTuple
{
  public static final NTpl $0Enum = new NTpl(new IValue[] {});
  public static final IType unitType = $0Enum.getType();

  public static class NTpl extends NTuple implements IConstructor
  {
    private final IValue[] els;

    public NTpl(IValue[] els)
    {
      this.els = els;
    }

    @Override
    public int conIx()
    {
      return els.length;
    }

    @Override
    public String getLabel()
    {
      return TypeUtils.tupleLabel(els.length);
    }

    @Override
    public int size()
    {
      return els.length;
    }

    public IValue get___0()
    {
      return els[0];
    }

    @Override
    public IValue getCell(int index)
    {
      if (index >= 0 && index < els.length)
        return els[index];
      throw new IllegalArgumentException("index out of range");
    }

    @Override
    public IValue[] getCells()
    {
      return els;
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new EvaluationException("not permitted");
    }

    @Override
    public IType getType()
    {
      IType[] elTypes = new IType[els.length];
      for (int ix = 0; ix < els.length; ix++)
        elTypes[ix] = els[ix].getType();
      return TypeUtils.tupleType(elTypes);
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      IValue[] copy = new IValue[els.length];
      for (int ix = 0; ix < els.length; ix++)
        copy[ix] = els[ix].copy();
      return new NTpl(copy);
    }

    @Override
    public IConstructor shallowCopy()
    {
      IValue[] copy = new IValue[els.length];
      for (int ix = 0; ix < els.length; ix++)
        copy[ix] = els[ix];
      return new NTpl(copy);
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitConstructor(this);
    }

    @Override
    public String toString()
    {
      return ValueDisplay.display(this);
    }

    @Override
    public int hashCode()
    {
      int hash = "$".hashCode() + els.length;
      for (IValue el : els)
        hash = hash * 37 + el.hashCode();

      return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof NTpl) {
        NTpl other = (NTpl) obj;
        if (other.size() == size()) {
          for (int ix = 0; ix < size(); ix++)
            if (!getCell(ix).equals(other.getCell(ix)))
              return false;
          return true;
        }
      }
      return false;
    }
  }

  public static IConstructor tuple(IValue... els)
  {
    if (els.length == 0)
      return $0Enum;
    else
      return new NTpl(els);
  }

  public static void declare(Intrinsics cxt)
  {
    TypeVar tv = new TypeVar();
    IType tplType = TypeUtils.tupleType(tv);
    IType conType = TypeUtils.tupleConstructorType(tv, tplType);
    ConstructorSpecifier tplSpec = new ConstructorSpecifier(Location.nullLoc, null, "$", 0, conType, NTpl.class,
        NTuple.class);

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(tplSpec);

    ITypeDescription locDesc = new CafeTypeDescription(Location.nullLoc, new UniversalType(tv, tplType), NTuple.class
        .getName(), specs);

    cxt.defineType(locDesc);
  }
}
