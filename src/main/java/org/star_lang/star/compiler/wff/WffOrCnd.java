package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.wff.WffOp.applyMode;
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
public class WffOrCnd implements WffCond
{
  private final WffCond left, right;

  public WffOrCnd(WffCond left, WffCond right)
  {
    this.left = left;
    this.right = right;
  }

  @Override
  public applyMode satisfied(IAbstract env[], Location loc, WffEngine engine)
  {
    applyMode result = left.satisfied(env, loc, engine);

    if (result != applyMode.validates)
      result = right.satisfied(env, loc, engine);

    return result;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    left.prettyPrint(disp);
    disp.append(" ");
    disp.append(StandardNames.WFF_OR);
    disp.append(" ");
    right.prettyPrint(disp);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
