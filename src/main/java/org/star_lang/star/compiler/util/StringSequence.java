package org.star_lang.star.compiler.util;

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
