package org.star_lang.star.compiler.operator;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

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
public class BracketPair implements PrettyPrintable
{
  public final String leftBracket, rightBracket, operator;
  public final int innerPriority;

  public BracketPair(int innerPriority, String leftBracket, String rightBracket, String operator)
  {
    this.innerPriority = innerPriority;
    this.leftBracket = leftBracket;
    this.rightBracket = rightBracket;
    this.operator = operator;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.META_HASH);
    disp.appendWord(StandardNames.BRACKETS);
    disp.append("((");
    disp.append(leftBracket);
    disp.append("),(");
    disp.append(rightBracket);
    disp.append("),");
    disp.append(innerPriority);
    disp.append(")");
  }

}
