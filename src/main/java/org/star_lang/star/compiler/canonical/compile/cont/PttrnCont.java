package org.star_lang.star.compiler.canonical.compile.cont;

import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.compile.CompileContext;
import org.star_lang.star.compiler.canonical.compile.Continue;
import org.star_lang.star.compiler.canonical.compile.FrameState;
import org.star_lang.star.compiler.canonical.compile.PatternCompile;
import org.star_lang.star.data.type.Location;

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
public class PttrnCont implements Continue
{
  private final IContentPattern ptn;
  private final CompileContext ptnCxt;

  public PttrnCont(IContentPattern ptn, CompileContext ptnCxt)
  {
    this.ptn = ptn;
    this.ptnCxt = ptnCxt;
  }

  @Override
  public FrameState cont(FrameState src, Location loc)
  {
    ptn.transformPattern(new PatternCompile(), ptnCxt);
    return src;
  }

  @Override
  public boolean isJump()
  {
    return false;
  }

}
