package org.star_lang.star.compiler.operator;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
@SuppressWarnings("serial")
public class RightAssocOperator extends InfixOperator
{
  public RightAssocOperator(String operator, int priority)
  {
    super(operator, priority, OperatorForm.right);
  }

  public RightAssocOperator(String operator, int priority, int minPriority)
  {
    super(operator, minPriority, priority, OperatorForm.right);
  }

  @Override
  public int rightPriority()
  {
    return getPriority();
  }

  @Override
  public String toLatex()
  {
    return "\\tt " + StandardNames.latexString(getOperator()) + "&" + getPriority() + "&right";
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.META_HASH);
    disp.append(StandardNames.RIGHT);
    disp.append("((");
    disp.append(getOperator());
    disp.append("),");
    disp.append(getPriority());
    if (getMinPriority() > 0) {
      disp.append(",");
      disp.append(getMinPriority());
    }
    disp.append(")");
  }
}
