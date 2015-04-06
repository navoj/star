package org.star_lang.star.compiler.canonical;

import org.star_lang.star.data.type.IType;

/*
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
public interface IContentAction extends Canonical
{
  void accept(ActionVisitor visitor);

  /**
   * Allow a transformer to transform this action
   * 
   * @param transform
   * @param context TODO
   * @return the transformed entity. May not be an action, depends on the transform
   */
  <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context);

  /**
   * Actions have a type associated with them. This is generally of the form
   * 
   * <pre>
   * action of %t
   * </pre>
   * 
   * where %t is a state type associated with the action.
   * 
   * @return an expression denoting the type of the expression.
   */

  IType getType();
}
