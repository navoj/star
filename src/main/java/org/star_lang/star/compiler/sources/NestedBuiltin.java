package org.star_lang.star.compiler.sources;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.operators.ICafeBuiltin;

import com.starview.platform.data.IFunction;
import com.starview.platform.data.type.IType;
/**
 * 
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
 * 
 * @author fgm
 *
 */

public final class NestedBuiltin implements ICafeBuiltin
{
  private final String name;
  private final IType type;
  private final Class<? extends IFunction> klass;

  public NestedBuiltin(String name, IType type, Class<? extends IFunction> klass)
  {
    this.name = name;
    this.type = type;
    this.klass = klass;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getJavaName()
  {
    return Utils.javaIdentifierOf(name);
  }

  @Override
  public String getJavaType()
  {
    return Utils.javaInternalClassName(klass);
  }

  @Override
  public String getJavaSig()
  {
    String sig = Utils.javaTypeSig(klass);
    return sig;
  }

  @Override
  public String getJavaInvokeName()
  {
    return "enter";
  }

  @Override
  public String getJavaInvokeSignature()
  {
    return Utils.javaInvokeSig(klass, "enter");
  }

  @Override
  public boolean isStatic()
  {
    return false;
  }

  @Override
  public Class<?> getImplClass()
  {
    return klass;
  }
}