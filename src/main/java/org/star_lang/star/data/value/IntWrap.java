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
 * Wrap an integer in an {@link org.star_lang.data.IValue} object. Used only when viewing pieces of a
 * value. I.e., if you do a getMember of an IRecord field that is an integer, you will actually get
 * an IntWrap value; even though the field is actually represented as an int. IntWrap is a way of
 * boxing integer value.
 * 
 * This is a hand-implemented version of the regular Star definition:
 * 
 * type integer is integer(integer_) or nonInteger;
 * 
 * where integer_ is the 'raw' type of integer. Raw types are not generally accessible to Star or
 * platform programmers.
 * 
 * We hand-implement it to ease platform-related issues since scalars are ubiquitous in the
 * platform.
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
public abstract class IntWrap implements PrettyPrintable, IConstructor
{
  public static final NonInteger nonIntegerEnum = new NonInteger();

  public static IType intType = TypeUtils.typeExp(StandardTypes.INTEGER); // We do not use

  // StandardTypes.integerType
  // here

  @Override
  public IType getType()
  {
    return StandardTypes.integerType;
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
  public IConstructor copy()
  {
    return this;
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return this;
  }

  public abstract Integer getValue();

  public static void declare(ITypeContext cxt)
  {
    ConstructorSpecifier strSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.INTEGER,
        IntWrapper.CONIX, IntWrapper.conType(), IntWrapper.class, IntWrap.class);
    ConstructorSpecifier nonSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.NON_INTEGER,
        NonInteger.CONIX, NonInteger.conType(), NonInteger.class, IntWrap.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(strSpec);
    specs.add(nonSpec);
    ITypeDescription type = new CafeTypeDescription(Location.nullLoc, intType, Utils
        .javaInternalClassName(IntWrap.class), specs);
    cxt.defineType(type);
  }

  public static class IntWrapper extends IntWrap implements IScalar<Integer>
  {
    public static final int CONIX = 0;
    public static final String name = StandardTypes.INTEGER;
    private final int ix;

    /**
     * Construct an integer wrapper as an {@link IValue} value.
     * 
     * @param ix
     */
    public IntWrapper(int ix)
    {
      this.ix = ix;
    }

    @Override
    public Integer getValue()
    {
      return ix;
    }

    // This is here to allow Cafe to access the value
    public int get___0()
    {
      return ix;
    }

    public int getVal()
    {
      return ix;
    }

    @Override
    public int size()
    {
      return 1;
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
      return StandardTypes.INTEGER;
    }

    @Override
    public boolean equals(Object other)
    {
      return other instanceof IntWrapper && ((IntWrapper) other).ix == ix;
    }

    @Override
    public int hashCode()
    {
      return ix;
    }

    public static IType conType()
    {
      return TypeUtils.tupleConstructorType(TypeUtils.typeExp(StandardTypes.RAW_INTEGER), intType);
    }
  }

  public final static class NonInteger extends IntWrap
  {
    public static final int CONIX = 1;

    @Override
    public Integer getValue()
    {
      return null;
    }

    @Override
    public int conIx()
    {
      return CONIX;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.NON_INTEGER;
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitConstructor(this);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendWord(getLabel());
    }

    @Override
    public boolean equals(Object arg0)
    {
      return arg0 instanceof NonInteger;
    }

    @Override
    public int hashCode()
    {
      return super.hashCode();
    }

    public static IType conType()
    {
      return TypeUtils.constructorType(intType);
    }
  }
}
