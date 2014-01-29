package org.star_lang.star.compiler.type;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.starview.platform.data.type.ExistentialType;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeVisitor;
import com.starview.platform.data.type.Type;
import com.starview.platform.data.type.TypeExp;
import com.starview.platform.data.type.TypeInterfaceType;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;

/**
 * This checks to see if a type expression has any type variables in it.
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
public class TypeClosed implements ITypeVisitor<Void>
{
  private final Set<String> exclusions = new HashSet<>();
  private Boolean flag = true;

  public static boolean isTypeClosed(IType type)
  {
    TypeClosed finder = new TypeClosed();
    type.accept(finder, null);
    return finder.flag;
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, Void cxt)
  {
    for (Entry<String, IType> entry : t.getAllTypes().entrySet()) {
      if (flag) {
        IType te = TypeUtils.deRef(entry.getValue());
        if (te instanceof TypeVar) {
          TypeVar tv = (TypeVar) te;
          if (!exclusions.contains(tv.getVarName())) {
            flag = false;
            return;
          }
        }
      }
    }
    for (Entry<String, IType> entry : t.getAllFields().entrySet()) {
      if (flag)
        entry.getValue().accept(this, cxt);
    }
  }

  @Override
  public void visitSimpleType(Type t, Void cxt)
  {
  }

  @Override
  public void visitTypeExp(TypeExp t, Void cxt)
  {
    for (IType argType : t.getTypeArgs())
      if (flag)
        argType.accept(this, cxt);
    t.getTypeCon().accept(this, cxt);
  }

  @Override
  public void visitTypeVar(TypeVar v, Void cxt)
  {
    IType t = v.deRef();

    if (t instanceof TypeVar) {
      v = (TypeVar) t;
      if (!exclusions.contains(v.getVarName()))
        flag = false;
    } else
      t.accept(this, cxt);
  }

  @Override
  public void visitExistentialType(ExistentialType t, Void cxt)
  {
    String varName = t.getBoundVar().getVarName();
    exclusions.add(varName);

    t.getBoundType().accept(this, cxt);
    exclusions.remove(varName);
  }

  @Override
  public void visitUniversalType(UniversalType t, Void cxt)
  {
    String varName = t.getBoundVar().getVarName();
    exclusions.add(varName);

    t.getBoundType().accept(this, cxt);
    exclusions.remove(varName);
  }
}
