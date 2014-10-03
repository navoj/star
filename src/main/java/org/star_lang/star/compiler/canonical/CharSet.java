package org.star_lang.star.compiler.canonical;

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
public abstract class CharSet implements PrettyPrintable, CharSetVisitable
{

  public static class CharClass extends CharSet
  {
    final int[] chars;

    public CharClass(int chars[])
    {
      this.chars = chars;
    }

    public int[] getChars()
    {
      return chars;
    }

    @Override
    boolean isCharInSet(int ch)
    {
      for (int ix = 0; ix < chars.length; ix++)
        if (chars[ix] == ch)
          return true;
      return false;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      for (int ch : chars)
        disp.appendChar(ch);
    }

    @Override
    public void accept(CharSetVisitor visitor)
    {
      visitor.visitCharClass(this);
    }
  }

  public static class CharUnion extends CharSet
  {
    private final CharSet left;
    private final CharSet right;

    public CharUnion(CharSet left, CharSet right)
    {
      this.left = left;
      this.right = right;
    }

    public CharSet getLeft()
    {
      return left;
    }

    public CharSet getRight()
    {
      return right;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      left.prettyPrint(disp);
      right.prettyPrint(disp);
    }

    @Override
    boolean isCharInSet(int ch)
    {
      return left.isCharInSet(ch) || right.isCharInSet(ch);
    }

    @Override
    public void accept(CharSetVisitor visitor)
    {
      visitor.visitUnion(this);
    }
  }

  public static class AnyChar extends CharSet
  {

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(".");
    }

    @Override
    boolean isCharInSet(int ch)
    {
      return true;
    }

    @Override
    public void accept(CharSetVisitor visitor)
    {
      visitor.visitAnyChar(this);
    }
  }

  abstract boolean isCharInSet(int ch);

}