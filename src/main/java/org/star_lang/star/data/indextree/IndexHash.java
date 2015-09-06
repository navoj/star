package org.star_lang.star.data.indextree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
