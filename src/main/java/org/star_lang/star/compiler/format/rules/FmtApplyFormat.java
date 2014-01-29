package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.format.FormatRanges;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
@SuppressWarnings("serial")
public class FmtApplyFormat implements FmtFormatOp
{
  private final FmtBuildOp term;
  private final FmtFormatOp op;

  public FmtApplyFormat(Location loc, FmtBuildOp term, FmtFormatOp op)
  {
    this.term = term;
    this.op = op;
  }

  @Override
  public void format(IAbstract term, Location loc, IAbstract[] env, FormatRanges ranges)
  {
    IAbstract t = this.term.build(env, loc);
    op.format(t, loc, env, ranges);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    this.term.prettyPrint(disp);
    disp.append(StandardNames.WFF_DEFINES);
    op.prettyPrint(disp);
  }
}
