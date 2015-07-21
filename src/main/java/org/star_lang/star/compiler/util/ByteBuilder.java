package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.List;

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
public class ByteBuilder
{
  private static final int DEFLTBUFFSIZE = 16384;

  List<Segment> segments = new ArrayList<>();
  byte[] buff;
  int pos;

  public void append(byte[] app)
  {
    append(app, 0, app.length);
  }

  public void append(byte[] app, int from, int count)
  {
    if (buff != null && pos + count > buff.length) {
      segments.add(new Segment(buff, pos));
      buff = null;
      pos = 0;
    }

    if (buff == null) {
      buff = new byte[Math.max(count + pos, DEFLTBUFFSIZE)];
      pos = 0;
    }

    System.arraycopy(app, from, buff, pos, count);
    pos += count;
  }

  public void append(byte b)
  {
    if (pos >= buff.length) {
      segments.add(new Segment(buff, pos));
      buff = new byte[DEFLTBUFFSIZE];
      pos = 0;
    }

    buff[pos++] = b;
  }

  private int totalSize()
  {
    int cx = 0;
    for (Segment segment : segments) cx += segment.size();
    return cx + pos;
  }

  public byte[] toBytes()
  {
    if (!segments.isEmpty() || pos != buff.length) {
      byte[] out = new byte[totalSize()];

      int oPx = 0;
      for (Segment segment : segments) {
        int size = segment.size();
        System.arraycopy(segment.data, 0, out, oPx, size);
        oPx += size;
      }
      System.arraycopy(buff, 0, out, oPx, pos);
      oPx += pos;

      assert oPx == out.length;
      return out;
    } else
      return buff;
  }

  private static class Segment
  {
    final byte[] data;
    int pos;

    public Segment(byte[] data, int pos)
    {
      this.data = data;
      this.pos = pos;
    }

    int size()
    {
      return pos;
    }
  }
}
