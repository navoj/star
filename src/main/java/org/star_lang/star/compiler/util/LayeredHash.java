package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * A LayeredHash is a LayeredMap that supports checkpoints -- a check point can be introduced which
 * lays on top of the hash.
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

@SuppressWarnings("serial")
public class LayeredHash<K, V> implements LayeredMap<K, V>
{
  private final Map<K, V> map = new HashMap<>();
  private LayeredHash<K, V> parent;

  public LayeredHash()
  {
    this(null);
  }

  public LayeredHash(LayeredHash<K, V> parent)
  {
    this.parent = parent;
  }

  public LayeredHash(Map<K, V> template)
  {
    this.parent = null;
    for (Entry<K, V> entry : template.entrySet())
      map.put(entry.getKey(), entry.getValue());
  }

  @Override
  public void clear()
  {
    map.clear();
    if (parent != null)
      parent.clear();

  }

  @Override
  public boolean containsKey(Object key)
  {
    if (map.containsKey(key))
      return true;
    else if (parent != null)
      return parent.containsKey(key);
    else
      return false;
  }

  @Override
  public boolean containsValue(Object value)
  {
    if (map.containsValue(value))
      return true;
    else if (parent != null)
      return parent.containsValue(value);
    else
      return false;
  }

  @Override
  public Set<Entry<K, V>> entrySet()
  {
    Map<K, V> entries = new HashMap<>();
    updateEntrySet(entries);
    return entries.entrySet();
  }

  private void updateEntrySet(Map<K, V> entries)
  {
    if (parent != null)
      parent.updateEntrySet(entries);

    entries.putAll(map);
  }

  @Override
  public V getLocal(K key)
  {
    return map.get(key);
  }

  @Override
  public V get(Object key)
  {
    V value = map.get(key);
    if (value == null && parent != null)
      value = parent.get(key);
    return value;
  }

  @Override
  public boolean isEmpty()
  {
    return map.isEmpty() && (parent == null || parent.isEmpty());
  }

  @Override
  public Set<K> keySet()
  {
    if (parent != null) {
      Set<K> entries = new HashSet<>();

      addKeys(entries, this);
      return entries;
    } else
      return map.keySet();
  }

  private static <K, V> void addKeys(Set<K> keys, LayeredHash<K, V> map)
  {
    if (map.parent != null)
      addKeys(keys, map.parent);
    keys.addAll(map.map.keySet());
  }

  @Override
  public V put(K key, V value)
  {
    V old = get(key);
    map.put(key, value);
    return old;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> t)
  {
    map.putAll(t);
  }

  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public V remove(Object key)
  {
    if (map.containsKey(key))
      return map.remove(key);
    else
      throw new UnsupportedOperationException("cannot handle remove");
  }

  @Override
  public int size()
  {
    int size = 0;
    LayeredHash<K, V> par = this;
    while (par != null) {
      size += par.map.size();
      par = par.parent;
    }
    return size;
  }

  @Override
  public Collection<V> values()
  {
    if (parent != null) {
      List<V> values = new ArrayList<>();

      addValues(values, this);
      return values;
    } else
      return map.values();
  }

  private static <K, V> void addValues(List<V> values, LayeredHash<K, V> map)
  {
    if (map.parent != null)
      addValues(values, map.parent);
    values.addAll(map.map.values());
  }

  @Override
  public LayeredHash<K, V> fork()
  {
    return new LayeredHash<>(this);
  }

  @Override
  public LayeredHash<K, V> getParent()
  {
    return parent;
  }

  @Override
  public Map<K, V> getSurfaceMap()
  {
    return map;
  }

  @Override
  public ContinueFlag visit(EntryVisitor<K, V> visitor)
  {
    LayeredHash<K, V> hash = this;
    while (hash != null) {
      for (Entry<K, V> entry : hash.map.entrySet()) {
        ContinueFlag contInue = visitor.visit(entry.getKey(), entry.getValue());
        if (contInue == ContinueFlag.stop)
          return contInue;
      }
      hash = hash.parent;
    }
    return ContinueFlag.cont;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("{");
    String sep = "";
    LayeredHash<K, V> hash = this;
    Set<K> visited = new HashSet<>();

    while (hash != null) {
      for (Entry<K, V> entry : hash.map.entrySet()) {
        disp.append(sep);
        sep = ";\n";
        final K key = entry.getKey();
        if (!visited.contains(key)) {
          visited.add(key);
          if (key instanceof PrettyPrintable)
            ((PrettyPrintable) key).prettyPrint(disp);
          else
            disp.append(key.toString());
          disp.append("=");
          final V value = entry.getValue();
          if (value instanceof PrettyPrintable)
            ((PrettyPrintable) value).prettyPrint(disp);
          else
            disp.append(value.toString());
        }
      }
      hash = hash.parent;
    }
    disp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
