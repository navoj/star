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
public abstract class NTuple
{
  public static final String label = "$";
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
      int hash = label.hashCode() + els.length;
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
    ConstructorSpecifier tplSpec = new ConstructorSpecifier(Location.nullLoc, null, label, 0, conType, NTpl.class,
        NTuple.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(tplSpec);

    ITypeDescription locDesc = new CafeTypeDescription(Location.nullLoc, new UniversalType(tv, tplType), NTuple.class
        .getName(), specs);

    cxt.defineType(locDesc);
  }
}
