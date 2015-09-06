package org.star_lang.star.compiler.type;

import java.util.Map;

import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;

/*
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
