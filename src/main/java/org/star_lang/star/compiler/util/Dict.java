package org.star_lang.star.compiler.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
    this(null, new HashMap<String, E>());
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
