package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
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
public class FmtVarPtn implements FmtPtnOp
{
  private final int offset;
  private final FmtPtnOp other;

  FmtVarPtn(int offset)
  {
    this(offset, new FmtPtnNull());
  }

  public FmtVarPtn(int offset, FmtPtnOp other)
  {
    this.offset = offset;
    this.other = other;
  }

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    formatCode mode = other.apply(term, env, loc);
    if (env[offset] == null) {
      env[offset] = term;
      return mode;

    } else if (env[offset].equals(term))
      return mode;
    else
      return formatCode.notApply;
  }

  @Override
  public int getSpecificity()
  {
    return 0;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    other.prettyPrint(disp);
    disp.append(" ");
    disp.append(StandardNames.WFF_VAR);
    disp.append(Long.toString(offset));
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
