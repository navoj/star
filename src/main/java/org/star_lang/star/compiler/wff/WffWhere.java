package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
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
public class WffWhere implements WffCond
{
  private final WffCond tgt;
  private final Location loc;
  private final WffProgram subRules;

  public WffWhere(Location loc, WffCond tgt, WffProgram rules)
  {
    this.loc = loc;
    this.tgt = tgt;
    this.subRules = rules;
  }

  @Override
  public applyMode satisfied(IAbstract[] env, Location loc, WffEngine engine)
  {
    int mark = engine.pushRules(subRules);
    applyMode satisfied = tgt.satisfied(env, loc, engine);
    engine.reset(mark);
    return satisfied;
  }

  public Location getLoc()
  {
    return loc;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    tgt.prettyPrint(disp);
    disp.append("##");
    int mark = disp.markIndent(2);
    subRules.prettyPrint(disp);
    disp.popIndent(mark);
  }
}
