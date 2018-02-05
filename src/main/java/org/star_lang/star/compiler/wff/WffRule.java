package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
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
public class WffRule implements PrettyPrintable
{
  private final int varCount;
  private final WffOp ptn;
  private final long specificity;
  private final WffCond body;
  private final String category;
  private final Location loc;

  public WffRule(Location loc, int varCount, String category, WffOp ptn, WffCond rep)
  {
    this.varCount = varCount;
    this.category = category;
    this.ptn = ptn;
    this.specificity = ptn.specificity();
    this.body = rep;
    this.loc = loc;
  }

  public applyMode validate(IAbstract term, WffEngine engine)
  {
    IAbstract vars[] = new IAbstract[varCount];
    final Location loc = term.getLoc();

    if (ptn.apply(term, vars, loc, engine) == applyMode.validates) {
      if (WffEngine.traceValidation)
        System.out.println("validating " + term + " at " + loc + " as " + category + " using " + this + ", rule at "
            + this.getLoc());

      return body.satisfied(vars, loc, engine);
    } else
      return applyMode.notApply;
  }

  public int getVarCount()
  {
    return varCount;
  }

  public String getCategory()
  {
    return category;
  }

  public Location getLoc()
  {
    return loc;
  }

  public long specificity()
  {
    return specificity;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ptn.prettyPrint(disp);
    disp.append(StandardNames.WFF_DEFINES);
    disp.appendWord(category);
    if (!(body instanceof WffPtnNull)) {
      disp.append(StandardNames.WFF_RULE);
      body.prettyPrint(disp);
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
