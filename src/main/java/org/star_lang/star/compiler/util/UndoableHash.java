package org.star_lang.star.compiler.util;

import java.util.HashMap;
import java.util.Stack;

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
