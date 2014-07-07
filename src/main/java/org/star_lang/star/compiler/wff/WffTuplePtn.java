package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.IList;
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
@SuppressWarnings("serial")
public class WffTuplePtn implements WffOp
{
  final WffOp els[];

  public WffTuplePtn(WffOp els[])
  {
    this.els = els;
  }

  @Override
  public applyMode apply(IAbstract term, IAbstract[] env, Location loc, WffEngine engine)
  {
    if (Abstract.isTupleTerm(term)) {
      IList tuple = Abstract.tupleArgs(term);

      if (tuple.size() == els.length) {
        applyMode mode = applyMode.validates;
        for (int ix = 0; mode == applyMode.validates && ix < els.length; ix++) {

          mode = els[ix].apply((IAbstract) tuple.getCell(ix), env, loc, engine);
        }
        return mode;
      }
    }
    return applyMode.notValidates;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    String sep = "";

    for (WffOp op : els) {
      disp.append(sep);
      sep = ", ";
      op.prettyPrint(disp);
    }
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
    long spec = els.length;
    for (int ix = 0; ix < els.length; ix++)
      spec += els[ix].specificity();
    return spec;
  }
}
