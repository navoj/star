package org.star_lang.star.compiler.format.rules;

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
public class FmtApplyApplyBuild implements FmtBuildOp
{
  private final FmtBuildOp op, arg;

  public FmtApplyApplyBuild(FmtBuildOp op, FmtBuildOp arg)
  {
    this.op = op;
    this.arg = arg;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc)
  {
    IAbstract op = this.op.build(env, loc);
    Apply args = (Apply) this.arg.build(env, loc);
    return new Apply(loc, op, args.getArgs());
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    op.prettyPrint(disp);
    disp.append("@");
    arg.prettyPrint(disp);
  }
}
