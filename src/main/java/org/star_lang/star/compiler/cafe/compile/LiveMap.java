package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.Wrapper;

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
public class LiveMap implements PrettyPrintable
{
  private final List<Zone> live = new ArrayList<Zone>();
  private int depth = 0;
  private final Wrapper<Integer> maxDepth;

  public LiveMap()
  {
    this.maxDepth = Wrapper.create(0);
  }

  private LiveMap(Wrapper<Integer> depth)
  {
    this.maxDepth = depth;
  }

  public int reserve(int count)
  {
    for (ListIterator<Zone> it = live.listIterator(); it.hasNext();) {
      Zone zone = it.next();
      if (zone.to - zone.from >= count) {
        int offset = zone.from;
        zone.from += count;
        if (zone.from == zone.to)
          it.remove();
        return offset;
      }
    }
    int offset = depth;
    depth += count;
    if (depth > maxDepth.get())
      maxDepth.set(depth);
    return offset;
  }

  public void allocInt(int offset, int count)
  {
    for (int ix = 0; ix < live.size(); ix++) {
      Zone zone = live.get(ix);
      if (zone.from <= offset && zone.to > offset) {
        if (zone.from == offset) {
          zone.from += count;
          if (zone.from == zone.to)
            live.remove(ix);
          return;
        } else if (zone.to - count == offset) {
          zone.to -= count;
          if (zone.from == zone.to)
            live.remove(ix);
          return;
        } else { // split
          Zone upper = new Zone(offset + count, zone.to);
          zone.to = offset;
          live.add(ix, upper);
          return;
        }
      }
    }
    if (offset == depth) {
      depth += count;
      if (maxDepth.get() < depth)
        maxDepth.set(depth);
    } else if (offset > depth) {
      Zone diff = new Zone(depth, offset);
      live.add(diff);
      depth = offset + count;
      if (maxDepth.get() < depth)
        maxDepth.set(depth);
    } // otherwise already accounted for
  }

  public void release(int offset, int count)
  {
    if (offset + count == depth) {
      depth = offset;
      if (!live.isEmpty()) {
        Zone last = live.get(live.size() - 1);
        if (last.to == depth) {
          depth = last.from;
          live.remove(live.size() - 1);
        }
      }
    } else {
      for (int ix = 0; ix < live.size(); ix++) {
        Zone zone = live.get(ix);
        if (zone.from == offset + count) {
          zone.from = offset;
          mergeLeft(ix);
          return;
        } else if (zone.to == offset) {
          zone.to += count;
          mergeRight(ix);
          return;
        } else if (zone.from > offset + count) {
          assert ix == 0 || live.get(ix - 1).to < offset;
          live.add(ix, new Zone(offset, count));
          return;
        }
      }
      live.add(new Zone(offset, count));
    }
  }

  private void mergeLeft(int ix)
  {
    if (ix > 0) {
      Zone zone = live.get(ix);
      Zone prev = live.get(ix - 1);
      if (prev.to == zone.from) {
        prev.to = zone.to;
        live.remove(ix);
        mergeLeft(ix - 1);
      }
    }
  }

  private void mergeRight(int ix)
  {
    if (ix < live.size() - 1) {
      Zone zone = live.get(ix);
      Zone next = live.get(ix + 1);
      if (zone.to == next.from) {
        zone.to = next.to;
        live.remove(ix + 1);
        mergeRight(ix);
      }
    }
  }

  public int getHwm()
  {
    return maxDepth.get();
  }

  public LiveMap fork()
  {
    return new LiveMap(maxDepth);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.prettyPrint(live, ", ");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  private static class Zone implements PrettyPrintable
  {
    int from;
    int to;

    Zone(int offset, int count)
    {
      this.from = offset;
      this.to = offset + count;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append("[");
      disp.append(from);
      disp.append("-");
      disp.append(to);
      disp.append("]");
    }

    @Override
    public String toString()
    {
      return PrettyPrintDisplay.toString(this);
    }
  }
}
