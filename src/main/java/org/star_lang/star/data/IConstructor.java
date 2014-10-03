package org.star_lang.star.data;

/* 
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
/**
 * The {@code IConstructor} interface is implemented by so-called constructor functions defined in
 * algebraic data-types.
 * 
 * @author fgm
 * 
 */

public interface IConstructor extends IValue
{
  /**
   * Every constructor has an index that denotes a code within an algebraic data type
   * 
   * @return
   */
  int conIx();

  /**
   * Get the label that identifies which constructor was used to build this value
   * 
   * @return a string label
   */
  String getLabel();

  /**
   * See how many elements are in the constructor
   * 
   * @return the number of elements in the constructor. A return value of 0 indicates an enumerated
   *         symbol.
   */
  int size();

  /**
   * Return the value corresponding to a given index
   * 
   * @param index
   * @return the nth argument of the constructor.
   */
  IValue getCell(int index);

  /**
   * Return an array of all values
   * 
   * @return the array of arguments to the constructor
   */
  IValue[] getCells();

  /**
   * Set the cell at a specific position to a new value. Should NEVER be null.
   * 
   * @param index
   *          the offset of the value to update
   * @param value
   *          the new value of the constructor argument.
   * @throws EvaluationException
   *           TODO
   */
  void setCell(int index, IValue value) throws EvaluationException;

  /**
   * When constructors are copied, the copy is also an IConstructor
   */
  @Override
  IConstructor copy() throws EvaluationException;

  @Override
  IConstructor shallowCopy() throws EvaluationException;
}
