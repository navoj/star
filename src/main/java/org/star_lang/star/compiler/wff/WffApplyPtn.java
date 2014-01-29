package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.IList;
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
public class WffApplyPtn implements WffOp
{
  private final WffOp opOp;
  private final WffOp argOps[];

  public WffApplyPtn(WffOp opOp, WffOp argOps[])
  {
    this.opOp = opOp;
    this.argOps = argOps;
  }

  @Override
  public applyMode apply(IAbstract term, IAbstract env[], Location loc, WffEngine engine)
  {
    if (term instanceof Apply) {
      Apply apply = (Apply) term;

      applyMode mode = opOp.apply(apply.getOperator(), env, loc, engine);

      if (apply.getArgs().size() != argOps.length) {
        mode = applyMode.notValidates;
      }

      if (mode == applyMode.validates) {
        IList args = apply.getArgs();
        for (int ix = 0; ix < argOps.length && mode == applyMode.validates; ix++)
          mode = argOps[ix].apply((IAbstract) args.getCell(ix), env, loc, engine);
      }
      return mode;
    } else
      return applyMode.notValidates;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    opOp.prettyPrint(disp);
    disp.append("(");
    disp.prettyPrint(argOps, ", ");
    disp.append(")");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public long specificity()
  {
    long count = opOp.specificity();
    for (int ix = 0; ix < argOps.length; ix++)
      count += argOps[ix].specificity();
    return count;
  }
}
