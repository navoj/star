package org.star_lang.star.data.type;

import java.io.Serializable;

/**
 * An IType is a type expression -- it denotes a type of some form.
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

public interface IType extends Serializable, TypeTransformable
{
  /**
   * Every type has a name. It is not generally the same as toString().
   * 
   * @return the label associated with the type.
   */
  String typeLabel();

  /**
   * What kind of type is this type?
   * 
   * @return
   */
  Kind kind();

  /**
   * Visitor accept pattern for type expressions
   * 
   * @param visitor
   * @param cxt
   *          TODO
   */
  <C> void accept(ITypeVisitor<C> visitor, C cxt);
}
