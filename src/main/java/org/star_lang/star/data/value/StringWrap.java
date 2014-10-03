package org.star_lang.star.data.value;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.StringUtils;
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
public abstract class StringWrap implements IValue, PrettyPrintable
{
  public static final NonStringWrapper nonStringEnum = new NonStringWrapper();
  public static final IType stringType = TypeUtils.typeExp(StandardTypes.STRING);

  @Override
  public IType getType()
  {
    return StandardTypes.stringType;
  }

  public static void declare(ITypeContext cxt)
  {
    ConstructorSpecifier strSpec = new ConstructorSpecifier(Location.nullLoc, null, StringWrapper.name, 0,
        StringWrapper.conType(), StringWrapper.class, StringWrap.class);
    ConstructorSpecifier nonSpec = new ConstructorSpecifier(Location.nullLoc, null, NonStringWrapper.name, 1,
        NonStringWrapper.conType(), NonStringWrapper.class, StringWrap.class);

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(strSpec);
    specs.add(nonSpec);
    ITypeDescription type = new CafeTypeDescription(Location.nullLoc, stringType, Utils
        .javaInternalClassName(StringWrap.class), specs);
    cxt.defineType(type);
  }

  @Override
  public String toString()
  {
    return QuoteDisplay.display(this);
  }

  public static class StringWrapper extends StringWrap implements IScalar<String>, IConstructor
  {
    private final String str;
    public static final String name = "string";

    public StringWrapper(String str)
    {
      this.str = str;
      assert str != null;
    }

    // This is here to allow Cafe to access the value
    public String get___0()
    {
      return str;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitScalar(this);
    }

    @Override
    public String getValue()
    {
      return str;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(StringUtils.quoteString(str));
    }

    @Override
    public String toString()
    {
      return str;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof StringWrapper) {
        StringWrapper other = (StringWrapper) obj;
        return str.equals(other.str);
      }
      return false;
    }

    @Override
    public int hashCode()
    {
      return name.hashCode() * 37 + str.hashCode();
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
    public int conIx()
    {
      return 0;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.STRING;
    }

    @Override
    public int size()
    {
      return 1; // No user-adjustable parts inside
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

    public static IType conType()
    {
      return TypeUtils.tupleConstructorType(TypeUtils.typeExp(StandardTypes.RAW_STRING), stringType);
    }
  }

  public final static class NonStringWrapper extends StringWrap implements IConstructor
  {
    public static final String name = "nonString";

    @Override
    public IType getType()
    {
      return StandardTypes.stringType;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitConstructor(this);
    }

    @Override
    public int conIx()
    {
      return 1;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.NON_STRING;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendWord(getLabel());
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

    public static IType conType()
    {
      return TypeUtils.constructorType(stringType);
    }

    @Override
    public int hashCode()
    {
      return name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof NonStringWrapper;
    }
  }
}
