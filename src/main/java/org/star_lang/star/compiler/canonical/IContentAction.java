package org.star_lang.star.compiler.canonical;

import org.star_lang.star.data.type.IType;

/*
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
public interface IContentAction extends Canonical {
  void accept(ActionVisitor visitor);

  /**
   * Allow a transformer to transform this action
   *
   * @param transform
   * @param context   cotext of transformation
   * @param context   cotext of transformation
   * @return the transformed entity. May not be an action, depends on the transform
   */
  <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context);

  /**
   * Actions have a type associated with them. This is generally of the form
   * <p>
   * <pre>
   * action of %t
   * </pre>
   * <p>
   * where %t is a state type associated with the action.
   *
   * @return an expression denoting the type of the expression.
   */

  IType getType();
}
