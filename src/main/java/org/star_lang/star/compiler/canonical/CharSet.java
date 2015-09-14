package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

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
      for (int aChar : chars)
        if (aChar == ch)
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