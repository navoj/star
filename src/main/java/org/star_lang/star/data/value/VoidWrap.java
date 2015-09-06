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
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Type;
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
public abstract class VoidWrap implements IConstructor, PrettyPrintable
{
  public static final String label = "void";

  // This MUST have these names, for Cafe to work
  public static final Void voidEnum = new Void();

  public static IType voidType = new Type(label);

  @Override
  public IType getType()
  {
    return voidType;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  public static void declare(ITypeContext cxt)
  {
    IType conType = TypeUtils.constructorType(voidType);
    ConstructorSpecifier voidSpec = new ConstructorSpecifier(Location.nullLoc, null, Void.name, 0, conType, Void.class,
        VoidWrap.class);
    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(voidSpec);
    cxt.defineType(new CafeTypeDescription(Location.nullLoc, voidType, Utils.javaInternalClassName(VoidWrap.class),
        specs));
  }

  public static class Void extends VoidWrap
  {
    public static String name = label;
    public static final int conIx = 0;

    public Void()
    {
      super();
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
      throw new IllegalArgumentException("index out of range");
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new EvaluationException("index out of range");
    }

    @Override
    public IValue[] getCells()
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

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(name);
    }

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return name;
    }
  }
}
