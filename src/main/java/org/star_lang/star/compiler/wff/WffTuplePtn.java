package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Abstract;
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
