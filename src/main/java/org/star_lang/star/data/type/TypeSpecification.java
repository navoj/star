package org.star_lang.star.data.type;

import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * The specification of a type. This is used in type interfaces to determine the types defined in
 * that interface.
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
public interface TypeSpecification extends PrettyPrintable
{
  /**
   * What is the NAME of this type?
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
