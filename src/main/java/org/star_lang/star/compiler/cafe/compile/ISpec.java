package org.star_lang.star.compiler.cafe.compile;

import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

/**
 * This interface describes the Cafe type of an entity and the Java-related representation of the
 * entity.
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
public interface ISpec extends PrettyPrintable
{
  /**
   * Return the location associated with this specification
   * 
   * @return a {@link Location}
   */
  Location getLoc();

  /**
   * Get the Star type of the source.
   * 
   * @return
   */
  IType getType();

  /**
   * Get the Java type of the source
   * 
   * @return
   */
  String getJavaType();

  /**
   * Get the Java signature of the source. This is usually equal to L+javaType+;
   * 
   * @return
   */
  String getJavaSig();

  /**
   * Get Java's invocation signature for the source - i.e., what to do to invoke the function
   * 
   * @return
   */
  String getJavaInvokeSig();

  /**
   * Get the java name of the method to invoke
   * 
   * @return
   */
  String getJavaInvokeName();

  /**
   * Return the element that represents this value in the frame state
   * 
   * @return
   */
  Object getFrameCode();

  /**
   * How many slots does this value take?
   * 
   * @return
   */
  int slotSize();
}
