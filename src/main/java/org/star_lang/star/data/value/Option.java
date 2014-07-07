package org.star_lang.star.data.value;

import java.util.ArrayList;
import java.util.List;

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
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

/*
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
 */
/**
 * Builtin implementation of the option type:
 * 
 * type option of %a is none or some(%a);
 * 
 * @author fgm
 */
@SuppressWarnings("serial")
public abstract class Option implements IConstructor, PrettyPrintable
{
  public static final String typeLabel = "option";
  public static None noneEnum = new None();

  public abstract IValue getContent();

  public static class Some extends Option
  {
    public static final int conIx = 0;
    public static final String label = "some";

    private static final int valOffset = 0;

    private final IValue val;

    public Some(IValue val)
    {
      this.val = val;
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
      case valOffset:
        return val;
      default:
        throw new IllegalAccessError("index out of range");
      }
    }

    public IValue get___0()
    {
      return val;
    }

    @Override
    public IValue getContent()
    {
      return val;
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] { val };
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new IllegalAccessError("not permitted");
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return new Some(val.copy());
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return new Some(val);
    }

    @Override
    public IType getType()
    {
      return new TypeExp(typeLabel, val.getType());
    }

    public static IType conType()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.tupleConstructorType(tv, new TypeExp(typeLabel, tv)));
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof Some) {
        Some cons = (Some) obj;
        return cons.val.equals(val);
      } else
        return false;
    }

    @Override
    public int hashCode()
    {
      return (label.hashCode() * 37) + +val.hashCode();
    }
  }

  public static class None extends Option
  {
    public static final int conIx = 1;
    public static final String label = "none";

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
      return 0;
    }

    @Override
    public IValue getContent()
    {
      throw new IllegalAccessError("not available");
    }

    @Override
    public IValue getCell(int index)
    {
      throw new IllegalAccessError("index out of range");
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new IllegalAccessError("index out of range");
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] {};
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return this;
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return this;
    }

    @Override
    public IType getType()
    {
      return new TypeExp(typeLabel, new TypeVar());
    }

    public static IType conType()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.constructorType(new TypeExp(typeLabel, tv)));
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof None;
    }

    @Override
    public int hashCode()
    {
      return label.hashCode();
    }
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

  public static Option some(IValue el)
  {
    return new Some(el);
  }

  public static void declare(ITypeContext cxt)
  {
    TypeVar tv = new TypeVar();

    IType optionType = new TypeExp(typeLabel, tv); // avoid issues with initialization of
    // intrinsics
    IType conConType = new UniversalType(tv, TypeUtils.tupleConstructorType(tv, optionType));
    Location nullLoc = Location.nullLoc;

    ConstructorSpecifier someSpec = new ConstructorSpecifier(nullLoc, null, Some.label, Some.conIx, conConType,
        Some.class, Option.class);

    IType nilConType = new UniversalType(tv, TypeUtils.constructorType(optionType));
    ConstructorSpecifier noneSpec = new ConstructorSpecifier(nullLoc, null, None.label, None.conIx, nilConType,
        None.class, Option.class);

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(someSpec);
    specs.add(noneSpec);

    ITypeDescription desc = new CafeTypeDescription(nullLoc, new UniversalType(tv, optionType), Option.class.getName(),
        specs);

    cxt.defineType(desc);
  }
}
