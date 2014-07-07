package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

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
public class WffNumberOp implements WffOp
{

  @Override
  public applyMode apply(IAbstract term, IAbstract env[], Location loc, WffEngine engine)
  {
    if (term instanceof IntegerLiteral || term instanceof FloatLiteral || term instanceof LongLiteral
        || term instanceof BigDecimalLiteral)
      return applyMode.validates;
    else
      return applyMode.notValidates;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.WFF_NUMBER);
  }

  @Override
  public String toString()
  {
    return StandardNames.WFF_NUMBER;
  }

  @Override
  public long specificity()
  {
    return 0;
  }
}
