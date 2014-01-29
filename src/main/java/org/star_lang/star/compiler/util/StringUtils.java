package org.star_lang.star.compiler.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.star_lang.star.compiler.type.DisplayType;

import com.starview.platform.data.type.IType;

/**
 * String utility functions
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
public class StringUtils
{
  /**
   * Look for any occurrence of a character in match within text.
   * 
   * @param text
   * @param match
   * @return true if any char in match occurs in text
   */
  public static boolean containsAny(String text, String match)
  {
    for (StringIterator it = new StringIterator(match); it.hasNext();)
      if (text.indexOf(it.next()) >= 0)
        return true;
    return false;
  }

  public static boolean contains(String list[], String tst)
  {
    for (String el : list)
      if (el.equals(tst))
        return true;
    return false;
  }

  public static String spaces(int count)
  {
    StringBuilder blder = new StringBuilder();
    for (int ix = 0; ix < count; ix++)
      blder.append(' ');
    return blder.toString();
  }

  public static boolean lookingAt(String src, int off, String test)
  {
    int sLength = src.length();
    int tLength = test.length();
    if (off + tLength > sLength)
      return false;
    else {
      for (int ix = 0; ix < tLength;)
        if (test.codePointAt(ix) == src.codePointAt(off + ix))
          ix = test.offsetByCodePoints(ix, 1);
        else
          return false;
      return true;
    }
  }

  public static String replaceAfter(String src, String flag, String test, String replace)
  {
    int off = src.indexOf(flag);
    assert off > 0 && lookingAt(src, off + flag.length(), test);

    StringBuilder blder = new StringBuilder();
    blder.append(src, 0, off);
    blder.append(flag);
    blder.append(replace);
    blder.append(src, off + flag.length() + test.length(), src.length());
    return blder.toString();
  }

  /**
   * Load a resource as a reader
   * 
   * @param resource
   * @param root
   * @return a reader, or null if its not there
   */
  public static Reader getResource(String resource, Class<?> root)
  {
    String fullResourceName = resource;
    InputStream istream = root.getResourceAsStream(fullResourceName);

    if (istream == null && !resource.startsWith("/")) {
      fullResourceName = "/" + resource;
      istream = root.getResourceAsStream(fullResourceName);
      if (istream == null && !resource.startsWith("/")) {
        fullResourceName = "/" + resource;
        istream = root.getResourceAsStream(fullResourceName);
      }
    }
    if (istream != null)
      return new InputStreamReader(istream);
    return null;
  }

  /** Load a resource as a stream */
  public static InputStream getResourceStream(String resource, Class<?> root)
  {
    String fullResourceName = resource;
    InputStream istream = root.getResourceAsStream(fullResourceName);

    if (istream == null && !resource.startsWith("/")) {
      fullResourceName = "/" + resource;
      istream = root.getResourceAsStream(fullResourceName);
      if (istream == null && !resource.startsWith("/")) {
        fullResourceName = "/" + resource;
        istream = root.getResourceAsStream(fullResourceName);
      }
    }
    return istream;
  }

  /**
   * Convert a normal Java string into one that obeys StarRules quoting conventions
   * 
   * @param orig
   *          original string
   * @return a string that obeys StarRules quoting conventions
   */
  public static String quoteString(String orig)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    disp.append("\"");
    for (StringIterator it = new StringIterator(orig); it.hasNext();)
      strChr(disp, it.next());
    disp.append("\"");
    return disp.toString();
  }

  /** Display a character properly */
  public static void strChr(PrettyPrintDisplay wtr, int ch)
  {
    switch (ch) {
    case '\b':
      wtr.append("\\b");
      break;
    case '\377':
      wtr.append("\\d");
      break;
    case '\33':
      wtr.append("\\e");
      break;
    case '\f':
      wtr.append("\\f");
      break;
    case '\n':
      wtr.append("\\n");
      break;
    case '\r':
      wtr.append("\\r");
      break;
    case '\t':
      wtr.append("\\t");
      break;
    case '\"':
      wtr.append("\\\"");
      break;
    case '\\':
      wtr.append("\\\\");
      break;
    case '$':
      wtr.append("\\$");
      break;
    case '#':
      wtr.append("\\#");
      break;
    default:
      wtr.appendChar(ch);
      break;
    }
  }

  public static String stringMerge(String... fragments)
  {
    StringBuilder bldr = new StringBuilder();
    for (String frag : fragments)
      bldr.append(frag);
    return bldr.toString();
  }

  public static String[] split(String source, int ch)
  {
    List<String> segments = new ArrayList<String>();

    int mark = 0;
    int next;

    while ((next = source.indexOf(ch, mark)) >= mark) {
      segments.add(source.substring(mark, next));
      mark = next + 1;
    }
    segments.add(source.substring(mark));

    return segments.toArray(new String[segments.size()]);
  }

  public static String removeQuotes(String value)
  {
    final int length = value.length();

    if (value.codePointAt(0) == '"' || value.codePointAt(0) == '\'') {
      // removing quotes at the start and at the end
      StringBuilder sb = new StringBuilder();
      for (int curPos = 1; curPos < length - 1;) {
        int curC = value.codePointAt(curPos);
        if (curC != '\\') {
          sb.appendCodePoint(curC);
          curPos++;
          continue;
        }
        curPos = value.offsetByCodePoints(curPos, 1);
        curC = value.codePointAt(curPos);
        curPos = value.offsetByCodePoints(curPos, 1);

        switch (curC) {
        case 'b':
          sb.append('\b');
          break;
        case 'd':
          sb.append('\377');
          break;
        case 'e':
          sb.append('\33');
          break;
        case 'f':
          sb.append('\f');
          break;
        case 'n':
          sb.append('\n');
          break;
        case 'r':
          sb.append('\r');
          break;
        case 't':
          sb.append('\t');
          break;
        case 'u': {
          int code = Integer.parseInt(value.substring(curPos, curPos + 4), 16);
          sb.appendCodePoint(code);
          curPos += 4;
        }
        case '+': {
          int next = curPos;
          while (next < length && Character.isDigit(value.codePointAt(next)))
            next = value.offsetByCodePoints(next, 1);
          int code = Integer.parseInt(value.substring(curPos, next), 16);
          sb.append(Character.toChars(code));

          if (value.codePointAt(next) == ';')
            next = value.offsetByCodePoints(next, 1);
          curPos = next;
        }
        }
      }
      return sb.toString();
    } else
      return value;
  }

  public static String escapeDotsAndBackslashes(String str)
  {
    str = str.replace("\\", "\\\\");
    str = str.replace(".", "\\.");
    return str;
  }

  private static int deHex(int ch)
  {
    if (ch >= '0' && ch <= '9')
      return ch - '0';
    else if (ch >= 'a' && ch <= 'f')
      return ch - 'a';
    else
      return ch - 'A';
  }

  public static String hexToString(String hex)
  {
    StringBuilder b = new StringBuilder();
    for (StringIterator it = new StringIterator(hex); it.hasNext();) {
      int hi = it.next();
      int lo = it.next();
      b.appendCodePoint((deHex(hi) * 16 + deHex(lo)));
    }

    return b.toString();
  }

  public static String hexString(byte[] bytes)
  {
    StringBuilder str = new StringBuilder();
    for (int ix = 0; ix < bytes.length; ix++)
      hexByte(str, bytes[ix]);
    return str.toString();
  }

  private static void hexByte(StringBuilder str, byte b)
  {
    hexNibble(str, (byte) ((b >> 4) & 0xf));
    hexNibble(str, (byte) (b & 0xf));
  }

  private static void hexNibble(StringBuilder str, byte nib)
  {
    str.appendCodePoint(Character.forDigit(nib, 16));
  }

  public static String firstLines(String source, int i)
  {
    int ix = 0;
    while (i-- > 0 && ix < source.length())
      ix = source.indexOf("\n", ix);
    if (ix < source.length())
      return source.substring(0, ix) + "...";
    else
      return source;
  }

  /**
   * Move down a string until the paren count is balanced.
   * <p/>
   * The initial condition is that the first character is a bracket character
   * 
   * @param str
   *          the string to parse
   * @param from
   *          the initial index
   * @param lparen
   *          the left paren character to count
   * @param rparen
   *          the corresponding right paren character
   * @return the index of the character following the balancing paren character
   */
  public static int countParens(String str, int from, char lparen, char rparen)
  {
    int ix = from;
    int limit = str.length();
    int depth = 0;

    do {
      int ch = str.codePointAt(ix);
      if (ch == lparen)
        depth++;
      else if (ch == rparen)
        depth--;
      else
        switch (ch) {
        case '[':
          ix = countParens(str, ix, '[', ']');
          continue;
        case '{':
          ix = countParens(str, ix, '{', '}');
          continue;
        case '(':
          ix = countParens(str, ix, '(', ')');
          continue;
        case '"': // Regular string, ignore everything up until the close
          // quote
          do {
            ix = str.offsetByCodePoints(ix, 1);
          } while (ix < limit && (ch = str.codePointAt(ix)) != '"');
          break;
        case '`': // Regular string, ignore everything up until the close
          // quote
          do {
            ix = str.offsetByCodePoints(ix, 1);
          } while (ix < limit && (ch = str.codePointAt(ix)) != '`');
          break;
        }
      ix = str.offsetByCodePoints(ix, 1);
    } while (ix < limit && depth > 0);
    return ix;
  }

  public static boolean isTrivial(String s)
  {
    return s == null || s.equals("");
  }

  public static String pluralize(int count, String singular, String plural)
  {
    if (count == 1)
      return singular;
    else
      return plural;
  }

  public static String msg(Object... els)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    DisplayType typeDisp = new DisplayType(disp);

    for (Object el : els) {
      addToMsg(disp, typeDisp, el);
    }
    return disp.toString();
  }

  public static String interleave(String sep, Collection<String> extra)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    String s = "";

    for (String el : extra) {
      disp.append(s);
      s = sep;
      disp.append(el);
    }
    return disp.toString();
  }

  private static void addToMsg(PrettyPrintDisplay disp, DisplayType typeDisp, Object el)
  {
    if (el instanceof IType)
      typeDisp.display((IType) el);
    else if (el instanceof Collection<?>) {
      Collection<?> coll = (Collection<?>) el;
      for (Object e : coll)
        addToMsg(disp, typeDisp, e);
    } else if (el instanceof PrettyPrintable)
      ((PrettyPrintable) el).prettyPrint(disp);
    else
      disp.append(el.toString());
  }
}
