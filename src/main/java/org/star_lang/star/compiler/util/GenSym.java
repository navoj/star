package org.star_lang.star.compiler.util;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;

/**
 * Implement a gensym function that generates unique identifiers
 */
/*
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
 */
public class GenSym
{
  @SuppressWarnings("unused")
  private static final long varJvmId = Math.abs((long) new UID().hashCode());

  private static Map<String, Long> counters = new HashMap<String, Long>();

  public static String genSym(String prefix)
  {
    Long counter = counters.get(prefix);
    if (counter == null) {
      counter = 0L;
      counters.put(prefix, 1L);
    } else
      counters.put(prefix, counter + 1);
    return prefix + /* "_" + varJvmId + */"_" + counter;
  }

  public static long counter(String prefix)
  {
    Long counter = counters.get(prefix);

    if (counter == null)
      counter = 0l;

    counters.put(prefix, counter + 1);
    return counter;
  }

  public static String genSym()
  {
    return genSym("__");
  }
}
