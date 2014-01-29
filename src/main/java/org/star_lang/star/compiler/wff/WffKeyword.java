package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Abstract;
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
public class WffKeyword implements WffCond
{
  private final WffBuildOp trm;

  public WffKeyword(WffBuildOp trm)
  {
    this.trm = trm;
  }

  @Override
  public applyMode satisfied(IAbstract env[], Location loc, WffEngine engine)
  {
    IAbstract term = trm.build(env, loc, engine);

    if (Abstract.isIdentifier(term) && StandardNames.isKeyword(Abstract.getId(term)))
      return applyMode.validates;
    else
      return applyMode.notValidates;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    trm.prettyPrint(disp);
    disp.append(StandardNames.WFF_DEFINES);
    disp.append(StandardNames.WFF_KEYWORD);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
