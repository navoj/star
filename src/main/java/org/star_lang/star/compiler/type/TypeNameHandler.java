package org.star_lang.star.compiler.type;

import java.util.Map;

import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;

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
public interface TypeNameHandler
{
  IType typeByName(String name, Location loc);

  IType newTypeVar(String name, Location loc, Kind kind);

  void defineType(String name, TypeVar v);

  void addEntries(Map<String, TypeVar> sub);

  void removeEntries(Map<String, TypeVar> rem);

  void removeTypeVar(String var);

  AccessMode access();

  Map<String, TypeVar> typeVars();

  boolean suppressWarnings();

  TypeNameHandler fork();
}
