package org.star_lang.star.data;

/**
 *  The IArray interface is implemented by array-like objects.
 *   Copyright (C) 2013 Starview Inc
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 * @author fgm
 *
 */

import java.util.Collection;

public interface IArray extends IList
{

  /**
   * Set the cell at a specific position to a new value. Should NEVER be null.
   * 
   * @param index
   * @param value
   * @throws IndexOutOfBoundsException
   *           when attempting to set a value outside the current bounds of the array.
   */
  @Deprecated
  void setCell(int index, IValue value);

  /**
   * Splice another array into this one.
   * 
   * @param sub
   *          the array to splice into this array
   * @param index
   *          the index where the first (and subsequent) elements will be spliced in. A value of 0
   *          results in the sub-array being placed in at the start of the array and a value of
   *          {@code size()} results in the sub-array being put at the end of the array.
   * @param to
   *          the index of the first element that forms the remainder of the array
   * @return a new array consisting of the sub array spliced into this array at point index.
   */
  IArray spliceList(IArray sub, int index, int to);

  /**
   * Concatenate an array to the end
   * 
   * @param sub
   *          the array to merge with this array
   * @return
   */
  IArray concatList(IArray sub);

  /**
   * Extract a sub-array from a array.
   * 
   * @param from
   *          the index of the first element to extract
   * @param to
   *          the index of the first element not in the extracted array
   * @return the sub-array. If count is greater than the remaining length of the array then the
   *         whole tail will be returned.
   * @throws IndexOutOfBoundsException
   *           if the start index is not in range.
   */
  IArray slice(int from, int to);

  /**
   * Return a sub-array consisting of all the remaining elements after the front n elements
   * 
   * @param from
   *          the number of elements to 'skip' in creating the sub-array.
   * @return a sub-array
   * @throws IndexOutOfBoundsException
   */
  IArray slice(int from);

  /**
   * return a new array with the elements of this array reversed
   * 
   * @return
   */
  IArray reverse();

  /**
   * Add a new entry to the end of the array.
   * 
   * @param value
   *          the new value to add.
   * @throws EvaluationException
   *           if there is a problem with the addition.
   */
  IArray addCell(IValue value) throws EvaluationException;

  /**
   * Add in a collection of entries
   * 
   * @param values
   * @return the updated array
   * @throws EvaluationException
   */
  IArray addCells(Collection<IValue> values) throws EvaluationException;

  /**
   * Add an element to the front
   * 
   * @param el
   * @throws EvaluationException
   */
  @Override
  IArray consCell(IValue el) throws EvaluationException;

  /**
   * Remove a cell from the array
   * 
   * @param ix
   * @throws EvaluationException
   */
  IArray removeCell(int ix) throws EvaluationException;

  /**
   * Construct a 'shallow' copy of the array.
   * 
   * @throws EvaluationException
   */
  @Override
  IArray shallowCopy();

  /**
   * Apply a transform to the relation to get a new one
   * 
   * @param transform
   * @return the new array
   * @throws EvaluationException
   */
  @Override
  IArray mapOver(IFunction transform) throws EvaluationException;

  /**
   * Apply a filter to reduce the relation
   * 
   * @param filter
   *          a function that returns true for elements to keep
   * @return the new relation
   * @throws EvaluationException
   */
  IArray filter(IFunction test) throws EvaluationException;

  /**
   * Apply a left associative reducing fold to the relation
   * 
   * @param transform
   * @param init
   *          the initial value of passed into the fold function as a 'zero'
   * @return the final result value by evaluating the transform for each element
   * @throws EvaluationException
   */
  @Override
  IValue leftFold(IFunction transform, IValue init) throws EvaluationException;

  /**
   * Apply a right associative reducing fold to the relation
   * 
   * @param transform
   * @param init
   *          the initial value of passed into the fold function as a 'zero'
   * @return the final result value by evaluating the transform for each element
   * @throws EvaluationException
   */
  @Override
  IValue rightFold(IFunction transform, IValue init) throws EvaluationException;

  
}
