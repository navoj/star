package org.star_lang.star.compiler.util;

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
public class StringSequence implements Sequencer<Integer>, PrettyPrintable
{
  private int ix;
  private final String str;
  private final int limit;

  public StringSequence(String str)
  {
    this.str = str;
    ix = 0;
    limit = str.length();
  }

  public StringSequence(String str, int off)
  {
    this.str = str;
    ix = off;
    limit = str.length();
  }

  @Override
  public int index()
  {
    return ix;
  }

  @Override
  public Integer peek()
  {
    return str.codePointAt(ix);
  }

  @Override
  public Integer next()
  {
    if (ix < limit) {
      Integer next = str.codePointAt(ix);
      ix = str.offsetByCodePoints(ix, 1);
      return next;
    }
    throw new SequenceException("index out of bounds");
  }

  @Override
  public Integer prev()
  {
    if (ix > 0) {
      ix = str.offsetByCodePoints(ix, -1);
      return str.codePointAt(ix);
    }
    throw new SequenceException("index out of bounds");
  }

  @Override
  public boolean hasNext()
  {
    return ix < limit;
  }

  @Override
  public boolean hasPrev()
  {
    return ix > 0;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("\"");
    disp.append(str);
    disp.append("\"\n");
    for (int cx = 0; cx <= ix; cx++)
      disp.appendChar(' ');
    disp.append("^");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
