package org.star_lang.star.data.indextree;

import java.util.Map.Entry;

import org.star_lang.star.compiler.util.PrettyPrintable;

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

/**
 * Interface for applicative (aka persistent) maps.
 * 
 * It is not permitted for a value to be null.
 * 
 * @param <K>
 *          the type of the key
 * @param <V>
 *          the type of the value
 * 
 * @author fgm
 * 
 */

public interface Mapping<K, V> extends PrettyPrintable, Iterable<Entry<K, V>>
{
  /**
   * Is the map empty?
   * 
   * @return
   */
  boolean isEmpty();

  /**
   * How many elements in the map?
   * 
   * @return the number of kay/value pairs represented
   */
  int size();

  /**
   * Find the value associated with a key.
   * 
   * @param key
   * @return the value, or null if the key is not present.
   */
  V find(K key);

  /**
   * test to see if there is a value associated with the key
   * 
   * @param key
   * @return true if there is
   */
  boolean contains(K key);

  /**
   * Create a new Mapping that results from adding the key/value pair to the map.
   * 
   * @param key
   * @param value
   * @return a new mapping. It is required that this operation does NOT affect the original mapping.
   */
  Mapping<K, V> insrt(K key, V value);

  /**
   * create a new mapping that results from deleting any mapping associated with the key. It is not
   * required that there previously existed an entry for the key.
   * 
   * @param key
   * @return a new mapping with no entry in it for the key. It is required that this operation does
   *         not affect the original mapping.
   */
  Mapping<K, V> delete(K key);

  /**
   * Fold a lambda-like object over the entries in the map
   * 
   * @param folder
   *          the function to apply
   * @param init
   *          value of the state
   * @return the result of applying the function successively to elements of the map
   */
  <S> S fold(Fold<Entry<K, V>, S> folder, S init);
}
