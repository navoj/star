package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.wff.WffOp.applyMode;
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
public class WffStarPtn implements WffCond
{
  private final String category;
  private final int offset;

  public WffStarPtn(int offset, String category)
  {
    this.category = category;
    this.offset = offset;
  }

  @Override
  public applyMode satisfied(IAbstract env[], Location loc, WffEngine engine)
  {
    IAbstract term = env[offset];

    if (Abstract.isTupleTerm(term)) {
      IList args = Abstract.tupleArgs(term);
      applyMode mode = applyMode.validates;

      for (int ix = 0; mode == applyMode.validates && ix < args.size(); ix++) {
        mode = engine.validate((IAbstract) args.getCell(ix), category);
      }

      return mode;
    } else
      return applyMode.notValidates;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("?" + offset);
    disp.append(StandardNames.WFF_STAR);
    disp.append(category);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
