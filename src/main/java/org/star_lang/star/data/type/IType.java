package org.star_lang.star.data.type;

import java.io.Serializable;

/**
 * An IType is a type expression -- it denotes a type of some form.
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
