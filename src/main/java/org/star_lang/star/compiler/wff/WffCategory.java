package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.operator.StandardNames;
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
public class WffCategory implements WffCond
{
  private final String category;
  private final WffBuildOp tgt;
  private final Location loc;

  public WffCategory(Location loc, WffBuildOp tgt, String category)
  {
    this.tgt = tgt;
    this.category = category;
    this.loc = loc;
  }

  @Override
  public applyMode satisfied(IAbstract env[], Location loc, WffEngine engine)
  {
    IAbstract term = tgt.build(env, loc, engine);
    if (WffEngine.traceValidation)
      System.out.println("validating " + term + " at" + loc + " as " + category + ", rule from " + getLoc());
    return engine.validate(term, category);
  }

  public Location getLoc()
  {
    return loc;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    tgt.prettyPrint(disp);
    disp.append(" ");
    disp.append(StandardNames.WFF_DEFINES);
    disp.append(" ");
    disp.append(category);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
