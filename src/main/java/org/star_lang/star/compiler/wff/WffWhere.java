package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
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
public class WffWhere implements WffCond
{
  private final WffCond tgt;
  private final Location loc;
  private final WffProgram subRules;

  public WffWhere(Location loc, WffCond tgt, WffProgram rules)
  {
    this.loc = loc;
    this.tgt = tgt;
    this.subRules = rules;
  }

  @Override
  public applyMode satisfied(IAbstract[] env, Location loc, WffEngine engine)
  {
    int mark = engine.pushRules(subRules);
    applyMode satisfied = tgt.satisfied(env, loc, engine);
    engine.reset(mark);
    return satisfied;
  }

  public Location getLoc()
  {
    return loc;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    tgt.prettyPrint(disp);
    disp.append("##");
    int mark = disp.markIndent(2);
    subRules.prettyPrint(disp);
    disp.popIndent(mark);
  }
}
