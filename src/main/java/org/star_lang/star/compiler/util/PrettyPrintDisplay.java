package org.star_lang.star.compiler.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.BindingLocations;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

/*
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
/**
 * Pretty printer implementation support
 * 
 * @author fgm
 * 
 */
public class PrettyPrintDisplay
{
  private final PrettyPrintFormatProperties properties;

  Stack<State> marks = new Stack<State>();
  int lineOffset = 0;
  List<Integer> newLines = new ArrayList<Integer>();

  private StringBuilder blder = new StringBuilder();

  public PrettyPrintDisplay(PrettyPrintFormatProperties properties)
  {
    this.properties = properties;
  }

  public PrettyPrintDisplay()
  {
    this(new PrettyPrintFormatProperties());
  }

  public PrettyPrintFormatProperties getProperties()
  {
    return properties;
  }

  int pos;
  int lastAppended = ' ';

  public PrettyPrintDisplay append(String frag)
  {
    if (frag == null)
      return this;

    for (int ix = 0; ix < frag.length(); ix = frag.offsetByCodePoints(ix, 1))
      appendChar(frag.codePointAt(ix));
    return this;
  }

  public void appendChar(int ch)
  {
    if (ch == '\n') {
      blder.appendCodePoint(ch);
      lastAppended = ch;
      for (int jx = 0; jx < lineOffset; jx++) {
        blder.appendCodePoint(' ');
        lastAppended = ' ';
      }
      pos = lineOffset;
    } else {
      blder.appendCodePoint(ch);
      lastAppended = ch;
      pos++;
    }
  }

  public void append(int i)
  {
    append(Long.toString(i));
  }

  public void append(long i)
  {
    append(Long.toString(i));
    appendChar('L');
  }

  public void append(double d)
  {
    append(Double.toString(d));
  }

  public void append(BigDecimal a)
  {
    append(a.toString());
  }

  public void appendWord(long i)
  {
    if (!isWordBoundarySafe())
      appendChar(' ');
    append(Long.toString(i));
    appendChar('L');
  }

  public void appendWord(int i)
  {
    if (!isWordBoundarySafe())
      appendChar(' ');
    append(Integer.toString(i));
  }

  public void appendWord(double d)
  {
    appendWord(Double.toString(d));
  }

  public void appendWord(BigDecimal a)
  {
    appendWord(a.toString());
  }

  public void append(String[] words)
  {
    if (words.length == 1)
      append(words[0]);
    else {
      append("(");
      String sep = "";
      for (int ix = 0; ix < words.length; ix++) {
        append(sep);
        sep = ", ";
        append(words[ix]);
      }
      append(")");
    }
  }

  /**
   * Potentially insert an extra space in front of a string that is supposed to be a word
   * 
   * @param str
   */
  public void appendWord(String str)
  {
    if (!str.isEmpty()) {
      if (!isWordBoundarySafe())
        appendChar(' ');

      append(str);
    }
  }

  public void appendWords(Collection<String> words, String sep)
  {
    String s = sep;
    for (String word : words) {
      append(s);
      appendWord(word);
      s = sep;
    }
  }

  public void appendId(String str)
  {
    if (StandardNames.isKeyword(str) || Operators.operatorRoot().isOperator(str, 0)) {
      append("(");
      append(str);
      append(")");
    } else if (!Pattern.matches("[a-zA-Z_][a-zA-Z_0-9]*", str)) {
      appendIden(str);
    } else
      appendWord(str);
  }

  public void appendIden(String str)
  {
    if (!isWordBoundarySafe())
      appendChar(' ');
    for (int ix = 0; ix < str.length(); ix = str.offsetByCodePoints(ix, 1)) {
      int ch = str.codePointAt(ix);

      switch (Character.getType(ch)) {
      case Character.CONNECTOR_PUNCTUATION:
      case Character.LETTER_NUMBER:
      case Character.LOWERCASE_LETTER:
      case Character.TITLECASE_LETTER:
      case Character.UPPERCASE_LETTER:
      case Character.OTHER_LETTER:
      case Character.OTHER_NUMBER:
      case Character.DECIMAL_DIGIT_NUMBER:
        appendChar(ch);
        continue;
      case Character.MODIFIER_SYMBOL:
      case Character.MATH_SYMBOL:
      case Character.OTHER_SYMBOL:
      case Character.OTHER_PUNCTUATION:
      case Character.START_PUNCTUATION:
      case Character.DASH_PUNCTUATION:
      case Character.END_PUNCTUATION:
      case Character.CURRENCY_SYMBOL:
      case Character.COMBINING_SPACING_MARK:
      case Character.MODIFIER_LETTER:
      case Character.NON_SPACING_MARK:
      case Character.PARAGRAPH_SEPARATOR:
      case Character.PRIVATE_USE:
      case Character.SPACE_SEPARATOR:
      case Character.SURROGATE:
        append("\\");
        appendChar(ch);
        continue;
      default:
        StringUtils.strChr(this, ch);
      }
    }
  }

