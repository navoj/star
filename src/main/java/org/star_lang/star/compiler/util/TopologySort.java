package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/**
 * Used to sort a set into a sequence of subsets, each of which only depends on earlier subsets
 * 
 * @author fgm
 * 
 * @param <T>
 */
public class TopologySort<T>
{
  private final Stack<DefEntry> stack = new Stack<>();
  private final List<DefEntry> definitions;
  private final List<List<IDefinition<T>>> groups = new ArrayList<>();
  private final Map<T, List<Integer>> index;

  private TopologySort(Collection<? extends IDefinition<T>> defs)
  {
    definitions = new ArrayList<>();
    for (IDefinition<T> def : defs)
      definitions.add(new DefEntry(def));
    index = buildIndex(definitions);
  }

  private Map<T, List<Integer>> buildIndex(List<DefEntry> defs)
  {
    Map<T, List<Integer>> index = new HashMap<>();

    for (int ix = 0; ix < defs.size(); ix++) {
      DefEntry def = defs.get(ix);
      for (T d : def.def.definitions()) {
        List<Integer> list = index.get(d);
        if (list == null) {
          list = new ArrayList<>();
          index.put(d, list);
        }
        list.add(ix);
      }
    }
    return index;
  }

  private DefEntry findDefinition(T ref)
  {
    List<Integer> list = index.get(ref);
    if (list == null || list.isEmpty())
      return null;
    else {
      for (Integer ix : list) {
        DefEntry def = definitions.get(ix);
        if (!def.done)
          return def;
      }
      return null;
    }
  }

  private int analyseRefs(IDefinition<T> def, int point)
  {
    int low = point;

    for (T ref : def.references())
      low = minPoint(low, analyse(ref, low));

    return low;
  }

  private int analyse(T ref, int low)
  {
    // Is the reference already being considered?
    for (int ix = stack.size() - 1; ix >= 0; ix--) {
      DefEntry stackEntry = stack.get(ix);
      IDefinition<T> entry = stackEntry.def;

      if (entry.defines(ref))
        return minPoint(low, stackEntry.stackOffset);
    }

    // Look for an implementation
    DefEntry def = findDefinition(ref);
    if (def != null)
      return minPoint(low, analyseDef(def));

    return low;
  }

  private static int minPoint(int X, int Y)
  {
    if (X <= Y)
      return X;
    return Y;
  }

  private int analyseDef(DefEntry defPair)
  {
    int low = defPair.push();

    int point = analyseRefs(defPair.def, low);

    if (point == low) {
      // We have a group
      List<IDefinition<T>> group = new ArrayList<>();
      while (!stack.isEmpty()) {
        DefEntry entry = stack.peek();
        if (entry.stackOffset >= point) {
          group.add(entry.def);
          stack.pop();
        } else
          break;
      }
      if (!group.isEmpty())
        groups.add(group);
    }
    return point;
  }

  public static <T> List<List<IDefinition<T>>> sort(Collection<? extends IDefinition<T>> defs)
  {
    TopologySort<T> sorter = new TopologySort<>(defs);
    return sorter.sort();
  }

  private List<List<IDefinition<T>>> sort()
  {
    for (DefEntry def : definitions) {
      if (!def.done)
        analyseDef(def);
    }

    return groups;
  }

  public interface IDefinition<T>
  {
    boolean defines(T obj);

    Collection<T> definitions();

    Collection<T> references();
  }

  private class DefEntry
  {
    boolean done = false;
    IDefinition<T> def;
    int stackOffset = -1;

    public DefEntry(IDefinition<T> def)
    {
      this.def = def;
    }

    int push()
    {
      stackOffset = stack.size();
      stack.push(this);
      done = true;
      return stackOffset;
    }
  }
}
