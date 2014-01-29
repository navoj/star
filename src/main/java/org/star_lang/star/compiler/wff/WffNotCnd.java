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
public class WffNotCnd implements WffCond
{
  private final WffCond right;

  public WffNotCnd(WffCond right)
  {
    this.right = right;
  }

  @Override
  public applyMode satisfied(IAbstract env[], Location loc, WffEngine engine)
  {
    applyMode mode = right.satisfied(env, loc, engine);

    switch (mode) {
    case validates:
      return applyMode.notValidates;
    case notValidates:
      return applyMode.validates;
    default:
      return mode;
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.WFF_NOT);
    disp.append(" ");
    right.prettyPrint(disp);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
