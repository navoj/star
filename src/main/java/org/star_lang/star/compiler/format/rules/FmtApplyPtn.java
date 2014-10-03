package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.IList;
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
public class FmtApplyPtn implements FmtPtnOp
{
  private final FmtPtnOp opOp;
  private final FmtPtnOp argOps[];

  public FmtApplyPtn(FmtPtnOp opOp, FmtPtnOp argOps[])
  {
    this.opOp = opOp;
    this.argOps = argOps;
  }

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    if (term instanceof Apply) {
      Apply apply = (Apply) term;

      formatCode mode = opOp.apply(apply.getOperator(), env, loc);

      if (mode == formatCode.applies) {
        IList args = apply.getArgs();
        if (args.size() == argOps.length) {
          for (int ix = 0; ix < argOps.length && mode == formatCode.applies; ix++)
            mode = argOps[ix].apply((IAbstract) args.getCell(ix), env, loc);
        } else
          return formatCode.notApply;
      }
      return mode;
    } else
      return formatCode.notApply;
  }

  @Override
  public int getSpecificity()
  {
    int count = opOp.getSpecificity();
    for (int ix = 0; ix < argOps.length; ix++)
      count += argOps[ix].getSpecificity();
    return count;
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
}
