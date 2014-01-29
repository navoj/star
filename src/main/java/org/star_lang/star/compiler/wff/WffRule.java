package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
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
public class WffRule implements PrettyPrintable
{
  private final int varCount;
  private final WffOp ptn;
  private final long specificity;
  private final WffCond body;
  private final String category;
  private final Location loc;

  public WffRule(Location loc, int varCount, String category, WffOp ptn, WffCond rep)
  {
    this.varCount = varCount;
    this.category = category;
    this.ptn = ptn;
    this.specificity = ptn.specificity();
    this.body = rep;
    this.loc = loc;
  }

  public applyMode validate(IAbstract term, WffEngine engine)
  {
    IAbstract vars[] = new IAbstract[varCount];
    final Location loc = term.getLoc();

    if (ptn.apply(term, vars, loc, engine) == applyMode.validates) {
      if (WffEngine.traceValidation)
        System.out.println("validating " + term + " at " + loc + " as " + category + " using " + this + ", rule at "
            + this.getLoc());

      return body.satisfied(vars, loc, engine);
    } else
      return applyMode.notApply;
  }

  public int getVarCount()
  {
    return varCount;
  }

  public String getCategory()
  {
    return category;
  }

  public Location getLoc()
  {
    return loc;
  }

  public long specificity()
  {
    return specificity;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ptn.prettyPrint(disp);
    disp.append(StandardNames.WFF_DEFINES);
    disp.appendWord(category);
    if (!(body instanceof WffPtnNull)) {
      disp.append(StandardNames.WFF_RULE);
      body.prettyPrint(disp);
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
