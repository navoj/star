package org.star_lang.star.compiler.cafe.compile;

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
public class HWM implements PrettyPrintable
{
  int depth;
  private final Wrapper<Integer> hwm;

  public HWM()
  {
    this.hwm = new Wrapper<Integer>(0);
  }

  private HWM(Wrapper<Integer> hwm)
  {
    this.hwm = hwm;
  }

  public int bump(int amnt)
  {
    int curr = depth;
    depth += amnt;
    if (depth > hwm.get())
      hwm.set(depth);
    return curr;
  }

  // probe a deeper HWM without actually changing the current depth
  public int probe(int amnt)
  {
    if (depth + amnt > hwm.get())
      hwm.set(depth + amnt);
    return depth;
  }

  public void reset(int mark)
  {
    depth = mark;
  }

  public int getDepth()
  {
    return depth;
  }

  public void setDepth(int depth)
  {
    if (depth > hwm.get())
      hwm.set(depth);
    this.depth = depth;
  }

  public int mark()
  {
    return depth;
  }

  public int getHwm()
  {
    return hwm.get();
  }

  public HWM fork()
  {
    return new HWM(hwm);
  }

  public void merge(HWM with)
  {
    if (with.depth > depth)
      depth = with.depth;
  }

  public void clear()
  {
    hwm.set(0);
    depth = 0;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(depth);
    disp.append("[");
    disp.append(hwm.get());
    disp.append("]");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
