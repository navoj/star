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

/**
 * Floating point wrapper
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
