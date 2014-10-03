package org.star_lang.star.compiler.util;

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
public class Fixed extends Number implements Comparable<Fixed>
{
  private static final long serialVersionUID = 1L;
  public final long base;
  public final int scale;

  public final static Fixed ZERO = new Fixed(0, 1);
  public final static Fixed ONE = new Fixed(1, 1);

  public Fixed(long base, int scale)
  {
    this.base = base;
    this.scale = scale;
  }

  public Fixed(long base)
  {
    this(base, 1);
  }

  public Fixed(float base, int scale)
  {
    this((long) (base * scale), scale);
  }

  public Fixed(double base, int scale)
  {
    this((long) (base * scale), scale);
  }

  @Override
  public double doubleValue()
  {
    return ((double) base) / scale;
  }

  @Override
  public float floatValue()
  {
    return ((float) base) / scale;
  }

  @Override
  public int intValue()
  {
    if (scale > 1) {
      return (int) base / scale;
    }
    return (int) base;
  }

  @Override
  public long longValue()
  {
    if (scale > 1) {
      return base / scale;
    }
    return base;
  }

  public static Fixed parseFixed(String str)
  {
    StringBuilder buff = new StringBuilder();
    int ix;
    int scale = 1;
    int fscale = 1;
    int length = str.length();
    for (ix = 0; ix < length; ix = str.offsetByCodePoints(ix, 1)) {
      int ch = str.codePointAt(ix);
      if (Character.isDigit(ch))
        buff.appendCodePoint(ch);
      else
        break;
    }
    if (ix < length) {
      int ch = str.codePointAt(ix);
      if (ch == '.') {
        for (ix = ix + 1; ix < length; ix = str.offsetByCodePoints(ix, 1)) {
          ch = str.codePointAt(ix);
          if (Character.isDigit(ch)) {
            scale *= 10;
            fscale = scale;
            buff.appendCodePoint(ch);
          } else
            break;
        }
      }

      if (ix < length) {
        ch = str.codePointAt(ix);
        if (ch == 'f') {
          fscale = 1;
          for (ix = ix + 1; ix < length; ix = str.offsetByCodePoints(ix, 1)) {
            ch = str.codePointAt(ix);
            if (ch == '0')
              fscale *= 10;
            else
              throw new NumberFormatException("invalid character in fixed number");
          }
        }
      }
    }
    long value = Long.parseLong(buff.toString());
    if (fscale > scale)
      value = value * (fscale / scale);
    else if (fscale < scale)
      value = value / (scale / fscale);
    return new Fixed(value, fscale);
  }

  @Override
  public int compareTo(Fixed o)
  {
    if (scale < o.scale) {
      long test = base * (o.scale / scale);
      return (int) (test - o.base);
    } else if (scale > o.scale) {
      long test = o.base * (scale / o.scale);
      return (int) (base - test);
    } else
      return (int) (base - o.base);
  }

  @Override
  public String toString()
  {
    return Long.toString(base / scale) + "." + base % scale + "f";
  }

  public static String toString(Fixed f)
  {
    return f.toString();
  }

  public static Fixed valueOf(String text)
  {
    long base = 0;
    int scale = 1;
    int ix = 0;
    for (; ix < text.length() && Character.isDigit(text.codePointAt(ix)); ix = text
        .offsetByCodePoints(ix, 1)) {
      int ch = text.codePointAt(ix);

      base = base * 10 + Character.digit(ch, 10);
    }
    if (ix < text.length() && text.codePointAt(ix) == '.') {
      for (ix++; ix < text.length() && Character.isDigit(text.codePointAt(ix)); ix = text
          .offsetByCodePoints(ix, 1)) {
        int ch = text.codePointAt(ix);

        base = base * 10 + Character.digit(ch, 10);
        scale = scale * 10;
      }
    }
    return new Fixed(base, scale);
  }

  public Fixed add(Fixed o)
  {
    int nscale = Math.min(scale, o.scale);
    return new Fixed(base * (nscale / scale) + o.base * (nscale / o.scale), nscale);
  }

  public Fixed add(int i)
  {
    return new Fixed(base + (i * scale), scale);
  }

  public Fixed add(float f)
  {
    return add(new Fixed(f, scale));
  }

  public Fixed add(double f)
  {
    return add(new Fixed(f, scale));
  }

  public Fixed sub(Fixed o)
  {
    int nscale = Math.min(scale, o.scale);
    return new Fixed(base * (nscale / scale) - o.base * (nscale / o.scale), nscale);
  }

  public Fixed sub(int i)
  {
    return new Fixed(base - (i * scale), scale);
  }

  public Fixed sub(float f)
  {
    return sub(new Fixed(f, scale));
  }

  public Fixed sub(double f)
  {
    return sub(new Fixed(f, scale));
  }

  public Fixed uminus()
  {
    return new Fixed(-base, scale);
  }

  public Fixed mul(Fixed o)
  {
    int nscale = Math.max(scale, o.scale);
    int oscale = Math.min(scale, o.scale);
    return new Fixed((base * o.base) / oscale, nscale);
  }

  public Fixed mul(int i)
  {
    return new Fixed(base * (i * scale), scale);
  }

  public Fixed mul(float f)
  {
    return mul(new Fixed(f, scale));
  }

  public Fixed mul(double f)
  {
    return mul(new Fixed(f, scale));
  }

  public Fixed div(Fixed o)
  {
    int nscale = Math.max(scale, o.scale);
    int oscale = Math.min(scale, o.scale);
    return new Fixed((base / o.base) * oscale, nscale);
  }

  public Fixed mod(Fixed o)
  {
    int nscale = Math.max(scale, o.scale);
    int oscale = Math.min(scale, o.scale);
    return new Fixed((base % o.base) * oscale, nscale);
  }

  // This is horrific
  public Fixed pow(Fixed o)
  {
    double d1 = ((double) base) / scale;
    double d2 = ((double) o.base) / o.scale;
    double p = Math.pow(d1, d2);
    return new Fixed((long) (p * scale), scale);
  }

  public Fixed min(Fixed o)
  {
    if (compareTo(o) <= 0)
      return this;
    else
      return o;
  }

  public Fixed max(Fixed o)
  {
    if (compareTo(o) >= 0)
      return this;
    else
      return o;
  }

  public Fixed abs()
  {
    if (base >= 0)
      return this;
    else
      return new Fixed(-base, scale);
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof Fixed && ((Fixed) obj).base == base && ((Fixed) obj).scale == scale;
  }

  @Override
  public int hashCode()
  {
    return (int) base * 47 + scale;
  }
}
