package org.star_lang.star.data.type;

import java.util.SortedMap;

import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * A TypeInterface is an abstract environment in which fields may be defined, as well as types being
 * defined.
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
public interface TypeInterface extends PrettyPrintable
{
  /**
   * Get the type of an individual field in the interface
   * 
   * @param name
   * @return null if name not defined, otherwise the type associated with the field
   */
  IType getFieldType(String name);

  /**
   * All the fields in this interface.
   * 
   * @return a Map of all the fields in the interface
   */
  SortedMap<String, IType> getAllFields();

  /**
   * How many fields are there? It may be cheaper to compute this than sizing the map of fields.
   * 
   * @return the number of fields in this TypeInterface
   */
  int numOfFields();

  /**
   * Pick up the specification of an individual type
   * 
   * @param name
   * @return null if the type is not known, otherwise its specification
   */
  IType getType(String name);

  /**
   * All the types defined in this interface
   * 
   * @return
   */
  SortedMap<String, IType> getAllTypes();

  /**
   * How many types are defined in this TypeInterface?
   * 
   * @return
   */
  int numOfTypes();
}
