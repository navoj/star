package org.star_lang.star.data.value;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IScalar;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
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
public abstract class FloatWrap implements PrettyPrintable, IConstructor
{
  public static final NonFloat nonFloatEnum = new NonFloat();

  @Override
  public IType getType()
  {
    return StandardTypes.floatType;
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
  }

  @Override
  public int size()
  {
    return 0;
  }

  @Override
  public IValue getCell(int index)
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public IValue[] getCells()
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public IConstructor copy()
  {
    return this;
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return this;
  }

  public static void declare(ITypeContext cxt)
  {
    IType intType = TypeUtils.typeExp(StandardTypes.FLOAT); // We do not use
    // StandardTypes.floatType
    // here
    IType conType = TypeUtils.tupleConstructorType(TypeUtils.typeExp(StandardTypes.RAW_FLOAT), intType);
    ConstructorSpecifier strSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.FLOAT,
        FloatWrapper.CONIX, conType, FloatWrapper.class, FloatWrap.class);
    ConstructorSpecifier nonSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.NON_FLOAT,
        NonFloat.CONIX, TypeUtils.constructorType(intType), NonFloat.class, FloatWrap.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(strSpec);
    specs.add(nonSpec);
    ITypeDescription type = new CafeTypeDescription(Location.nullLoc, intType, Utils
        .javaInternalClassName(FloatWrap.class), specs);
    cxt.defineType(type);
  }

  public static class FloatWrapper extends FloatWrap implements IScalar<Double>
  {
    public static final int CONIX = 0;
    private final double dx;

    /**
     * Construct a float wrapper as an {@link IValue} value.
     * 
     * @param ix
     */
    public FloatWrapper(double ix)
    {
      this.dx = ix;
    }

    @Override
    public Double getValue()
    {
      return dx;
    }

    // This is here to allow Cafe to access the value
    public double get___0()
    {
      return dx;
    }

    public double getVal()
    {
      return dx;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitScalar(this);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(dx);
    }

    @Override
    public String toString()
    {
      return String.valueOf(dx);
    }

    @Override
    public int conIx()
    {
      return CONIX;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.FLOAT;
    }

    @Override
    public boolean equals(Object other)
    {
      return other instanceof FloatWrapper && ((FloatWrapper) other).dx == dx;
    }

    @Override
    public int hashCode()
    {
      long bits = Double.doubleToLongBits(dx);
      return StandardTypes.FLOAT.hashCode() * 37 + (int) (bits ^ (bits >>> 32));
    }

    public static IType conType()
    {
      return TypeUtils.tupleConstructorType(StandardTypes.rawFloatType, StandardTypes.floatType);
    }
  }

  public final static class NonFloat extends FloatWrap
  {
    public static final int CONIX = 1;

    @Override
    public int conIx()
    {
      return CONIX;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.NON_FLOAT;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendWord(getLabel());
    }

    public static IType conType()
    {
      return TypeUtils.constructorType(StandardTypes.floatType);
    }
  }
}
