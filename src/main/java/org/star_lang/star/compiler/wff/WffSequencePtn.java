package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.CompilerUtils;
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
public class WffSequencePtn implements WffCond
{
  private final String category;
  private final String operator;
  private final int offset;

  public WffSequencePtn(int offset, String operator, String category)
  {
    this.category = category;
    this.operator = operator;
    this.offset = offset;
  }

  @Override
  public applyMode satisfied(IAbstract env[], Location loc, WffEngine engine)
  {
    IAbstract term = env[offset];

    for (IAbstract el : CompilerUtils.unWrap(term, operator)) {
      applyMode mode = engine.validate(el, category);

      if (mode != applyMode.validates)
        return mode;
    }
    return applyMode.validates;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("?" + offset);
    disp.append(StandardNames.WFF_TERM);
    disp.append(category);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
