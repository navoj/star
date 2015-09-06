package org.star_lang.star.compiler.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
