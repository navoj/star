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
package org.star_lang.star.compiler.format.rules;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.Location;

@SuppressWarnings("serial")
public class FmtApply implements FmtBuildOp
{
  private final FmtBuildOp op, argOps[];

  public FmtApply(FmtBuildOp op, FmtBuildOp args[])
  {
    this.op = op;
    this.argOps = args;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc)
  {
    List<IAbstract> args = new ArrayList<IAbstract>();
    for (int ix = 0; ix < argOps.length; ix++)
      args.add(argOps[ix].build(env, loc));
    return new Apply(loc, op.build(env, loc), args);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    op.prettyPrint(disp);
    disp.append("@(");
    disp.prettyPrint(argOps, ", ");
    disp.append(")");
  }
}
