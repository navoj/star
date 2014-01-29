package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintable;

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

public interface Canonical extends PrettyPrintable
{
  /**
   * Every term has a potential location. This enables debugging and error reporting to be sensitive
   * to the actual element itself.
   * 
   * @return a Location object that denotes the source location of the term.
   */

  Location getLoc();

  /**
   * Every canonical is visitable
   * 
   * @param visitor
   */
  void accept(CanonicalVisitor visitor);
}
