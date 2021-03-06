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
public class WfIfCnd implements WffCond
{
  private final WffCond test, thOp, elOp;

  public WfIfCnd(WffCond test, WffCond thOp, WffCond elOp)
  {
    this.test = test;
    this.thOp = thOp;
    this.elOp = elOp;
  }

  @Override
  public applyMode satisfied(IAbstract[] env, Location loc, WffEngine engine)
  {

    if (test.satisfied(env, loc, engine) == applyMode.validates)
      return thOp.satisfied(env, loc, engine);
    else
      return elOp.satisfied(env, loc, engine);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    test.prettyPrint(disp);
    disp.append(":?");
    thOp.prettyPrint(disp);
    disp.append(":|");
    elOp.prettyPrint(disp);
    disp.append(")");
  }

}
