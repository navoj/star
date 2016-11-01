package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.AApply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.Location;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
    if (term instanceof AApply) {
      AApply apply = (AApply) term;

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
    for (FmtPtnOp argOp : argOps) count += argOp.getSpecificity();
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
