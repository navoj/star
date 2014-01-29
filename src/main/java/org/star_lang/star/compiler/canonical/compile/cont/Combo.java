/**
 * 
 */
package org.star_lang.star.compiler.canonical.compile.cont;

import org.star_lang.star.compiler.canonical.compile.Continue;
import org.star_lang.star.compiler.canonical.compile.FrameState;

import com.starview.platform.data.type.Location;

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
public class Combo implements Continue
{
  private final Continue cont1, cont2;

  public Combo(Continue cont1, Continue cont2)
  {
    this.cont1 = cont1;
    this.cont2 = cont2;
  }

  @Override
  public FrameState cont(FrameState src, Location loc)
  {
    FrameState left = cont1.cont(src, loc);
    return cont2.cont(left, loc);
  }

  @Override
  public boolean isJump()
  {
    return cont2.isJump();
  }
}