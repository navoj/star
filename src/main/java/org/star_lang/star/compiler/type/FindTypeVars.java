package org.star_lang.star.compiler.type;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.starview.platform.data.type.ExistentialType;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeConstraint;
import com.starview.platform.data.type.ITypeVisitor;
import com.starview.platform.data.type.Type;
import com.starview.platform.data.type.TypeExp;
import com.starview.platform.data.type.TypeInterfaceType;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;

/**
 * This looks for type variables in a type expression. It also defines the 'official' order of
 * occurrence of type variables in a type expression. This is important for the meaning of a
 * quantified type.
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
public class FindTypeVars<T> implements ITypeVisitor<Void>
{
  private final Set<String> exclusions;
  private final VarHandler<T> handler;

  private FindTypeVars(VarHandler<T> handler)
  {
    this.handler = handler;
    this.exclusions = new HashSet<>();
  }

  public interface VarHandler<T>
  {
    boolean checkVar(TypeVar var);

    void foundVar(TypeVar var);

    void foundExists(String name, TypeVar var);

    T readOff();
  }

  public static <T> T findTypeVars(IType type, VarHandler<T> handler)
  {
    FindTypeVars<T> finder = new FindTypeVars<>(handler);
    type.accept(finder, null);
    return handler.readOff();
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, Void cxt)
  {
    for (Entry<String, IType> entry : t.getAllTypes().entrySet()) {
      IType te = TypeUtils.deRef(entry.getValue());
      if (te instanceof TypeVar) {
        TypeVar tv = (TypeVar) te;
        if (handler.checkVar(tv) && !exclusions.contains(tv.getVarName()))
          handler.foundExists(entry.getKey(), tv);
      }
    }
    for (Entry<String, IType> entry : t.getAllFields().entrySet()) {
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
      argType.accept(this, cxt);
    t.getTypeCon().accept(this, cxt);
  }

  @Override
  public void visitTypeVar(TypeVar v, Void cxt)
  {
    IType t = v.deRef();

    if (t instanceof TypeVar) {
      v = (TypeVar) t;
      String varName = v.getVarName();
      if (handler.checkVar(v) && !exclusions.contains(varName)) {
        exclusions.add(varName);
        handler.foundVar(v);
        for (ITypeConstraint con : v)
          con.accept(this, cxt);
      }
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
