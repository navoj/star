package com.starview.platform.data.value;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IConstructor;
import com.starview.platform.data.IValue;
import com.starview.platform.data.IValueVisitor;
import com.starview.platform.data.type.ConstructorSpecifier;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeContext;
import com.starview.platform.data.type.ITypeDescription;
import com.starview.platform.data.type.IValueSpecifier;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.TypeExp;

/*
 * Copyright (C) 2013 Starview Inc
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

  public static void declare(ITypeContext cxt)
  {

    Location nullLoc = Location.nullLoc;

    IType conType = TypeUtils.constructorType(type);
    ConstructorSpecifier permSpec = new ConstructorSpecifier(nullLoc, null, NoPermission.label, NoPermission.conIx,
        conType, NoPermission.class, Reason.class);
    ConstructorSpecifier busySpec = new ConstructorSpecifier(nullLoc, null, NoPermission.label, NoPermission.conIx,
        conType, NoPermission.class, Reason.class);

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(permSpec);
    specs.add(busySpec);

    ITypeDescription desc = new CafeTypeDescription(nullLoc, type, Reason.class.getName(), specs);

    cxt.defineType(desc);
  }
}
