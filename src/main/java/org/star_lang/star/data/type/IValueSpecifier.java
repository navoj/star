package org.star_lang.star.data.type;

import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * An IValueSpecifier is a specification of constructed values. A given type may have more than one
 * IValueSpecifier; however, certain types must have exactly one value specifier: specifically
 * {@link ScalarSpecifier}s may not be mixed with {@link ConstructorSpecifier}s.
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
public interface IValueSpecifier extends PrettyPrintable
{
  /**
   * The label of the value specifier
   * 
   * @return
   */
  String getLabel();

  /**
   * Every constructor has a type. That type is the same type that would be needed were the
   * constructor to be a 'real' function; which of course it is.
   * 
   * @return the type of the function
   */
  IType getConType();

  /**
   * Where the constructor is defined, in some source file
   * 
   * @return
   */
  Location getLoc();

  /**
   * Access the constructor code itself
   * 
   * @return
   */
  IContentExpression source();
}
