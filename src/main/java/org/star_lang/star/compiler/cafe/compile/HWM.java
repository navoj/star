package org.star_lang.star.compiler.cafe.compile;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.Wrapper;

/*  * Copyright (c) 2015. Francis G. McCabe  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file  * except in compliance with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software distributed under the  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language governing  * permissions and limitations under the License.  */
@SuppressWarnings("serial")
public class HWM implements PrettyPrintable
{
  int depth;
  private final Wrapper<Integer> hwm;

  public HWM()
  {
    this.hwm = new Wrapper<>(0);
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
