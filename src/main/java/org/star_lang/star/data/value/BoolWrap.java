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
    List<IValueSpecifier> specs = new ArrayList<>();
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
