package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.wff.WffOp.applyMode;
import org.star_lang.star.data.type.Location;

/**
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
public class WffTermPtn implements WffCond
{
  private final String category;
  private final int offset;

  public WffTermPtn(int offset, String category)
  {
    this.category = category;
    this.offset = offset;
  }

  @Override
  public applyMode satisfied(IAbstract env[], Location loc, WffEngine engine)
  {
    IAbstract term = env[offset];

    for (IAbstract el : CompilerUtils.unWrap(term)) {
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
