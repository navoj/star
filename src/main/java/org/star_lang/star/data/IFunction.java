package org.star_lang.star.data;

import org.star_lang.star.data.type.IType;

/**
 * The {@link IFunction} interface is implemented by code: functions and procedures.
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

public interface IFunction
{
  /**
   * Enter a function and return the result thereof
   * 
   * @param args
   *          the arguments to the function
   * 
   * @return the value returned by the function
   */
  IValue enter(IValue... args) throws EvaluationException;

  /**
   * All functions expose a means of getting their type
   * 
   * @return the type of the function
   */
  IType getType();
}
