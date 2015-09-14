package org.star_lang.star.operators;

import org.star_lang.star.data.type.IType;

/**
 * Cafe built-in functions implement the ICafeBuiltin interface.
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

public interface ICafeBuiltin
{
  /**
   * The name of the built-in function
   * 
   * @return
   */
  String getName();

  /**
   * All built-ins expose a means of getting their type
   * 
   * @return the type of the built-in
   */
  IType getType();

  /**
   * The java name of the built-in. This name must obey the Java rules for identifiers.
   */
  String getJavaName();

  /**
   * The Java type of the function
   */
  String getJavaType();

  /**
   * The java descriptor of the function
   */
  String getJavaSig();

  /**
   * The name of the Java method to invoke
   * 
   * @return
   */
  String getJavaInvokeName();

  /**
   * The java invoke signature of the function
   */
  String getJavaInvokeSignature();

  /**
   * Should this be invoked as a static function? Implies that the builtin cannot be the value of a
   * variable
   * 
   * @return
   */
  boolean isStatic();

  /**
   * Return the class name that implements this built-in function
   * 
   * @return the implementation class for this builtin
   */
  Class<?> getImplClass();
}
