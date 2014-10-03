package org.star_lang.star.data.indextree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* 
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
 */
/**
 * Implement the std map interface using the IndexTree structure
 * 
 * @param <K>
 *          the type of the key
 * @param <V>
 *          the type of the value
 */

public class IndexHash<K, V> implements Map<K, V>
{
  private IndexTree<K, V> tree;

  public IndexHash(IndexTree<K, V> tree)
  {
    this.tree = tree;
  }

  @Override
  public int size()
  {
    return tree.size();
  }

  @Override
  public boolean isEmpty()
  {
    return tree.isEmpty();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean containsKey(Object key)
  {
    return tree.contains((K) key);
  }

  @Override
  public boolean containsValue(Object value)
  {
    for (Entry<K, V> entry : tree) {
      if (entry.getValue().equals(value))
        return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V get(Object key)
  {
    return tree.find((K) key);
  }

  @Override
  public V put(K key, V value)
  {
    if (tree.contains(key)) {
      V old = tree.find(key);
      tree = tree.insrt(key, value);
      return old;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V remove(Object key)
  {
    if (tree.contains((K) key)) {
      V old = tree.find((K) key);
      tree = tree.delete((K) key);
      return old;
    }
    return null;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m)
  {
    for (Entry<? extends K, ? extends V> e : m.entrySet()) {
      tree = tree.insrt(e.getKey(), e.getValue());
    }
  }

  @Override
  public void clear()
  {
    tree = IndexTree.emptyTree();
  }

  @Override
  public Set<K> keySet()
  {
    Set<K> keys = new HashSet<>();
    for (Entry<K, V> e : tree)
      keys.add(e.getKey());
    return keys;
  }

  @Override
  public Collection<V> values()
  {
    Set<V> values = new HashSet<>();
    for (Entry<K, V> e : tree)
      values.add(e.getValue());
    return values;
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    Set<Entry<K, V>> entries = new HashSet<>();

    for (Entry<K, V> e : tree)
      entries.add(e);
    return entries;
  }
}
