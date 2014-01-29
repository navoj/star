package com.starview.platform.data;

/**
 * The IRelation is primarily a marker for the {@link Iterable} interface.
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
public interface IRelation extends Iterable<IValue>, IValue
{
  /**
   * Find out if the relation is empty.
   * 
   * @return true if the relation is known to be empty
   */
  boolean isEmpty();

  /**
   * Return a measure of the size of the relation. Note that this operation is likely to be
   * expensive.
   * 
   * @return the number of elements in the relation
   * 
   */
  int size();

  /**
   * Construct a 'shallow' copy of the relation. I.e., just copy the spine of the relation such that
   * modifying elements of the copy does not affect the original.
   * 
   * @return the copied relation. Any insertions, or deletions, of entries in the copy does not
   *         affect the original (and vice-versa).
   * @throws EvaluationException
   */
  @Override
  IRelation shallowCopy() throws EvaluationException;

  /**
   * Apply a transform to the relation to get a new one
   * 
   * @param transform
   * @return the new relation
   * @throws EvaluationException
   */
  IRelation mapOver(IFunction transform) throws EvaluationException;

  /**
   * Apply a filter to reduce the relation
   * 
   * @param filter
   *          a function that returns true for elements to keep
   * @return the new relation
   * @throws EvaluationException
   */
  IRelation filter(IFunction test) throws EvaluationException;

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
   * Test for equality using a supplied equality function
   * 
   * @param other
   * @param test
   * @return true if the elements are equal, according to the equality function
   * @throws EvaluationException
   */
  boolean equals(IRelation other, IFunction test) throws EvaluationException;

  /**
   * Add a new tuple to the relation.
   * 
   * @param tuple
   *          the new tuple in the relation. No check is implied to see if there is already an
   *          equivalent tuple in the relation.
   * @return the new relation that results
   */
  IRelation addCell(IValue tuple) throws EvaluationException;

  /**
   * Concatenate a relation to the relation
   * 
   * @param sub
   *          the list to merge with this relation
   * @return
   * @throws EvaluationException
   */
  IRelation concat(IRelation sub) throws EvaluationException;

  /**
   * remove all elements from a relation that match a given pattern
   * 
   * @param filter
   * @return the modified relation
   * @throws EvaluationException
   */
  IRelation deleteUsingPattern(IPattern filter) throws EvaluationException;

  /**
   * Replace elements that match a pattern with new elements
   * 
   * @param filter
   * @param transform
   * @return
   * @throws EvaluationException
   */
  IRelation updateUsingPattern(IPattern filter, IFunction transform) throws EvaluationException;
}
