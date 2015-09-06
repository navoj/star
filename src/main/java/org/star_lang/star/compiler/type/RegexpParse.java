package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.NFA;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.grammar.Tokenizer;
import org.star_lang.star.compiler.util.Sequencer;
import org.star_lang.star.compiler.util.StringSequence;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeConstraintException;

/**
 * Convert a regexp-bound case into an NFA machine.
 *
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
public class RegexpParse
{
  public static NFA regexpNFA(Location loc, String regex, ErrorReport errors, Set<Variable> vars, Dictionary cxt)
  {
    return generate(loc, new StringSequence(regex), errors, vars, cxt);
  }

  private static NFA generate(Location loc, Sequencer<Integer> regexp, ErrorReport errors, Set<Variable> vars,
      Dictionary cxt)
  {
    NFA first = generateNFA(loc, regexp, errors, vars, cxt);

    if (regexp.hasNext() && conjNext(regexp.peek())) {
      List<NFA> nfas = new ArrayList<>();
      nfas.add(first);
      while (regexp.hasNext() && conjNext(regexp.peek())) {
        NFA el = generateNFA(loc, regexp, errors, vars, cxt);
        nfas.add(el);
      }
      if (nfas.size() > 1)
        return NFA.sequence(loc, -1, nfas.toArray(new NFA[nfas.size()]));
      else
        return first;
    } else
      return first;

  }

  private static NFA generateNFA(Location loc, Sequencer<Integer> regexp, ErrorReport errors, Set<Variable> vars,
      Dictionary cxt)
  {
    if (!regexp.hasNext())
      return NFA.emptyNFA(loc);
    else {
      int lead = regexp.next();
      NFA left = null;
      switch (lead) {
      case '.':
        left = NFA.anyNFA(loc, -1);
        break;
      case '(':
        left = disjunct(loc, regexp, errors, vars, cxt);
        break;
      case '[':
        left = charClass(loc, regexp, errors);
        break;
      case '\\':
        left = charReference(loc, regexp, vars, cxt, errors);
        break;
      case '$':
        left = varReference(loc, regexp, errors, vars, cxt);
        break;
      case ')':
      case ']':
      case '|':
      case '?':
      case '*':
      case '+':
        errors.reportError("unexpected meta-character: `" + lead + "' in regular expression", loc);
      default:
        left = NFA.charNFA(loc, -1, lead);
        break;
      }

      return nfaCardinality(loc, regexp, left);
    }
  }

  private static boolean conjNext(int ch)
  {
    switch (ch) {
    case ':':
    case '|':
    case ')':
      return false;
    default:
      return true;
    }
  }

  private static NFA disjunct(Location loc, Sequencer<Integer> regexp, ErrorReport errors, Set<Variable> vars,
      Dictionary cxt)
  {
    NFA arm = generate(loc, regexp, errors, vars, cxt);
    if (regexp.hasNext()) {
      int lead = regexp.next();
      switch (lead) {
      case ':': {
        arm = grabName(loc, regexp, arm, vars);
        lead = regexp.next();
        break;
      }
      case '|':
        while (lead == '|') {
          NFA other = generate(loc, regexp, errors, vars, cxt);
          arm = NFA.disjunct(loc, -1, arm, other);
          lead = regexp.next();
        }
        break;
      case ')':
        return arm;
      default:
      }
      if (lead == ')')
        return arm;
      else {
        errors.reportError("expecting a ')'", loc);
        regexp.prev();
        return arm;
      }
    } else {
      errors.reportError("premature end of regexp", loc);
      return arm;
    }
  }

  private static NFA varReference(Location loc, Sequencer<Integer> regexp, ErrorReport errors, Set<Variable> vars,
      Dictionary cxt)
  {
    int pos = regexp.index();
    String var = grabIdentifier(regexp);
    loc = loc.offset(pos, var.length());
    IType varType = cxt.getVarType(var);
    try {
      Subsume.same(varType, StandardTypes.stringType, loc, cxt);
    } catch (TypeConstraintException e) {
      errors.reportError("cannot reference variable " + var + " here\nbecause " + e.getMessage(), Location.merge(loc, e
          .getLocs()));
    }
    return NFA.variable(loc, new Variable(loc, varType, var));
  }

  public static String grabIdentifier(Sequencer<Integer> regexp)
  {
    StringBuilder vStr = new StringBuilder();

    while (regexp.hasNext() && Tokenizer.isIdentifierChar(regexp.peek()))
      vStr.appendCodePoint(regexp.next());

    return vStr.toString();
  }

  private static NFA grabName(Location loc, Sequencer<Integer> regexp, NFA left, Set<Variable> vars)
  {
    int pos = regexp.index();
    String vName = grabIdentifier(regexp);

    Variable var = new Variable(loc.offset(pos, vName.length()), StandardTypes.stringType, vName);
    vars.add(var);

    return NFA.bound(loc, var, left);
  }

  private static NFA charClass(Location loc, Sequencer<Integer> reg, ErrorReport errors)
  {
    Set<Integer> set = new TreeSet<>();
    boolean negated = false;
    if (reg.hasNext() && reg.peek() == '^') {
      negated = true;
      reg.next();
    }

    while (reg.hasNext() && reg.peek() != ']') {
      int first = charRef(loc, reg, errors);

      if (reg.hasNext() && reg.peek() == '-') {
        reg.next();
        if (reg.hasNext()) {
          int second = charRef(loc, reg, errors);
          for (int ix = first; ix <= second; ix++)
            set.add(ix);
        } else {
          set.add(first);
          set.add((int) '-');
        }
      } else
        set.add(first);
    }

    if (!(reg.hasNext() && reg.next() == ']'))
      errors.reportError("expecting a ']'", loc);

    int chars[] = new int[set.size()];
    int ix = 0;
    for (Integer ch : set)
      chars[ix++] = ch;

    return NFA.charClass(loc, -1, negated, chars);
  }

  private static NFA nfaCardinality(Location loc, Sequencer<Integer> regexp, NFA left)
  {
    if (regexp.hasNext()) {
      int c = regexp.next();

      switch (c) {
      case '?':
        return NFA.optional(loc, -1, left);
      case '*':
        return NFA.star(loc, -1, left);
      case '+':
        return NFA.plus(loc, -1, left);
      default:
        regexp.prev();
        return left;
      }
    } else
      return left;
  }

  private static NFA charReference(Location loc, Sequencer<Integer> it, Set<Variable> vars, Dictionary cxt,
      ErrorReport errors)
  {
    int ch = it.next();
    switch (ch) {
    case 'b':
      return NFA.charNFA(loc, -1, '\b');
    case 'e': // The escape character
      return NFA.charNFA(loc, -1, '\33');
    case 'f': // Form feed
      return NFA.charNFA(loc, -1, '\f');
    case 'n': // New line
      return NFA.charNFA(loc, -1, '\n');
    case 'r': // Carriage return
      return NFA.charNFA(loc, -1, '\r');
    case 't': // Tab
      return NFA.charNFA(loc, -1, '\t');
    case '"': // Quote
      return NFA.charNFA(loc, -1, '\"');
    case '$':
      return NFA.charNFA(loc, -1, '$');
    case '\\': // Backslash itself
      return NFA.charNFA(loc, -1, '\\');
    case 'd':
      return NFA.digit(loc, -1, false);
    case 'D':
      return NFA.digit(loc, -1, true);
    case 'F':
      return generate(loc, new StringSequence(NFA.floatRegexp), errors, vars, cxt);
    case 's':
      return NFA.white(loc, -1, false);
    case 'S':
      return NFA.white(loc, -1, true);
    case 'w':
      return NFA.word(loc, -1, false);
    case 'W':
      return NFA.word(loc, -1, true);
    case 'u': { // Start a unicode hex sequence
      int hex = grabUnicode(loc, it, errors);
      return NFA.charNFA(loc, -1, hex);
    }
    default:
      return NFA.charNFA(loc, -1, ch);
    }
  }

  private static int charRef(Location loc, Sequencer<Integer> it, ErrorReport errors)
  {
    int ch = it.next();
    if (ch == '\\' && it.hasNext()) {
      switch (ch = it.next()) {
      case 'b':
        return '\b';
      case 'e': // The escape character
        return '\33';
      case 'f': // Form feed
        return '\f';
      case 'n': // New line
        return '\n';
      case 'r': // Carriage return
        return '\r';
      case 't': // Tab
        return '\t';
      case '"': // Quote
        return '\"';
      case '$':
        return '$';
      case '\\': // Backslash itself
        return '\\';
      case '+': // Start a hex sequence
        return grabUnicode(loc, it, errors);
      default:
        return ch;
      }
    } else
      return ch;
  }

  public static int grabUnicode(Location loc, Sequencer<Integer> it, ErrorReport errors)
  {
    int X = 0;
    int ch = it.next();
    while (Character.getType(ch) == Character.DECIMAL_DIGIT_NUMBER || (ch >= 'a' && ch <= 'f')
        || (ch >= 'A' && ch <= 'F')) {
      X = X * 16 + Character.digit(ch, 16);
      ch = it.next();
    }
    if (ch != ';')
      errors.reportError("invalid Unicode sequence", loc);
    return X;
  }
}
