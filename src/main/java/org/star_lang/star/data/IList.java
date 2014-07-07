package org.star_lang.star.data;

public interface IList extends IValue, Iterable<IValue>
{
  /**
   * True if the array has no elements
   * 
   * Copyright (C) 2013 Starview Inc
   * 
   * This library is free software; you can redistribute it and/or modify it under the terms of the
   * GNU Lesser General Public License as published by the Free Software Foundation; either version
   * 2.1 of the License, or (at your option) any later version.
   * 
   * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
   * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
   * the GNU Lesser General Public License for more details.
   * 
   * You should have received a copy of the GNU Lesser General Public License along with this
   * library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
   * Boston, MA 02110-1301 USA
   * 
   * @return true if the list is empty
   */
  boolean isEmpty();

  /**
   * See how many elements are in the array
   * 
   * @return the number of elements in the list
   */
  int size();

  /**
   * Return the value corresponding to a given index
   * 
   * @param index
   * @return the value at the indicated position. The first offset is 0.
   */
  IValue getCell(int index);

  /**
   * The substitute function returns a new list value which is equal to the original but with the
   * indicated cell replaced by a new value
   * 
   * This function is guaranteed not to affect the original array
   * 
   * @param index
   *          the index of the cell to replace
   * @param value
   *          the replacement value for the cell
   * @return a new array.
   */
  IList substituteCell(int index, IValue value);

  /**
   * Add an element to the front
   * 
   * @param el
   * @throws EvaluationException
   */
  IList consCell(IValue el) throws EvaluationException;

  /**
   * Drop the first element of the list
   * 
   * @return the remainder of the list
   */
  IList tail();

  /**
   * Apply a transform to the relation to get a new one
   * 
   * @param transform
   * @return the new indexed relation
   * @throws EvaluationException
   */
  IValue mapOver(IFunction transform) throws EvaluationException;

  /**
   * Apply a left associative reducing fold to the relation
   * 
   * @param transform
   * @param init
   *          the initial value of passed into the fold function as a 'zero'
   * @return the final result value by evaluating the transform for each element
   * @throws EvaluationException
   */
  IValue leftFold(IFunction transform, IValue init) throws EvaluationException;

  /**
   * Apply a left associative reducing fold to the relation
   * 
   * @param transform
   * @return the final result value by evaluating the transform for each element
   * @throws EvaluationException
   */
  IValue leftFold1(IFunction transform) throws EvaluationException;

  /**
   * Apply a right associative reducing fold to the relation
   * 
   * @param transform
   * @param init
   *          the initial value of passed into the fold function as a 'zero'
   * @return the final result value by evaluating the transform for each element
   * @throws EvaluationException
   */
  IValue rightFold(IFunction transform, IValue init) throws EvaluationException;

  /**
   * Apply a right associative reducing fold to the relation
   * 
   * @param transform
   * @return the final result value by evaluating the transform for each element
   * @throws EvaluationException
   */
  IValue rightFold1(IFunction transform) throws EvaluationException;

  /**
   * Test for equality using a supplied equality function
   * 
   * @param other
   * @param test
   * @return true if the elements are equal, according to the equality function
   * @throws EvaluationException
   */
  boolean equalTo(IList other, IFunction test) throws EvaluationException;

}
