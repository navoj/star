package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.TypeVar;

/**
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
public class ConstraintFinder extends AbstractTypeVisitor<Void>
{
  private List<ITypeConstraint> constraints = new ArrayList<>();
  private Set<TypeVar> vars = new HashSet<>();

  public static List<ITypeConstraint> findConstraints(IType type)
  {
    ConstraintFinder finder = new ConstraintFinder();
    type.accept(finder, null);

    return finder.getConstraints();
  }

  public static List<ITypeConstraint> findConstraints(Collection<IType> types)
  {
    ConstraintFinder finder = new ConstraintFinder();
    for (IType type : types)
      type.accept(finder, null);

    return finder.getConstraints();
  }

  private List<ITypeConstraint> getConstraints()
  {
    return constraints;
  }

  @Override
  public void visitTypeVar(TypeVar v, Void cxt)
  {
    IType t = v.deRef();
    if (t instanceof TypeVar) {
      v = (TypeVar) t;
      if (!vars.contains(v) && isNotExcluded(v.getVarName())) {
        vars.add(v);
        for (ITypeConstraint con : v) {
          if (!(constraints.contains(con))) {
            constraints.add(con);
            con.accept(this, cxt);
          }
        }
      }
    } else
      t.accept(this, cxt);
  }
}