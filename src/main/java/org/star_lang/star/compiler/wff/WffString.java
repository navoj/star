package org.star_lang.star.compiler.wff;

import java.util.List;

import org.star_lang.star.compiler.ast.Display;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.standard.StandardNames;
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
public class WffString implements WffBuildOp
{
  private final List<WffBuildOp> elements;

  public WffString(List<WffBuildOp> elements)
  {
    this.elements = elements;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc, WffEngine engine)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    for (WffBuildOp el : elements) {
      IAbstract trm = el.build(env, loc, engine);
      if (trm instanceof StringLiteral)
        disp.append(((StringLiteral) trm).getLit());
      else if (trm instanceof Name)
        disp.append(((Name) trm).getId());
      else
        Display.display(disp, trm);
    }
    return new StringLiteral(loc, disp.toString());
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    String sep = "";
    for (WffBuildOp el : elements) {
      disp.append(sep);
      sep = StandardNames.STRING_CATENATE;
      el.prettyPrint(disp);
    }
  }

}
