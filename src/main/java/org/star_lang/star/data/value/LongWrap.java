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
public abstract class LongWrap implements PrettyPrintable, IConstructor
{
  public static final NonLong nonLongEnum = new NonLong();
  public static final IType longType = TypeUtils.typeExp(StandardTypes.LONG); // We do not use

  @Override
  public IType getType()
  {
    return StandardTypes.longType;
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
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
  public IValue[] getCells()
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    throw new UnsupportedOperationException("not permitted");
  }

  public static void declare(ITypeContext cxt)
  {
    ConstructorSpecifier strSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.LONG,
        LongWrapper.CONIX, LongWrapper.conType(), LongWrapper.class, LongWrap.class);
    ConstructorSpecifier nonSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.NON_LONG,
        NonLong.CONIX, NonLong.conType(), NonLong.class, LongWrap.class);

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(strSpec);
    specs.add(nonSpec);
    ITypeDescription type = new CafeTypeDescription(Location.nullLoc, longType, Utils
        .javaInternalClassName(LongWrap.class), specs);
    cxt.defineType(type);
  }

  public static class LongWrapper extends LongWrap implements IScalar<Long>
  {
    public static final String name = StandardTypes.LONG;
    public static final int CONIX = 0;
    private final long ix;

    /**
     * Construct a long wrapper as an {@link IValue} value.
     * 
     * @param ix
     */
    public LongWrapper(long ix)
    {
      this.ix = ix;
    }

    @Override
    public Long getValue()
    {
      return ix;
    }

    public long getVal()
    {
      return ix;
    }

    // This is here to allow Cafe to access the value
    public long get___0()
    {
      return ix;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitScalar(this);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(ix);
    }

    @Override
    public String toString()
    {
      return String.valueOf(ix);
    }

    @Override
    public int conIx()
    {
      return CONIX;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.LONG;
    }

    @Override
    public boolean equals(Object other)
    {
      return other instanceof LongWrapper && ((LongWrapper) other).ix == ix;
    }

    @Override
    public int hashCode()
    {
      return (int) ((ix >>> 32) ^ ix); // From Long's hashcode
    }

    public static IType conType()
    {
      return TypeUtils.tupleConstructorType(TypeUtils.typeExp(StandardTypes.RAW_LONG), longType);
    }
  }

  public final static class NonLong extends LongWrap
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
      return StandardTypes.NON_LONG;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendWord(getLabel());
    }

    public static IType conType()
    {
      return TypeUtils.constructorType(longType);
    }
  }
}
