package org.star_lang.star.data.value;

import java.util.ArrayList;
import java.util.List;

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
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

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

/**
 * Builtin implementation of the result type:
 * 
 * type result of %a is success(%a) or error(exception) or denied;
 * 
 * @author fgm
 */
@SuppressWarnings("serial")
public abstract class Reason implements IConstructor, PrettyPrintable
{
  public static final String typeLabel = "reason";
  public static final IType type = new TypeExp(typeLabel);
  public static NoPermission noPermissionEnum = new NoPermission();
  public static Busy busyEnum = new Busy();

  public static class NoPermission extends Reason
  {
    public static final int conIx = 0;
    public static final String label = "noPermission";

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return label;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj == this;
    }
  }

  public static class Busy extends Reason
  {
    public static final int conIx = 1;
    public static final String label = "busy";

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return label;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj == this;
    }
  }

  @Override
  public int size()
  {
    return 0;
  }

  @Override
  public IValue getCell(int index)
  {
    throw new IllegalAccessError("index out of range");
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    throw new IllegalAccessError("index out of range");
  }

  @Override
  public IValue[] getCells()
  {
    return new IValue[] {};
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
  public IType getType()
  {
    return type;
  }

  @Override
  public int hashCode()
  {
    return getLabel().hashCode();
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ValueDisplay.display(disp, this);
  }

  public static void declare()
  {
    Location nullLoc = Location.nullLoc;

    IType conType = TypeUtils.constructorType(type);
    ConstructorSpecifier permSpec = new ConstructorSpecifier(nullLoc, null, NoPermission.label, NoPermission.conIx,
        conType, NoPermission.class, Reason.class);
    ConstructorSpecifier busySpec = new ConstructorSpecifier(nullLoc, null, NoPermission.label, NoPermission.conIx,
        conType, NoPermission.class, Reason.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(permSpec);
    specs.add(busySpec);

    ITypeDescription desc = new CafeTypeDescription(nullLoc, type, Reason.class.getName(), specs);

    org.star_lang.star.operators.Intrinsics.declare(desc);
  }
}
