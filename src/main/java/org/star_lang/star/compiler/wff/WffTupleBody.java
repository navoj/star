package org.star_lang.star.compiler.wff;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
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
public class WffTupleBody implements WffBuildOp
{
  final WffBuildOp els[];

  public WffTupleBody(WffBuildOp els[])
  {
    this.els = els;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc, WffEngine engine)
  {
    List<IAbstract> oArgs = new ArrayList<IAbstract>();

    for (int ix = 0; ix < els.length; ix++)
      oArgs.add(els[ix].build(env, loc, engine));

    return Abstract.tupleTerm(loc, oArgs);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    String sep = "";

    for (WffBuildOp op : els) {
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

}
