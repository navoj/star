package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * An ITypeDescription is the programmer's key to accessing the meta-level aspects of an IValue. It
 * contains enough information to permit introspection and to permit the Factory to create
 * appropriate instances of the type.
 */
/* 
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
public interface ITypeDescription extends PrettyPrintable
{
  /**
   * The name of this type. Note that a type expression may involve type variables, so a type's name
   * is not sufficient information to construct instances of a type expression.
   * 
   * @return the name of the type
   */
  String getName();

  /**
   * Where was this type defined?
   * 
   * @return
   */
  Location getLoc();

  /**
   * A type may be generic, in which case the {@code typeArity} of the type will be greater than
   * zero.
   * 
   * @return the number of type arguments type expressions of this type should have.
   */
  int typeArity();

  /**
   * Compute a generalized version of the type
   * 
   * @return
   */
  IType getType();

  /**
   * Return what kind of type this is
   * 
   * @return
   */
  Kind kind();

  /**
   * Verify that a given type expression is consistent with this description
   * 
   * @param type
   * @param loc
   * @param dict
   */
  IType verifyType(IType type, Location loc, Dictionary dict) throws TypeConstraintException;
}
