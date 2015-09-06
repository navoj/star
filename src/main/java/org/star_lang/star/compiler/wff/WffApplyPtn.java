package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Apply;
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
