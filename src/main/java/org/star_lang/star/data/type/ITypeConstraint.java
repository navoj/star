package org.star_lang.star.data.type;

import java.io.Serializable;
import java.util.Collection;

import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;

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
public interface ITypeConstraint extends Serializable
{
  Collection<TypeVar> affectedVars();

  void checkBinding(IType candidate, Location loc, Dictionary cxt) throws TypeConstraintException;

  boolean sameConstraint(ITypeConstraint other, Location loc, Dictionary cxt) throws TypeConstraintException;

  <X> void accept(ITypeVisitor<X> visitor, X cxt);

  void showConstraint(DisplayType disp);

  <T, C, X> C transform(TypeTransformer<T, C, X> trans, X cxt);
}