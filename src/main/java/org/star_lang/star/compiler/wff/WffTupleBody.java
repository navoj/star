package org.star_lang.star.compiler.wff;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.ast.Abstract;
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
public class WffTupleBody implements WffBuildOp
{
  final WffBuildOp els[];

  public WffTupleBody(WffBuildOp els[])
  {
    this.els = els;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc, WffEngine engine)
  {
    List<IAbstract> oArgs = new ArrayList<>();

    for (int ix = 0; ix < els.length; ix++)
      oArgs.add(els[ix].build(env, loc, engine));

    return Abstract.tupleTerm(loc, oArgs);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    String sep = "";

    for (WffBuildOp op : els) {
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

}
