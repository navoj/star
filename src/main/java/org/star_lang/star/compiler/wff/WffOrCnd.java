package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.wff.WffOp.applyMode;

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
public class WffOrCnd implements WffCond
{
  private final WffCond left, right;

  public WffOrCnd(WffCond left, WffCond right)
  {
    this.left = left;
    this.right = right;
  }

  @Override
  public applyMode satisfied(IAbstract env[], Location loc, WffEngine engine)
  {
    applyMode result = left.satisfied(env, loc, engine);

    if (result != applyMode.validates)
      result = right.satisfied(env, loc, engine);

    return result;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    left.prettyPrint(disp);
    disp.append(" ");
    disp.append(StandardNames.WFF_OR);
    disp.append(" ");
    right.prettyPrint(disp);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
