package org.star_lang.star.data;

import org.star_lang.star.data.indextree.Fold;
import org.star_lang.star.data.indextree.Sets;

import java.util.Iterator;
import java.util.Map.Entry;

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

public interface ISet extends IValue, Iterable<IValue> {
  /**
   * Is the set empty?
   *
   * @return
   */
  boolean isEmpty();

  /**
   * How many elements in the set?
   *
   * @return the number of elements
   */
  public int size();

  /**
   * test to see if an element is present
   *
   * @param key
   * @return true if there is
   */
  boolean contains(IValue key);

  /**
   * Create a new Mapping that results from adding an element to the set.
   *
   * @param key
   * @return a new set. It is required that this operation does NOT affect the original set.
   */
  ISet insert(IValue key);

  /**
   * create a new mapping that results from deleting any mapping associated with the key. It is not
   * required that there previously existed an entry for the key.
   *
   * @param key
   * @return a new set with no entry in it for the key. It is required that this operation does
   * not affect the original set.
   */
  ISet delete(IValue key);

  @Override
  ISet copy() throws EvaluationException;

  /**
   * Fold a lambda-like object over the entries in the map
   *
   * @param folder the function to apply
   * @param init   value of the state
   * @return the result of applying the function successively to elements of the map
   */
  <S> S fold(Fold<IValue, S> folder, S init);


  /**
   * Test for equality using a supplied equality function
   *
   * @param other
   * @return true if the maps are equal
   * @throws EvaluationException
   */
  boolean equals(ISet other) throws EvaluationException;

  /**
   * Filter out unwanted elements from a map.
   *
   * @param filter a pattern that is satisfied for pairs of key/values that should be deleted
   * @return the new map with the unwanted elements filtered out
   * @throws EvaluationException
   */
  ISet filterOut(IPattern filter) throws EvaluationException;

  /**
   * Convert a set into another form
   *
   * @param trans the function to apply
   * @return the result of applying the transformer function
   * @throws EvaluationException
   */
  ISet map(IFunction trans) throws EvaluationException;

  /**
   * Update set with new elements based on a combination of a filter pattern and a transform function
   *
   * @param filter    elements that match are transformed
   * @param transform function to apply on matched elements
   * @return the new set
   * @throws EvaluationException
   */

  ISet updateUsingPattern(IPattern filter, IFunction transform) throws EvaluationException;


  /**
   * Allow a set to be traversed in the reverse direction as well as the forward direction
   *
   * @return an iterator that goes backwards
   */

  Iterator<IValue> reverseIterator();
}