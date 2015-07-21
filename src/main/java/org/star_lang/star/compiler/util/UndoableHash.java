package org.star_lang.star.compiler.util;

import java.util.HashMap;
import java.util.Stack;

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
@SuppressWarnings("serial")
public class UndoableHash<K, V> extends HashMap<K, V> implements UndoableMap<K, V>
{
  private interface Undo
  {
    void undo();
  }

  private final Stack<Undo> state = new Stack<>();

  @Override
  public int undoState()
  {
    return state.size();
  }

  @Override
  public void undo(int point) throws IllegalStateException
  {
    if (point > state.size())
      throw new IllegalStateException("cannot undo");
    while (state.size() > point)
      state.pop().undo();
  }

  @Override
  public V put(K key, V value)
  {
    V old = super.put(key, value);
    state.push(new UndoPut(key, old));
    return old;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V remove(Object key)
  {
    V old = super.remove(key);
    state.push(new UndoRemove((K) key, old));
    return old;
  }

  private class UndoPut implements Undo
  {
    private final K key;
    private final V val;

    private UndoPut(K key, V val)
    {
      this.key = key;
      this.val = val;
    }

    @Override
    public void undo()
    {
      if (val != null)
        UndoableHash.super.put(key, val);
      else
        UndoableHash.super.remove(key);
    }
  }

  private class UndoRemove implements Undo
  {
    private final K key;
    private final V val;

    private UndoRemove(K key, V val)
    {
      this.key = key;
      this.val = val;
    }

    @Override
    public void undo()
    {
      UndoableHash.super.put(key, val);
    }
  }

}
