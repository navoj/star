package org.star_lang.star.data.indextree;

import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.IValue;

import java.util.Iterator;
import java.util.Map;

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
public interface Sets<T> extends Iterable<T>, PrettyPrintable {
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
  boolean contains(T key);

  /**
   * Create a new Mapping that results from adding an element to the set.
   *
   * @param key
   * @return a new set. It is required that this operation does NOT affect the original set.
   */
  Sets<T> insert(T key);

  /**
   * create a new mapping that results from deleting any mapping associated with the key. It is not
   * required that there previously existed an entry for the key.
   *
   * @param key
   * @return a new set with no entry in it for the key. It is required that this operation does
   * not affect the original set.
   */
  Sets<T> delete(T key);


  /**
   * Fold a lambda-like object over the entries in the map
   *
   * @param folder the function to apply
   * @param init   value of the state
   * @return the result of applying the function successively to elements of the map
   */
  <S> S fold(Fold<T, S> folder, S init);

  /**
   * Allow a set to be traversed in the reverse direction as well as the forward direction
   *
   * @return
   */

  Iterator<T> reverseIterator();
}
