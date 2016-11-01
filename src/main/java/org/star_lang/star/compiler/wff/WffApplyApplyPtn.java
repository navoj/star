package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.AApply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
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
public class WffApplyApplyPtn implements WffOp
{
  private final WffOp opOp;
  private final WffOp argOp;

  public WffApplyApplyPtn(WffOp opOp, WffOp argOp)
  {
    this.opOp = opOp;
    this.argOp = argOp;
  }

  @Override
  public applyMode apply(IAbstract term, IAbstract env[], Location loc, WffEngine engine)
  {
    if (term instanceof AApply) {
      AApply apply = (AApply) term;

      applyMode mode = opOp.apply(apply.getOperator(), env, loc, engine);

      if (mode == applyMode.validates) {
        IAbstract tpl = Abstract.tupleTerm(loc, apply.getArgs());
        mode = argOp.apply(tpl, env, loc, engine);
      }
      return mode;
    } else
      return applyMode.notValidates;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    opOp.prettyPrint(disp);
    disp.append("@");
    argOp.prettyPrint(disp);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public long specificity()
  {
    return opOp.specificity() + argOp.specificity();
  }
}
