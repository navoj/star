package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.List;

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
