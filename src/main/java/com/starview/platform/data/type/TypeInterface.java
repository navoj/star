package com.starview.platform.data.type;

import java.util.SortedMap;

import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * A TypeInterface is an abstract environment in which fields may be defined, as well as types being
 * defined.
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
