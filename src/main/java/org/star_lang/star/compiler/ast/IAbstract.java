package org.star_lang.star.compiler.ast;

import java.util.List;
import java.util.Map;

import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.type.Location;



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

/**
 * Interface defining abstract syntax tree
 */
public interface IAbstract extends IConstructor
{
  void accept(IAbstractVisitor visitor);

  Location getLoc();

  void setLoc(Location loc);

  IAttribute getAttribute(String att);

  boolean hasAttribute(String att);

  IAttribute setAttribute(String att, IAttribute attribute);

  Map<String, IAttribute> getAttributes();

  List<String> getCategories();

  void setCategory(String category);

  boolean isCategory(String category);

  astType astType();

  enum astType {
    Bool, Int, Long, Flt, Dec, Str, Name, Apply
  }
}
