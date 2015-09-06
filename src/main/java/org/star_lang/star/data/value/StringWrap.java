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

    List<IValueSpecifier> specs = new ArrayList<>();
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
