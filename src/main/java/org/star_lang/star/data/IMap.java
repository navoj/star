package org.star_lang.star.data;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A map value. Supports indexed access to elements based on a key.
 * <p>
 * Null keys or values are not permitted.
 * <p>
 * Copyright (c) 2015. Francis G. McCabe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public interface IMap extends IValue, Iterable<Entry<IValue, IValue>> {

  /**
   * Test to see if the map contains a pairing for a particular key
   *
   * @param key
   * @return true if there is a value associate with the key
   */
  boolean contains(IValue key);

  /**
   * Get the value associated with a key.
   *
   * @param key
   * @return Return the value associated with member. If there is no value, return null.
   */
  IValue getMember(IValue key);

  /**
   * Set up a pairing for a key and value.
   *
   * @param key   the key of the pair. May not be null.
   * @param value the value associated with the key. May not be null.
   * @return the new map with the new pairing in place.
   * @throws EvaluationException if cannot set entry in map
   */
  IMap setMember(IValue key, IValue value) throws EvaluationException;

  /**
   * Remove a pairing for a key and value.
   *
   * @param key the key of the pair.
   * @return the new map with any pairing for the key removed.
   * @throws EvaluationException if cannot remove entry from the map
   */
  IMap removeMember(IValue key) throws EvaluationException;

  /**
   * Determine the size of the map
   *
   * @return the number of elements in the map
   */
  int size();

  /**
   * Is the map empty.
   */
  boolean isEmpty();

  @Override
  IMap copy() throws EvaluationException;

  /**
   * Test for equality using a supplied equality function
   *
   * @param other
   * @param valQ  test for value equality
   * @return true if the maps are equal
   * @throws EvaluationException
   */
  boolean equals(IMap other, IFunction valQ) throws EvaluationException;

  /**
   * Filter out unwanted elements from a map.
   *
   * @param filter a pattern that is satisfied for pairs of key/values that should be deleted
   * @return the new map with the unwanted elements filtered out
   * @throws EvaluationException
   */
  IMap filterOut(IPattern filter) throws EvaluationException;

  /**
   * Update matching elements with new values.
   *
   * @param filter    a pattern that is satisfied for pairs that should be updated
   * @param transform a function from a key/value pair to a new key/value pair. Note that both the key and
   *                  the value may be adjusted
   * @return the new map
   * @throws EvaluationException
   */
  IMap update(IPattern filter, IFunction transform) throws EvaluationException;

  /**
   * Allow a map to be traversed in the reverse direction as well as the forward direction
   *
   * @return
   */

  Iterator<Entry<IValue, IValue>> reverseIterator();

  /**
   * Pick an element at random (not guaranteed)
   *
   * @return
   */
  IValue pick();

  /**
   * Pick an element and return the rest
   *
   * @return
   */
  IMap remaining();
}