package org.star_lang.star.compiler.cafe;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.Display;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.IList;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
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
