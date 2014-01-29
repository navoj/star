package org.star_lang.star.operators;

import org.star_lang.star.compiler.cafe.compile.ISpec;

/**
 * Expose a static variable to cafe functions.
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

public interface ICafeBuiltinVar extends ISpec
{
  /**
   * The name of the variable
   * 
   * @return
   */
  String getName();

  /**
   * Which class does this variable belong to?
   * 
   * @return
   */
  Class<?> getJavaOwner();

  /**
   * The java name of the builtin. This name must obey the Java rules for identifiers.
   */
  String getJavaName();

  /**
   * Return the class name that implements this builtin function
   * 
   * @return
   */
  Class<?> getImplClass();
}
