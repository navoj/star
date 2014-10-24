package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.star_lang.star.compiler.grammar.Tokenizer;
import org.star_lang.star.compiler.util.Template.LitFragment;
import org.star_lang.star.compiler.util.Template.TemplateFragment;
import org.star_lang.star.compiler.util.Template.VarFragment;

/*
 * Utility that mimics a simplified velocity-style templating 
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
public class TemplateString
{
  public static final char META_CHAR = '$';

  public static String stringTemplate(String template, Map<String, String> vars)
  {
    final int length = template.length();

    StringBuilder sb = new StringBuilder();
    for (int ix = 0; ix < length;) {
      int ch = template.codePointAt(ix);
      if (ch == META_CHAR && ix < length) {
        final String strVar;
        ix = template.offsetByCodePoints(ix, 1);

        if ((ch = template.codePointAt(ix)) == '(') {
          int nx = StringUtils.countParens(template, ix, '(', ')');
          strVar = template.substring(template.offsetByCodePoints(ix, 1), nx - 1);
          ix = nx;
        } else if (template.startsWith("//", ix)) {
          // If the #// appears on a line of its own, remove the whole line
          int pos = sb.length() - 1;
          while (pos >= 0 && Character.isWhitespace(sb.charAt(pos)) && sb.charAt(pos) != '\n')
            pos--;
          if (pos >= 0 && sb.charAt(pos) == '\n')
            sb.setLength(pos);
          while (ix < length && template.charAt(ix++) != '\n')
            ;
          continue;
        } else {
          int nx = ix;
          while (nx < template.length() && Tokenizer.isIdentifierChar(ch)) {
            nx = template.offsetByCodePoints(nx, 1);

            if (nx < length)
              ch = template.codePointAt(nx);
          }

          strVar = template.substring(ix, nx);
          ix = nx;
        }
        if (vars.containsKey(strVar))
          sb.append(vars.get(strVar));
        else
          throw new IllegalStateException(strVar + " not in variable map");
        continue;
      } else if (ch != '\\') {
        sb.appendCodePoint(ch);
        ix = template.offsetByCodePoints(ix, 1);
        continue;
      } else {
        ix = template.offsetByCodePoints(ix, 1);
        ch = template.codePointAt(ix);
        ix = template.offsetByCodePoints(ix, 1);

        switch (ch) {
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
          int next = ix;

          while (next < length && isHexDigit(template.codePointAt(next)))
            next = template.offsetByCodePoints(next, 1);
          int code = Integer.parseInt(template.substring(ix, next), 16);
          sb.append(Character.toChars(code));

          if (template.codePointAt(next) == ';')
            next = template.offsetByCodePoints(next, 1);
          ix = next;
        }
        }
      }
    }
    return sb.toString();
  }

  private static boolean isHexDigit(int ch)
  {
    return Character.getType(ch) == Character.DECIMAL_DIGIT_NUMBER || (ch >= 'a' && ch <= 'f');
  }

  public static Template parseTemplate(String template)
  {
    Set<String> vars = new TreeSet<>();
    List<TemplateFragment> fragments = new ArrayList<>();

    final int length = template.length();

    StringBuilder sb = new StringBuilder();

    for (int ix = 0; ix < length;) {
      int ch = template.codePointAt(ix);
      if (ch == META_CHAR && ix < length) {

        if (sb.length() > 0) {
          fragments.add(new LitFragment(sb.toString()));
          sb.setLength(0);
        }

        final String strVar;
        ix = template.offsetByCodePoints(ix, 1);

        if ((ch = template.codePointAt(ix)) == '(') {
          int nx = StringUtils.countParens(template, ix, '(', ')');
          strVar = template.substring(template.offsetByCodePoints(ix, 1), nx - 1);
          ix = nx;
        } else {
          int nx = ix;
          while (nx < template.length() && Tokenizer.isIdentifierChar(ch)) {
            nx = template.offsetByCodePoints(nx, 1);

            if (nx < length)
              ch = template.codePointAt(nx);
          }

          strVar = template.substring(ix, nx);
          ix = nx;
        }
        vars.add(strVar);
        fragments.add(new VarFragment(strVar));
        continue;
      } else if (ch != '\\') {
        sb.appendCodePoint(ch);
        ix = template.offsetByCodePoints(ix, 1);
        continue;
      } else {
        ix = template.offsetByCodePoints(ix, 1);
        ch = template.codePointAt(ix);
        ix = template.offsetByCodePoints(ix, 1);

        switch (ch) {
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
          int next = ix;

          while (next < length && isHexDigit(template.codePointAt(next)))
            next = template.offsetByCodePoints(next, 1);
          int code = Integer.parseInt(template.substring(ix, next), 16);
          sb.append(Character.toChars(code));

          if (template.codePointAt(next) == ';')
            next = template.offsetByCodePoints(next, 1);
          ix = next;
        }
        }
      }
    }

    if (sb.length() > 0) {
      fragments.add(new LitFragment(sb.toString()));
      sb.setLength(0);
    }
    return new Template(fragments, vars);
  }

}
