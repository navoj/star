package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.Wrapper;

/*
 *
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
public class LiveMap implements PrettyPrintable
{
  private final List<Zone> live = new ArrayList<>();
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
