package org.star_lang.star.compiler.ast;

import java.util.List;
import java.util.Map;

import com.starview.platform.data.IConstructor;
import com.starview.platform.data.type.Location;

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

  public enum astType {
    Bool, Char, Int, Long, Flt, Dec, Str, Name, Apply
  };
}
