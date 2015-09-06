package org.star_lang.star.data.type;

import java.util.Collection;

/*
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

public interface IAlgebraicType extends ITypeDescription
{

  /**
   * Return the list of value specifiers associated with this type. Note that while algebraic types
   * may have any number of value specifiers associated with them, types such as the array type or
   * the function types may have exactly one value specifier. If a type description has no value
   * specifiers then there is no official method for creating values of that type.
   * 
   * @return a list of the value specifiers associated with a type.
   */
  Collection<IValueSpecifier> getValueSpecifiers();

  /**
   * Find a specific named value specifier with a given label. This will only return a value
   * specifier that HAS a label (i.e., only algebraic types).
   * 
   * @param label
   * @return the value specifier whose label is label. Returns null if it cannot find one.
   */
  IValueSpecifier getValueSpecifier(String label);

  /**
   * All of the fields and types defined in this type
   * 
   * @return
   */
  TypeInterface getTypeInterface();
}
