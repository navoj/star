package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.type.Visibility;

/**
 * A statement in a theta environment
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
public interface IStatement extends Canonical
{
  /**
   * Test to see if this statement is 'about' a given name
   * 
   * @param name
   * @return true if the statement is a definition for the name
   */
  boolean defines(String name);

  /**
   * Return a list of all the defined names. You cannot infer any type information from this list.
   * 
   * @return a Collection<String> of the defined names.
   */
  Collection<String> definedFields();

  /**
   * Return a list of defined types.
   * 
   * @return a Collection<String> of type names;
   */
  Collection<String> definedTypes();

  /**
   * Does this statement define something that should not be exported from the theta environment
   * that the statement is in?
   */
  Visibility getVisibility();

  /**
   * Allow a transformer to transform this definition
   * 
   * @param transform
   * @param context
   * @return the transformed entity. Might not result in a statement, depends on the transform
   */
  <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context);
}
