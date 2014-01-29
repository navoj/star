package com.starview.platform.data.type;

import java.util.Collection;

/**
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
