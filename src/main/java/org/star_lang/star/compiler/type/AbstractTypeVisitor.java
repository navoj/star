package org.star_lang.star.compiler.type;

import java.util.Map.Entry;
import java.util.Stack;

import com.starview.platform.data.type.ExistentialType;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeVisitor;
import com.starview.platform.data.type.Type;
import com.starview.platform.data.type.TypeExp;
import com.starview.platform.data.type.TypeInterfaceType;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;

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
public abstract class AbstractTypeVisitor<C> implements ITypeVisitor<C>
{
  private final Stack<String> exclusions = new Stack<String>();

  @Override
  public void visitSimpleType(Type t, C cxt)
  {
  }

  @Override
  public void visitTypeVar(TypeVar v, C cxt)
  {
    IType type = v.getBoundValue();
    if (type != null)
      type.accept(this, cxt);
  }

  @Override
  public void visitTypeExp(TypeExp t, C cxt)
  {
    t.getTypeCon().accept(this, cxt);
    for (IType arg : t.getTypeArgs())
      arg.accept(this, cxt);
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, C cxt)
  {
    for (Entry<String, IType> entry : t.getAllFields().entrySet())
      entry.getValue().accept(this, cxt);
  }

  @Override
  public void visitExistentialType(ExistentialType t, C cxt)
  {
    exclusions.push(t.getBoundVar().getVarName());
    t.getBoundType().accept(this, cxt);
    exclusions.pop();
  }

  @Override
  public void visitUniversalType(UniversalType t, C cxt)
  {
    exclusions.push(t.getBoundVar().getVarName());
    t.getBoundType().accept(this, cxt);
    exclusions.pop();
  }

  protected boolean isExcluded(String name)
  {
    return exclusions.contains(name);
  }
}
