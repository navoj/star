package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.LanguageException;
import org.star_lang.star.compiler.canonical.NFA;
import org.star_lang.star.compiler.grammar.Tokenizer;

import com.starview.platform.data.type.Location;

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
public class RegexpUtils
{

  public static Pair<String, List<String>> analyseRegexp(String regexp, Location loc) throws LanguageException
  {
    List<String> vars = new ArrayList<>();
    StringBuilder blder = new StringBuilder();

    parseGroup(loc, new StringSequence(regexp), vars, blder);
    return Pair.pair(blder.toString(), vars);
  }

  private static void parseGroup(Location loc, Sequencer<Integer> it, List<String> subVars, StringBuilder blder) throws LanguageException
  {
    while (it.hasNext()) {
      int vNo = subVars.size();
      int ch = it.next();
      switch (ch) {
      case '(':
        blder.appendCodePoint(ch);
        parseGroup(loc, it, subVars, blder);
        continue;
      case ':': {
        StringBuilder var = new StringBuilder();
        int pos = it.index();
        while (it.hasNext() && Tokenizer.isIdentifierChar(ch = it.next()))
          var.appendCodePoint(ch);
        Location varLoc = loc.offset(pos + 1, var.length());
        subVars.add(var.toString());
        blder.appendCodePoint(ch);
        if (ch != ')')
          throw new LanguageException(StringUtils.msg("expecting a ')' in regular expression after variable name: ", var), varLoc);
        return;
      }

      case ')': {
        subVars.add("$" + vNo);
        blder.appendCodePoint(ch);
        return;
      }
      case '\\':
        charReference(loc, it, blder);
        continue;
      case Tokenizer.QUOTE:
        blder.appendCodePoint(it.next());
        continue;
      default:
        blder.appendCodePoint(ch);
        continue;
      }
    }
  }

  public static void charReference(Location loc, Sequencer<Integer> it, StringBuilder bldr) throws LanguageException
  {
    int ch = it.next();
    switch (ch) {
    case 'b':
      bldr.append('\b');
      return;
    case 'e': // The escape character
      bldr.append('\33');
      return;
    case 'f': // Form feed
      bldr.append('\f');
      return;
    case 'n': // New line
      bldr.append('\n');
      return;
    case 'r': // Carriage return
      bldr.append('\r');
      return;
    case 't': // Tab
      bldr.append('\t');
      return;
    case '"': // Quote
      bldr.append('\"');
      return;
    case '$':
      bldr.append("\\$");
      return;
    case '\\': // Backslash itself
      bldr.append('\\');
      return;
    case 'd':
      bldr.append("\\d");
      return;
    case 'D':
      bldr.append("\\A");
      return;
    case 'F':
      bldr.append(NFA.floatRegexp);
      return;
    case 's':
      bldr.append("\\s");
      return;
    case 'S':
      bldr.append("\\S");
      return;
    case 'w':
      bldr.append("\\w");
      return;
    case 'W':
      bldr.append("\\W");
      return;
    case 'u': { // Start a hex sequence
      int hex = grabUnicode(loc, it);

      bldr.appendCodePoint(hex);
      return;
    }
    default:
      bldr.append('\\');
      bldr.appendCodePoint(ch);
      return;
    }
  }

  public static int grabUnicode(Location loc, Sequencer<Integer> it) throws LanguageException
  {
    int X = 0;
    int ch = it.next();
    while (Character.getType(ch) == Character.DECIMAL_DIGIT_NUMBER || (ch >= 'a' && ch <= 'f')
        || (ch >= 'A' && ch <= 'F')) {
      X = X * 16 + Character.digit(ch, 16);
      ch = it.next();
    }
    if (ch != ';')
      throw new LanguageException("invalid Unicode sequence", loc);
    return X;
  }

}