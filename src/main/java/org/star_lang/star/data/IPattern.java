package org.star_lang.star.data;

import org.star_lang.star.data.type.IType;

/**
 * The {@link IPattern} interface is implemented by pattern code: pattern abstractions
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

public interface IPattern
{
  /**
   * Enter a pattern and return the result as a tuple of extracted values.
   * 
   * @param arg
   *          the value that the pattern will match against
   * 
   * @return null if the pattern fails, otherwise a tuple of IValues representing the extracted
   *         values
   */
  IValue match(IValue arg) throws EvaluationException;

  /**
   * All programs expose a means of getting their type
   * 
   * @return
   */
  IType getType();
}
