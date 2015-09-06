package org.star_lang.star.compiler.type;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeVisitor;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

/**
 * This checks to see if a type expression has any type variables in it.
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
  public void visitTupleType(TupleType t, Void cxt)
  {
    for (IType argType : t.getElTypes())
      if (flag)
        argType.accept(this, cxt);
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
