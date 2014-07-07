package org.star_lang.star.data;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A map value. Supports indexed access to elements based on a key.
 * 
 * Null keys or values are not permitted.
 * 
 * The assumption behind the map interface is that it is applicative: you do not side-effect maps by
 * inserting an element; instead you return a new map iwth the new element.
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

public interface IMap extends IValue, Iterable<Entry<IValue, IValue>>
{

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
   * @param key
   *          the key of the pair. May not be null.
   * @param value
   *          the value associated with the key. May not be null.
   * @throws EvaluationException
   *           if cannot set entry in map
   * @return the new map with the new pairing in place.
   */
  IMap setMember(IValue key, IValue value) throws EvaluationException;

  /**
   * Remove a pairing for a key and value.
   * 
   * @param key
   *          the key of the pair.
   * @throws EvaluationException
   *           if cannot remove entry from the map
   * @return the new map with any pairing for the key removed.
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
   * @param valQ
   *          test for value equality
   * @return true if the maps are equal
   * @throws EvaluationException
   */
  boolean equals(IMap other, IFunction valQ) throws EvaluationException;

  /**
   * Filter out unwanted elements from a map.
   * 
   * @param filter
   *          a pattern that is satisfied for pairs of key/values that should be deleted
   * @return the new map with the unwanted elements filtered out
   * @throws EvaluationException
   */
  IMap filterOut(IPattern filter) throws EvaluationException;

  /**
   * Update matching elements with new values.
   * 
   * @param filter
   *          a pattern that is satisfied for pairs that should be updated
   * @param transform
   *          a function from a key/value pair to a new key/value pair. Note that both the key and
   *          the value may be adjusted
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
}