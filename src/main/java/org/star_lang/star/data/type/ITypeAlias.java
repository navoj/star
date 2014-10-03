package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.Dictionary;

/**
 * A TypeAlias is a mapping between type expressions. It permits a programmer to specify type
 * aliases of the form:
 * 
 * type llist of %t is alias of list of list of %t
 * 
 * A TypeAlias can be viewed as the definition of an existentially quantified type.
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
public interface ITypeAlias extends ITypeDescription
{
  /**
   * Apply the alias to a potential type expression to acquire the aliased type expression.
   * 
   * @param pattern
   *          the type to test against the alias
   * @param loc
   *          where the alias is being applied
   * @param cxt
   *          the context of the aliasing
   * @return the aliased type
   * @throws TypeConstraintException
   *           if the pattern does not apply.
   */
  IType apply(IType pattern, Location loc, Dictionary cxt) throws TypeConstraintException;
}
