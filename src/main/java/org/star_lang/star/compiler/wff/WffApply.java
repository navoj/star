package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
/**
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
public class WffApply implements WffBuildOp
{
  private final WffBuildOp argOps[];
  private final WffBuildOp op;

  public WffApply(WffBuildOp op, WffBuildOp args[])
  {
    this.argOps = args;
    this.op = op;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc, WffEngine engine)
  {
    IAbstract args[] = new IAbstract[argOps.length];
    for (int ix = 0; ix < argOps.length; ix++)
      args[ix] = argOps[ix].build(env, loc, engine);
    return new Apply(loc, op.build(env, loc, engine), args);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    op.prettyPrint(disp);
    disp.append("(");
    disp.prettyPrint(argOps, ", ");
    disp.append(")");
  }
}
