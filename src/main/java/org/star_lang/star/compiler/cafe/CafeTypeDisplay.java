package org.star_lang.star.compiler.cafe;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.Display;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.IList;
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
public class CafeTypeDisplay extends Display
{
  public CafeTypeDisplay(PrettyPrintDisplay disp)
  {
    super(disp);
  }

  @Override
  public void visitApply(Apply trm)
  {
    if (CafeSyntax.isTypeVar(trm)) {
      appendWord(CafeSyntax.typeVarName(trm));
      IList contracts = CafeSyntax.typeVarContracts(trm);
      if (contracts != null) {
        append(Names.REQUIRING);
        int mark = disp.markIndent(2);
        display(contracts, "{", ";\n", "}");
        disp.popIndent(mark);
      }
    } else if (CafeSyntax.isExistentialType(trm)) {
      CafeSyntax.existentialTypeVar(trm).accept(this);
      disp.append(Names.TILDA);
      CafeSyntax.existentialBoundType(trm).accept(this);
    } else if (CafeSyntax.isUniversalType(trm)) {
      CafeSyntax.universalBoundVar(trm).accept(this);
      disp.append(Names.TILDA);
      CafeSyntax.universalBoundType(trm).accept(this);
    } else if (CafeSyntax.isArrowType(trm)) {
      CafeSyntax.arrowTypeArgs(trm).accept(this);
      append(Names.ARROW);
      CafeSyntax.arrowTypeRes(trm).accept(this);
    } else if (CafeSyntax.isPatternType(trm)) {
      display(CafeSyntax.patternTypeArgs(trm), "(", ", ", ")");
      append(Names.LARROW);
      CafeSyntax.patternTypePtn(trm).accept(this);
    } else if (Abstract.arity(trm) == 0)
      appendName(Abstract.getOp(trm));
    else
      super.visitApply(trm);
  }
}
