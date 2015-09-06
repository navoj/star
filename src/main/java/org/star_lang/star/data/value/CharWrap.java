package org.star_lang.star.data.value;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.*;
import org.star_lang.star.data.type.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap a char in an {@link org.star_lang.star.data.IValue} object. Used only when viewing pieces of a
 * value. I.e., if you do a getMember of an IRecord field that is an char, you will actually get an
 * CharWrap value; even though the field is actually represented as an int. CharWrap is a way of
 * boxing character values.
 *
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
public abstract class CharWrap implements PrettyPrintable, IConstructor
{
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

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(strSpec);
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
     * @param ix to wrap
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