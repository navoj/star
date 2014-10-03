package org.star_lang.star.compiler.format;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Location.SomeWhere;

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
public class FormatRanges implements PrettyPrintable
{
  // We keep formats sorted by start count and end count.
  // starts are stored in order, ends are in reverse order
  SortedMap<Integer, SortedMap<Integer, Map<String, IAttribute>>> ranges = new TreeMap<>();

  public void recordFormat(int start, int end, String key, IAttribute att)
  {
    SortedMap<Integer, Map<String, IAttribute>> starts = ranges.get(start);

    if (starts == null) {
      starts = new TreeMap<>(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2)
        {
          return o2 - o1;
        }
      });
      ranges.put(start, starts);
    }

    Map<String, IAttribute> formats = starts.get(end);

    if (formats == null) {
      formats = new TreeMap<>();
      starts.put(end, formats);
    }

    formats.put(key, att);
  }

  public void recordFormat(Location loc, String key, IAttribute att)
  {
    if (loc instanceof SomeWhere) {
      int locStart = loc.getCharCnt();
      int locEnd = locStart + loc.getLen();
      if (locEnd > locStart)
        recordFormat(locStart, locEnd, key, att);
    }
  }

  public SortedMap<Integer, Map<String, IAttribute>> getStarts(int ix)
  {
    return ranges.get(ix);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    for (Entry<Integer, SortedMap<Integer, Map<String, IAttribute>>> entry : ranges.entrySet()) {
      for (Entry<Integer, Map<String, IAttribute>> eEntry : entry.getValue().entrySet()) {
        disp.append("[");
        disp.append(entry.getKey());
        disp.append("-");
        disp.append(eEntry.getKey());
        disp.append(":{");
        String sep = "";
        for (Entry<String, IAttribute> aEntry : eEntry.getValue().entrySet()) {
          disp.append(sep);
          sep = ", ";
          disp.append(aEntry.getKey());
          disp.append(":");
          aEntry.getValue().prettyPrint(disp);
        }
        disp.append("}\n");
      }
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
