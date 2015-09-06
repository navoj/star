package org.star_lang.star.compiler.util;

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


public interface LayeredMap<K, V> extends Map<K, V>, PrettyPrintable
{
  /**
   * Create a fork of this map. New map contains all existing entries; but
   * entries put into the forked map do not show up in this map
   * 
   * @return
   */
  LayeredMap<K, V> fork();

  /**
   * Get the parent layered map
   * 
   * @return
   */
  LayeredMap<K, V> getParent();

  Map<K, V> getSurfaceMap();

  /**
   * Look for an entry in the surface map only. I.e., will not look in any of
   * the parent maps
   * 
   * @param key
   * @return
   */
  V getLocal(K key);

  /**
   * Visit the entries in the map until the visitor says stop, or run out of
   * entries
   * 
   * @param visitor
   * @return stop iff the visitor has said stop
   */
  ContinueFlag visit(EntryVisitor<K, V> visitor);
}
