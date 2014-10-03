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
 * Wrap a char in an {@link org.star_lang.data.IValue} object. Used only when viewing pieces of a
 * value. I.e., if you do a getMember of an IRecord field that is an char, you will actually get an
 * CharWrap value; even though the field is actually represented as an int. CharWrap is a way of
 * boxing character values.
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
public abstract class CharWrap implements PrettyPrintable, IConstructor
{
  public static final NonCharacter nonCharEnum = new NonCharacter();

  @Override
  public IType getType()
  {
    return StandardTypes.charType;
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
    IType charType = TypeUtils.typeExp(StandardTypes.CHAR); // We do cannot use
    // StandardTypes.charType
    // here
    ConstructorSpecifier strSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.CHAR,
        CharWrapper.CONIX, CharWrapper.conType(), CharWrapper.class, CharWrap.class);
    ConstructorSpecifier nonSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.NON_CHAR,
        NonCharacter.CONIX, TypeUtils.constructorType(charType), NonCharacter.class, CharWrap.class);

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(strSpec);
    specs.add(nonSpec);
    ITypeDescription type = new CafeTypeDescription(Location.nullLoc, charType, Utils
        .javaInternalClassName(CharWrap.class), specs);
    cxt.defineType(type);
  }

  public static class CharWrapper extends CharWrap implements IScalar<Integer>
  {
    public static final int CONIX = 0;
    private final int cx;

    /**
     * Construct an char wrapper as an {@link IValue} value.
     * 
     * @param ix
     */
    public CharWrapper(int ix)
    {
      this.cx = ix;
    }

    @Override
    public Integer getValue()
    {
      return cx;
    }

    // This is here to allow Cafe to access the value
    public int get___0()
    {
      return cx;
    }

    public int getVal()
    {
      return cx;
    }

    @Override
    public int hashCode()
    {
      return cx;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof CharWrapper && ((CharWrapper) obj).getVal() == cx;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitScalar(this);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendChar(cx);
    }

    @Override
    public String toString()
    {
      return String.valueOf(Character.toChars(cx));
    }

    @Override
    public int conIx()
    {
      return CONIX;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.CHAR;
    }

    public static IType conType()
    {
      return TypeUtils.constructorType(TypeUtils.tupleType(TypeUtils.typeExp(StandardTypes.RAW_CHAR)),
          StandardTypes.charType);
    }
  }

  public final static class NonCharacter extends CharWrap
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
      return StandardTypes.NON_CHAR;
    }

    @Override
    public int hashCode()
    {
      return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof NonCharacter;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendWord(getLabel());
    }

    public static IType conType()
    {
      return TypeUtils.constructorType(StandardTypes.charType);
    }
  }
}