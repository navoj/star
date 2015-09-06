package org.star_lang.star.compiler.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
public abstract class Dict<E> implements Iterable<Entry<String, E>>, PrettyPrintable
{
  private final Dict<E> outer;
  private final Map<String, E> entries;

  protected Dict(Dict<E> outer, Map<String, E> entries)
  {
    this.outer = outer;
    this.entries = entries;
  }

  public Dict()
  {
    this(null, new HashMap<>());
  }

  public abstract Dict<E> fork();

  public E get(String name)
  {
    E desc = entries.get(name);

    if (desc == null && outer != null)
      return outer.get(name);
    return desc;
  }

  public E find(String name)
  {
    E desc = entries.get(name);

    if (desc == null && outer != null) {
      desc = outer.find(name);
      if (desc != null)
        entries.put(name, desc);
    }
    return desc;
  }

  public boolean defines(String name)
  {
    if (entries.containsKey(name))
      return true;
    else if (outer != null)
      return outer.defines(name);
    else
      return false;
  }

  public void define(String name, E desc)
  {
    entries.put(name, desc);
  }

  @Override
  public Iterator<Entry<String, E>> iterator()
  {
    return entries.entrySet().iterator();
  }

  public int size()
  {
    return entries.size();
  }

  public Dict<E> getOuter()
  {
    return outer;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    Dict<E> d = this;
    String dictSep = "";
    while (d != null) {
      disp.append(dictSep).append("{");
      dictSep = "\n";
      int mark = disp.markIndent(2);
      for (Entry<String, E> e : d.entries.entrySet()) {
        disp.append("\n");
        disp.append(e.getKey()).append("->");
        E val = e.getValue();
        if (val instanceof PrettyPrintable)
          ((PrettyPrintable) val).prettyPrint(disp);
        else
          disp.append(val.toString());
      }
      disp.popIndent(mark);
      disp.append("\n}");
      d = d.outer;
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
