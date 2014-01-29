package org.star_lang.star.compiler.canonical;

import com.starview.platform.data.type.IType;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public interface IContentExpression extends Canonical
{

  /**
   * Expressions have a type associated with them. This returns the type of the expression.
   * 
   * @return an expression denoting the type of the expression.
   */

  IType getType();

  /**
   * Allow a transformer to transform this expression
   * 
   * @param transform
   * @return the transformed entity. May not be an expression, depends on the transform
   */
  <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context);
}
