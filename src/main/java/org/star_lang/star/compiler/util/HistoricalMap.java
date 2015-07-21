package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
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
 */
/**
 * A HistoricalMap preserves the order of addition to the set.
 * 
 * @param <K>
 * @param <V>
 */

@SuppressWarnings("serial")
public class HistoricalMap<K, V> implements Map<K, V>, PrettyPrintable
{
  private final List<Entry<K, V>> entries;

  public HistoricalMap()
  {
    this(new ArrayList<>());
  }

  public HistoricalMap(List<Entry<K, V>> entries)
  {
    this.entries = entries;
  }

  @Override
  public int size()
  {
    return entries.size();
  }

  @Override
  public boolean isEmpty()
  {
    return entries.isEmpty();
  }

  @Override
  public boolean containsKey(Object key)
  {
    for (Entry<K, V> entry : entries)
      if (entry.getKey().equals(key))
        return true;
    return false;
  }

  @Override
  public boolean containsValue(Object value)
  {
    for (Entry<K, V> entry : entries)
      if (entry.getValue().equals(value))
        return true;
    return false;
  }

  @Override
  public V get(Object key)
  {
    for (Entry<K, V> entry : entries)
      if (entry.getKey().equals(key))
        return entry.getValue();
    return null;
  }

  @Override
  public V put(final K ky, final V val)
  {
    for (Entry<K, V> entry : entries)
      if (entry.getKey().equals(ky)) {
        V old = entry.getValue();
        entry.setValue(val);
        return old;
      }
    entries.add(new HistoricalEntry<>(ky, val));

    return null;
  }

  private static class HistoricalEntry<K, V> implements Entry<K, V>
  {
    K key;
    V value;

    HistoricalEntry(K key, V value)
    {
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey()
    {
      return key;
    }

    @Override
    public V getValue()
    {
      return value;
    }

    @Override
    public V setValue(V value)
    {
      V old = this.value;
      this.value = value;
      return old;
    }

    @Override
    public String toString()
    {
      return PrettyPrintDisplay.msg("<", key, "=", value, ">");
    }
  }

  @Override
  public V remove(Object key)
  {
    for (Iterator<Entry<K, V>> it = entries.iterator(); it.hasNext();) {
      Entry<K, V> entry = it.next();
      if (entry.getKey().equals(key)) {
        it.remove();
        return entry.getValue();
      }
    }
    return null;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m)
  {
    for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear()
  {
    entries.clear();
  }

  @Override
  public Set<K> keySet()
  {
    Set<K> keys = new HashSet<>();

    for (Entry<K, V> entry : entries)
      keys.add(entry.getKey());

    return keys;
  }

  @Override
  public Collection<V> values()
  {
    Set<V> values = new HashSet<>();

    for (Entry<K, V> entry : entries)
      values.add(entry.getValue());

    return values;
  }

  @Override
  public Set<Entry<K, V>> entrySet()
  {
    return new ArraySet<>(entries);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("{ ");
    int mark = disp.markIndent(2);
    String sep = "";
    for (Entry<K, V> entry : entries) {
      disp.append(sep);
      sep = ";\n";
      dispObject(disp, entry.getKey());
      disp.append("=");
      dispObject(disp, entry.getValue());
    }
    disp.popIndent(mark);
    disp.append(" }");
  }

  private void dispObject(PrettyPrintDisplay disp, Object obj)
  {
    if (obj instanceof PrettyPrintable)
      ((PrettyPrintable) obj).prettyPrint(disp);
    else
      disp.append(obj.toString());
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
