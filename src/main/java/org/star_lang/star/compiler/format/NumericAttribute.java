package org.star_lang.star.compiler.format;

import org.star_lang.star.compiler.ast.BaseAttribute;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
public class NumericAttribute extends BaseAttribute<Integer>
{
  private final int count;
  private final Absolute absolute;

  public NumericAttribute(Absolute absolute, int count, int specificity)
  {
    super(true, specificity);
    this.count = count;
    this.absolute = absolute;
  }

  public Absolute isAbsolute()
  {
    return absolute;
  }

  public int count()
  {
    return count;
  }

  @Override
  public Integer attribute(Integer original)
  {
    switch (absolute) {
    case absolute:
    default:
      return count;
    case increasing:
      return count + original;
    case decreasing:
      return original - count;
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    switch (absolute) {
    case decreasing:
      disp.append("-");
      break;
    case increasing:
      disp.append("+");
      break;
    case absolute:
      break;
    case mark:
      disp.append("=");
      break;
    }
    disp.append(count);
  }
}
