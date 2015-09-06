package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
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
public class FmtApplyApplyPtn implements FmtPtnOp
{
  private final FmtPtnOp opOp;
  private final FmtPtnOp argOp;

  public FmtApplyApplyPtn(FmtPtnOp opOp, FmtPtnOp argOp)
  {
    this.opOp = opOp;
    this.argOp = argOp;
  }

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    if (term instanceof Apply) {
      Apply apply = (Apply) term;

      formatCode mode = opOp.apply(apply.getOperator(), env, loc);

      if (mode == formatCode.applies) {
        IAbstract tpl = Abstract.tupleTerm(loc, apply.getArgs());
        mode = argOp.apply(tpl, env, loc);
      }
      return mode;
    } else
      return formatCode.notApply;
  }

  @Override
  public int getSpecificity()
  {
    return opOp.getSpecificity() + argOp.getSpecificity();
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
}
