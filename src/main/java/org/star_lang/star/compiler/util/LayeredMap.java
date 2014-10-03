package org.star_lang.star.compiler.util;

import java.util.Map;


/**
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
