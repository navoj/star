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
 * The occurs check is an important part of type unification, as it prevents circular type
 * expressions being formed.
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
