package org.star_lang.star.compiler.format.rules;

import java.util.ArrayList;
import java.util.List;

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
public class FmtApply implements FmtBuildOp
{
  private final FmtBuildOp op, argOps[];

  public FmtApply(FmtBuildOp op, FmtBuildOp args[])
  {
    this.op = op;
    this.argOps = args;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc)
  {
    List<IAbstract> args = new ArrayList<>();
    for (FmtBuildOp argOp : argOps) args.add(argOp.build(env, loc));
    return new AApply(loc, op.build(env, loc), args);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    op.prettyPrint(disp);
    disp.append("@(");
    disp.prettyPrint(argOps, ", ");
    disp.append(")");
  }
}
