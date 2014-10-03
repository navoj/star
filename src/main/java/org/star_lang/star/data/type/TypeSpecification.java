package org.star_lang.star.data.type;

import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * The specification of a type. This is used in type interfaces to determine the types defined in
 * that interface.
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
public interface TypeSpecification extends PrettyPrintable
{
  /**
   * What is the name of this type?
   * 
   * @return
   */
  String getName();

  /**
   * What kind of type is this?
   * 
   * @return
   */
  Kind kind();

  /**
   * A type may be generic, in which case the {@code typeArity} of the type will be greater than
   * zero.
   * 
   * @return the number of type arguments type expressions of this type should have.
   */
  int typeArity();

  /**
   * Return a template of the type that this description defines. If the type is generic, then this
   * function will return a universally quantified type.
   * 
   * @return a template of the type defined by this description.
   */
  IType getType();
}
