package org.star_lang.star.compiler.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
public class TokenChar implements PrettyPrintable
{
  private final int ch;
  private final Map<Integer, TokenChar> follows = new HashMap<>();
  private boolean isTerm;

  public TokenChar(int ch, boolean isTerm)
  {
    this.ch = ch;
    this.isTerm = isTerm;
  }

  public final void addTokChar(TokenChar tokChar)
  {
    follows.put(tokChar.tokChar(), tokChar);
  }

  public int tokChar()
  {
    return ch;
  }

  public TokenChar follows(int ch)
  {
    return follows.get(ch);
  }

  public boolean isTerm()
  {
    return isTerm;
  }

  private void markFinal()
  {
    isTerm = true;
  }

  public static void recordToken(TokenChar root, String token)
  {
    assert root.tokChar() == -1;
    for (int ix = 0; ix < token.length();) {
      int ch = token.codePointAt(ix);
      ix = token.offsetByCodePoints(ix, 1);

      TokenChar follows = root.follows(ch);
      if (follows == null) {
        follows = new TokenChar(ch, ix >= token.length());
        root.addTokChar(follows);
      }
      root = follows;

    }
    root.markFinal();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    StringBuilder blder = new StringBuilder();

    print(disp, this, blder);
  }

  private static void print(PrettyPrintDisplay disp, TokenChar toks, StringBuilder blder)
  {
    if (toks.ch >= 0)
      blder.appendCodePoint(toks.ch);
    if (toks.isTerm) {
      disp.append(blder.toString());
      disp.append("\n");
    }
    int mark = blder.length();

    for (Entry<Integer, TokenChar> entry : toks.follows.entrySet()) {
      print(disp, entry.getValue(), blder);
      blder.setLength(mark);
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
