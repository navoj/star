package org.star_lang.star.operators;

import org.star_lang.star.data.type.IType;

/**
 * Cafe built-in functions implement the ICafeBuiltin interface.
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
