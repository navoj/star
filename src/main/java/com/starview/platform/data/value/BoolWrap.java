package com.starview.platform.data.value;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IConstructor;
import com.starview.platform.data.IScalar;
import com.starview.platform.data.IValue;
import com.starview.platform.data.IValueVisitor;
import com.starview.platform.data.type.ConstructorSpecifier;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeContext;
import com.starview.platform.data.type.IValueSpecifier;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;

/**
 * boolean value
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
public abstract class BoolWrap implements IScalar<Boolean>, PrettyPrintable
{
  public final boolean trueVal;
  public static final IType booleanType = TypeUtils.typeExp(StandardTypes.BOOLEAN);

  // These MUST have these names, for Cafe to work
  public static final TrueValue trueEnum = new TrueValue();
  public static final FalseValue falseEnum = new FalseValue();

  private BoolWrap(boolean bool)
  {
    this.trueVal = bool;
  }

  @Override
  public Boolean getValue()
  {
    return trueVal;
  }

  public boolean getVal()
  {
    return trueVal;
  }

  @Override
  public IType getType()
  {
    return booleanType;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(String.valueOf(trueVal));
  }

  @Override
  public String toString()
  {
    return String.valueOf(trueVal);
  }

  public static void declare(ITypeContext cxt)
  {
    ConstructorSpecifier trueSpec = new ConstructorSpecifier(Location.nullLoc, null, TrueValue.name, 1, conType(),
        TrueValue.class, BoolWrap.class);
    ConstructorSpecifier falseSpec = new ConstructorSpecifier(Location.nullLoc, null, FalseValue.name, 0, conType(),
        FalseValue.class, BoolWrap.class);
    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(falseSpec);
    specs.add(trueSpec);
    cxt.defineType(new CafeTypeDescription(Location.nullLoc, booleanType, Utils.javaInternalClassName(BoolWrap.class),
        specs));
  }

  public static IType conType()
  {
    return TypeUtils.constructorType(booleanType);
  }

  public abstract static class BooleanValue extends BoolWrap implements IConstructor
  {

    public BooleanValue(boolean value)
    {
      super(value);
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitScalar(this);
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public IValue getCell(int index)
    {
      throw new IllegalArgumentException("index out of range");
    }

    @Override
    public IValue[] getCells()
    {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new EvaluationException("index out of range");
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
  }

  public static class FalseValue extends BooleanValue
  {
    public static final String name = "false";

    public FalseValue()
    {
      super(false);
    }

    @Override
    public int conIx()
    {
      return 0;
    }

    @Override
    public String getLabel()
    {
      return name;
    }
  }

  public static class TrueValue extends BooleanValue
  {
    public static final String name = "true";

    public TrueValue()
    {
      super(true);
    }

    @Override
    public int conIx()
    {
      return 1;
    }

    @Override
    public String getLabel()
    {
      return name;
    }
  }
}
