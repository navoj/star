package org.star_lang.star.operators.assignment.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.assignment.runtime.AtomicOps.AtomReference;
import org.star_lang.star.operators.assignment.runtime.AtomicOps.AtomTestAndSet;
import org.star_lang.star.operators.assignment.runtime.AtomicOps.AtomicAssignment;
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
public abstract class AtomicCell extends AtomicReference<IValue> implements IConstructor, PrettyPrintable
{
  public static final String typeLabel = "atomic";

  public static class AtomCell extends AtomicCell
  {
    public static final int conIx = 0;
    public static final String label = "atomic";
    private static final int itemOffset = 0;

    public AtomCell(IValue value)
    {
      set(value);
    }

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return label;
    }

    @Override
    public int size()
    {
      return 1;
    }

    @Override
    public IValue getCell(int index)
    {
      switch (index) {
      case itemOffset:
        return get();
      default:
        throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] { get() };
    }

    public IValue get___0()
    {
      return get();
    }

    public void setItem(IValue value)
    {
      set(value);
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      switch (index) {
      case itemOffset:
        set(value);
        return;
      default:
        throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return new AtomCell(get().copy());
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return new AtomCell(get());
    }

    @Override
    public IType getType()
    {
      return typeExp(get().getType());
    }

    public static IType conType()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.tupleConstructorType(tv, new TypeExp(typeLabel, tv)));
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitConstructor(this);
    }

    @Override
    public String toString()
    {
      return PrettyPrintDisplay.toString(this);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      ValueDisplay.display(disp, this);
    }
  }

  public static void declare(Intrinsics cxt)
  {
    Location nullLoc = Location.nullLoc;

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();

    specs.add(new ConstructorSpecifier(nullLoc, null, AtomCell.label, AtomCell.conIx, AtomCell.conType(),
        AtomCell.class, AtomicCell.class));

    TypeVar tv = new TypeVar();
    ITypeDescription desc = new CafeTypeDescription(nullLoc, new UniversalType(tv, typeExp(tv)), AtomicCell.class
        .getName(), specs);

    cxt.defineType(desc);

    cxt.declareBuiltin(new Builtin(AtomReference.name, AtomReference.type(), AtomReference.class));
    cxt.declareBuiltin(new Builtin(AtomicAssignment.name, AtomicAssignment.type(), AtomicAssignment.class));
    cxt.declareBuiltin(new Builtin(AtomTestAndSet.name, AtomTestAndSet.type(), AtomTestAndSet.class));
  }

  public static IType typeExp(IType refType)
  {
    return new TypeExp(typeLabel, refType);
  }
}
