package org.star_lang.star.compiler.type;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;

/**
 * An implementation of the @{link ITypeVisitor} that verifies the so-called occurs-check (that a
 * given type expression does not depend on a particular type variable).
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

public class OccursCheck extends AbstractTypeVisitor<Void>
{
  private final TypeVar var;
  private boolean found = false;
  private Set<String> visited = new HashSet<>();
  private final boolean occursCheck;

  private OccursCheck(TypeVar var, boolean occursCheck)
  {
    this.var = var;
    this.occursCheck = occursCheck;
  }

  public static boolean occursCheck(IType type, TypeVar var)
  {
    OccursCheck finder = new OccursCheck(var, true);
    if (!type.equals(var))
      type.accept(finder, null);
    return finder.found;
  }

  public static boolean occursIn(IType type, TypeVar var)
  {
    OccursCheck finder = new OccursCheck(var, false);
    if (!type.equals(var))
      type.accept(finder, null);
    return finder.found;
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, Void cxt)
  {
    for (Entry<String, IType> entry : t.getAllFields().entrySet())
      entry.getValue().accept(this, cxt);
  }

  @Override
  public void visitTypeVar(TypeVar var, Void cxt)
  {
    IType type = var.deRef();

    if (type instanceof TypeVar) {
      var = (TypeVar) type;
      String varName = var.getVarName();

      if (isNotExcluded(varName)) {
        if (!visited.contains(varName)) {
          visited.add(varName);
          if (var.equals(this.var))
            found = true;
          else
            for (ITypeConstraint cons : var)
              cons.accept(this, cxt);
        }
      }
    } else
      type.accept(this, cxt);
  }

  @Override
  public void visitTypeExp(TypeExp t, Void cxt)
  {
    t.getTypeCon().accept(this, cxt);

    if (!found) {
      IType[] args = t.getTypeArgs();
      for (int ix = 0; !found && ix < args.length; ix++) {
        IType arg = TypeUtils.deRef(args[ix]);
        if (TypeUtils.isDetermines(arg)) {
          if (!occursCheck)
            arg.accept(this, cxt);
        } else
          arg.accept(this, cxt);
      }
    }
  }

  @Override
  public void visitTupleType(TupleType t, Void cxt)
  {
    IType[] args = t.getElTypes();
    for (int ix = 0; !found && ix < args.length; ix++) {
      IType arg = TypeUtils.deRef(args[ix]);
      if (TypeUtils.isDetermines(arg)) {
        if (!occursCheck)
          arg.accept(this, cxt);
      } else
        arg.accept(this, cxt);
    }
  }
}