  public void appendQuoted(String str)
  {
    append("\"");
    for (int ix = 0; ix < str.length(); ix = str.offsetByCodePoints(ix, 1))
      StringUtils.strChr(this, str.codePointAt(ix));
    append("\"");
  }

  private boolean isWordBoundarySafe()
  {
    return pos == 0 || " ([{'\"".indexOf(lastAppended) >= 0;
  }

  public void prettyPrint(Iterator<? extends PrettyPrintable> it, String sep)
  {
    while (it.hasNext()) {
      final PrettyPrintable next = it.next();
      if (next != null)
        next.prettyPrint(this);
      else
        append("(null)");
      if (it.hasNext())
        append(sep);
    }
  }

  public void prettyPrint(Iterable<? extends PrettyPrintable> coll, String sep)
  {
    String s = "";
    for (PrettyPrintable el : coll) {
      append(s);
      s = sep;
      if (el != null)
        el.prettyPrint(this);
      else
        append("(null)");
    }
  }

  public void prettyPrint(PrettyPrintable coll[], String sep)
  {
    String s = "";
    for (PrettyPrintable el : coll) {
      append(s);
      s = sep;
      if (el != null)
        el.prettyPrint(this);
      else
        append("(null)");
    }
  }

  @Override
  public String toString()
  {
    return blder.toString();
  }

  public static String toString(PrettyPrintable pp)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();

    pp.prettyPrint(disp);

    return disp.toString();
  }

  public static void write(File out, PrettyPrintable pp) throws IOException
  {
    if (out.exists() && !out.canWrite())
      throw new IOException("cannot write to " + out);
    else {
      try (FileWriter wtr = new FileWriter(out)) {
        PrettyPrintDisplay disp = new PrettyPrintDisplay();
        pp.prettyPrint(disp);
        wtr.append(pp.toString());
      }
    }
  }

  public int markIndent()
  {
    return markIndent(0);
  }

  public int markIndent(int offset)
  {
    marks.push(new State(newLines, lineOffset));
    if (properties.isRelativeTabs())
      lineOffset = pos + offset;
    else
      lineOffset += offset;
    newLines = new ArrayList<Integer>();
    return marks.size() - 1;
  }

  public int markAlignment(int offset)
  {
    marks.push(new State(newLines, lineOffset));

    lineOffset = offset;
    for (int ix = pos; ix < offset; ix++)
      appendChar(' ');
    newLines = new ArrayList<Integer>();
    return marks.size() - 1;
  }

  public void popIndent(int mark)
  {
    while (marks.size() > mark) {
      State state = marks.pop();
      lineOffset = state.lineMark;
      newLines = state.newLines;
    }
    trimToIndent();
  }

  private void trimToIndent()
  {
    int indent = 0;
    for (int ix = blder.length(); ix > 0 && blder.charAt(ix - 1) == ' '; ix--)
      indent++;
    if (indent > lineOffset)
      blder.setLength(blder.length() - indent + lineOffset);
  }

  public static String msg(Object... args)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();

    for (Object o : args) {
      if (o instanceof String)
        disp.append((String) o);
      else if (o instanceof IType) {
        IType type = (IType) o;
        Set<Location> bindingLocations = BindingLocations.bindingLocations(type);
        DisplayType.display(disp, type);
        if (!bindingLocations.isEmpty()) {
          disp.append(" bound at ");
          String sep = "";
          for (Location loc : bindingLocations) {
            disp.append(sep);
            sep = " and ";
            loc.prettyPrint(disp);
          }
        }
      } else if (o instanceof PrettyPrintable)
        ((PrettyPrintable) o).prettyPrint(disp);
      else if (o == null)
        disp.append("(null)");
      else
        disp.append(o.toString());
    }
    return disp.toString();
  }

  private static class State
  {
    List<Integer> newLines;
    int lineMark;

    State(List<Integer> newLines, int lineMark)
    {
      this.newLines = newLines;
      this.lineMark = lineMark;
    }

    @Override
    public String toString()
    {
      return newLines.toString() + "@" + lineMark;
    }
  }

}
