package org.star_lang.star.compiler.format.rules;

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
public class FmtApplyApplyBuild implements FmtBuildOp
{
  private final FmtBuildOp op, arg;

  public FmtApplyApplyBuild(FmtBuildOp op, FmtBuildOp arg)
  {
    this.op = op;
    this.arg = arg;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc)
  {
    IAbstract op = this.op.build(env, loc);
    Apply args = (Apply) this.arg.build(env, loc);
    return new Apply(loc, op, args.getArgs());
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    op.prettyPrint(disp);
    disp.append("@");
    arg.prettyPrint(disp);
  }
}
