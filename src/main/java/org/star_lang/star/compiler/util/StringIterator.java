package org.star_lang.star.compiler.util;

import java.util.Iterator;

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
public class StringIterator implements Iterator<Integer>
{
  private final String content;
  private int cx = 0;
  private final int length;

  public StringIterator(String content)
  {
    this.content = content;
    this.length = content.length();
  }

  @Override
  public boolean hasNext()
  {
    return cx < length;
  }

  @Override
  public Integer next()
  {
    int next = content.codePointAt(cx);
    cx = content.offsetByCodePoints(cx, 1);
    return next;
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException("not permitted");
  }
}
