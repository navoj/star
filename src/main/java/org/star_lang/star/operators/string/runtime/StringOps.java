package org.star_lang.star.operators.string.runtime;

import java.util.Map.Entry;
import java.util.Stack;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.*;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Array;
import org.star_lang.star.data.value.ArrayBase;
import org.star_lang.star.data.value.Cons;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.StringWrap;
import org.star_lang.star.data.value.StringWrap.StringWrapper;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.arrays.runtime.ArrayOps;

/**
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author fgm
 */

public class StringOps {
  /**
   * Return the number of unicode characters in the string.
   */

  private static final IType rawIntType = StandardTypes.rawIntegerType;
  private static final IType rawStringType = StandardTypes.rawStringType;

  public static class StrLength implements IFunction {
    public static final String name = "__str_length";

    @CafeEnter
    public static int enter(String txt) {
      return txt.codePointCount(0, txt.length());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newInt(enter(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, rawIntType);
    }
  }

  public static class StringQuote implements IFunction {
    public static final String name = "__string_quote";

    @CafeEnter
    public static String enter(String txt) {
      return StringUtils.quoteString(txt);
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, rawStringType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType() {
      return type();
    }
  }

  /**
   * Implement string concatenation
   *
   * @author fgm
   */
  public static class StringConcat implements IFunction {
    public static final String name = "__string_concat";

    @CafeEnter
    public static IValue enter(IValue s1, IValue s2) throws EvaluationException {
      return Factory.newString(Factory.stringValue(s1) + Factory.stringValue(s2));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(args[0], args[1]);
    }

    @Override
    public IType getType() {
      return funType();
    }

    public static IType funType() {
      return TypeUtils.functionType(StandardTypes.stringType, StandardTypes.stringType, StandardTypes.stringType);
    }
  }

  /**
   * Implement string concatenation (raw mode)
   *
   * @author fgm
   */
  public static class StringConcatenate implements IFunction {
    public static final String name = "__string_concatenate";

    @CafeEnter
    public static String enter(String s1, String s2) {
      return s1 + s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0]), Factory.stringValue(args[1])));
    }

    @Override
    public IType getType() {
      return funType();
    }

    public static IType funType() {
      return TypeUtils.functionType(rawStringType, rawStringType, rawStringType);
    }
  }

  /**
   * Split a string into a list of substrings.
   *
   * @author fgm
   */
  public static class SplitString implements IFunction {
    public static final String name = "splitString";

    @CafeEnter
    public static IArray enter(String lft, String rgt) throws EvaluationException {
      String[] splits = lft.split(rgt);
      IValue els[] = new IValue[splits.length];
      for (int ix = 0; ix < splits.length; ix++)
        els[ix] = Factory.newString(splits[ix]);
      return Factory.newArray(StandardTypes.stringType, els);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(Factory.stringValue(args[0]), Factory.stringValue(args[1]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.stringType, StandardTypes.stringType, TypeUtils
              .arrayType(StandardTypes.stringType));
    }
  }

  public static class StringExplode implements IFunction {
    public static final String name = "__string_explode";

    @CafeEnter
    public static IValue enter(String data) throws EvaluationException {
      Stack<IValue> stack = new Stack<>();

      int ix = 0;
      while (ix < data.length()) {
        stack.push(Factory.newChar(data.codePointAt(ix)));
        ix = data.offsetByCodePoints(ix, 1);
      }

      IValue reslt = Cons.nilEnum;
      while (!stack.isEmpty())
        reslt = Cons.cons(stack.pop(), reslt);

      return reslt;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, TypeUtils.consType(StandardTypes.charType));
    }
  }

  public static class String2Array implements IFunction {
    public static final String name = "__string_array";

    @CafeEnter
    public static IValue enter(String data) throws EvaluationException {
      ArrayBase base = new ArrayBase(data.length());
      for (int ix = 0; ix < data.length(); ) {
        base.append(Factory.newChar(data.codePointAt(ix)));
        ix = data.offsetByCodePoints(ix, 1);
      }

      return new Array(base, base.firstUsed(), base.lastUsed());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, TypeUtils.arrayType(StandardTypes.charType));
    }
  }

  /**
   * Find occurence of a substring in a string *
   *
   * @author fgm
   */
  public static class StringFind implements IFunction {
    public static final String name = "__string_find";

    @CafeEnter
    public static int enter(String s1, String s2, int from) {
      return s1.indexOf(s2, from);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory
              .newInt(enter(Factory.stringValue(args[0]), Factory.stringValue(args[1]), Factory.intValue(args[2])));
    }

    @Override
    public IType getType() {
      return funType();
    }

    public static IType funType() {
      return TypeUtils.functionType(rawStringType, rawStringType, rawIntType, rawIntType);
    }
  }

  public static class StringPair implements IPattern {
    public static final String name = "__string_pair";

    @CafeEnter
    public static IValue match(StringWrap s) throws EvaluationException {
      String str = ((StringWrapper) s).get___0();
      if (str.isEmpty())
        return null;
      else
        return NTuple.tuple(Factory.newChar(str.codePointAt(0)), Factory.newString(str.substring(str
                .offsetByCodePoints(0, 1))));
    }

    @Override
    public IValue match(IValue arg) throws EvaluationException {
      return match((StringWrap) arg);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.patternType(TypeUtils.tupleType(StandardTypes.charType, StandardTypes.stringType),
              StandardTypes.stringType);
    }
  }

  public static class StringBack implements IPattern {
    public static final String name = "__string_back";

    @CafeEnter
    public static IValue match(StringWrap s) throws EvaluationException {
      String str = ((StringWrapper) s).get___0();
      if (str.isEmpty())
        return null;
      else {
        int lastPoint = str.codePointBefore(str.length());
        return NTuple
                .tuple(Factory.newString(str.substring(0, lastPoint)), Factory.newChar(str.codePointAt(lastPoint)));
      }
    }

    @Override
    public IValue match(IValue arg) throws EvaluationException {
      return match((StringWrap) arg);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType strType = StandardTypes.stringType;
      IType charType = StandardTypes.charType;
      return TypeUtils.patternType(TypeUtils.tupleType(strType, charType), strType);
    }
  }

  public static class StringCons implements IFunction {
    public static final String name = "__string_cons";

    public static String enter(int ch, String tail) {
      StringBuilder b = new StringBuilder();
      b.appendCodePoint(ch);
      b.append(tail);
      return b.toString();
    }

    @CafeEnter
    public static IValue enter(IValue ch, IValue tail) throws EvaluationException {
      return Factory.newString(enter(Factory.charValue(ch), Factory.stringValue(tail)));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(args[0], args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.charType, StandardTypes.stringType, StandardTypes.stringType);
    }
  }

  public static class StringAppend implements IFunction {
    public static final String name = "__string_apnd";

    public static String enter(String str, int ch) {
      StringBuilder b = new StringBuilder();
      b.append(str);
      b.appendCodePoint(ch);
      return b.toString();
    }

    @CafeEnter
    public static IValue enter(IValue tail, IValue ch) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(tail), Factory.charValue(ch)));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(args[0], args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.stringType, StandardTypes.charType, StandardTypes.stringType);
    }
  }

  public static class StringIterate implements IFunction {
    public static final String name = "__string_iter";

    @CafeEnter
    public static IValue enter(IValue data, IFunction iter, IValue state) throws EvaluationException {
      String str = Factory.stringValue(data);

      int ix = 0;
      while (ix < str.length() && ArrayOps.moreToDo(state)) {
        state = iter.enter(Factory.newChar(str.codePointAt(ix)), state);
        ix = str.offsetByCodePoints(ix, 1);
      }

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType e = StandardTypes.charType;
      IType st = StandardTypes.stringType;
      TypeVar tv = new TypeVar();

      IType funType = TypeUtils.functionType(st, TypeUtils.functionType(e, tv, tv), tv, tv);
      return new UniversalType(tv, funType);
    }
  }

  public static class StringFilter implements IFunction {
    public static final String name = "__string_filter";

    @CafeEnter
    public static String enter(String str, IFunction filter) throws EvaluationException {
      StringBuilder out = new StringBuilder();

      int ix = 0;
      while (ix < str.length()) {
        int cp = str.codePointAt(ix);

        if (Factory.boolValue(filter.enter(Factory.newChar(cp))))
          out.appendCodePoint(cp);
        ix = str.offsetByCodePoints(ix, 1);
      }

      return out.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0]), (IFunction) args[1]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType e = StandardTypes.charType;
      IType st = StandardTypes.rawStringType;

      return TypeUtils.functionType(st, TypeUtils.functionType(e, StandardTypes.booleanType), st);
    }
  }

  public static class StringIxIterate implements IFunction {
    public static final String name = "__string_ix_iterate";

    @CafeEnter
    public static IValue enter(String str, IFunction iter, IValue state) throws EvaluationException {
      int ix = 0;
      int cx = 0;
      while (ix < str.length() && ArrayOps.moreToDo(state)) {
        state = iter.enter(Factory.newInt(cx++), Factory.newChar(str.codePointAt(ix)), state);
        ix = str.offsetByCodePoints(ix, 1);
      }

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(Factory.stringValue(args[0]), (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType e = StandardTypes.charType;
      IType st = rawStringType;
      IType i = StandardTypes.integerType;
      TypeVar tv = new TypeVar();

      IType funType = TypeUtils.functionType(st, TypeUtils.functionType(i, e, tv, tv), tv, tv);
      return new UniversalType(tv, funType);
    }
  }

  public static class StringImplode implements IFunction {
    public static final String name = "__string_implode";

    @CafeEnter
    public static String enter(IValue data) throws EvaluationException {
      PrettyPrintDisplay blder = new PrettyPrintDisplay();

      StringPicker picker = new StringPicker(blder);
      data.accept(picker);
      return blder.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(tv, rawStringType));
    }

    private static class StringPicker implements IValueVisitor {
      private final PrettyPrintDisplay disp;

      StringPicker(PrettyPrintDisplay disp) {
        this.disp = disp;
      }

      @Override
      public void visitScalar(IScalar<?> scalar) {
        Object value = scalar.getValue();
        if (value instanceof Integer)
          disp.appendChar((int) value);
      }

      @Override
      public void visitRecord(IRecord con) {
        int arity = con.size();
        for (int ix = 0; ix < arity; ix++)
          con.getCell(ix).accept(this);
      }

      @Override
      public void visitList(IList list) {
        for (IValue el : list)
          el.accept(this);
      }

      @Override
      public void visitFunction(IFunction fn) {
      }

      @Override
      public void visitPattern(IPattern ptn) {
      }

      @Override
      public void visitConstructor(IConstructor con) {
        int arity = con.size();
        for (int ix = 0; ix < arity; ix++)
          con.getCell(ix).accept(this);
      }

      @Override
      public void visitMap(IMap map) {
        for (Entry<IValue, IValue> entry : map) {
          entry.getKey().accept(this);
          entry.getValue().accept(this);
        }
      }

      @Override
      public void visitSet(ISet set) {
        for (IValue entry : set)
          entry.accept(this);
      }
    }
  }

  public static class StringRevImplode implements IFunction {
    public static final String name = "__string_rev_implode";

    @CafeEnter
    public static String enter(IValue data) throws EvaluationException {
      PrettyPrintDisplay blder = new PrettyPrintDisplay();

      StringRevPicker picker = new StringRevPicker(blder);
      data.accept(picker);
      return blder.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(tv, rawStringType));
    }

    private static class StringRevPicker implements IValueVisitor {
      private final PrettyPrintDisplay disp;

      StringRevPicker(PrettyPrintDisplay disp) {
        this.disp = disp;
      }

      @Override
      public void visitScalar(IScalar<?> scalar) {
        disp.append(scalar.toString());
      }

      @Override
      public void visitRecord(IRecord con) {
        int arity = con.size();
        for (int ix = arity - 1; ix >= 0; ix--)
          con.getCell(ix).accept(this);
      }

      @Override
      public void visitList(IList list) {
        for (int ix = list.size() - 1; ix >= 0; ix--)
          list.getCell(ix).accept(this);
      }

      @Override
      public void visitFunction(IFunction fn) {
      }

      @Override
      public void visitPattern(IPattern ptn) {
      }

      @Override
      public void visitConstructor(IConstructor con) {
        int arity = con.size();
        for (int ix = arity - 1; ix >= 0; ix--)
          con.getCell(ix).accept(this);
      }

      @Override
      public void visitMap(IMap map) {
        for (Entry<IValue, IValue> entry : map) {
          entry.getKey().accept(this);
          entry.getValue().accept(this);
        }
      }

      @Override
      public void visitSet(ISet set) {
        for (IValue entry : set)
          entry.accept(this);
      }
    }
  }

  public static class StringReplace implements IFunction {
    public static final String name = "__string_replace";

    @CafeEnter
    public static String enter(String s1, String s2, String s3) {
      return s1.replace(s2, s3);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0]), Factory.stringValue(args[1]), Factory
              .stringValue(args[2])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType stringType = StandardTypes.stringType;

      return TypeUtils.functionType(stringType, stringType, stringType, stringType);
    }
  }

  public static class StringReverse implements IFunction {
    public static final String name = "__string_reverse";

    @CafeEnter
    public static String enter(String s) {
      StringBuilder blder = new StringBuilder();
      int pos = s.length();
      while (pos > 0) {
        int ch = s.codePointBefore(pos);
        pos = s.offsetByCodePoints(pos, -1);
        blder.appendCodePoint(ch);
      }
      return blder.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType stringType = StandardTypes.rawStringType;

      return TypeUtils.functionType(stringType, stringType);
    }
  }

  public static class Spaces implements IFunction {
    public static final String name = "__spaces";

    @CafeEnter
    public static String enter(int s1) {
      return StringUtils.spaces(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawIntType, rawStringType);
    }
  }

  public static class StringFormat implements IFunction {
    public static String name = "__format_string";

    @CafeEnter
    public static String enter(String str, String format) throws EvaluationException {
      return format(str, format);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0]), Factory.stringValue(args[1])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, rawStringType, rawStringType);
    }
  }

  /**
   * The only legal format specifier for strings takes the form:
   * <p>
   * <p>
   * <pre>
   * L<decimal>
   * </pre>
   * <p>
   * <p>
   * <pre>
   * C<decimal>
   * </pre>
   * <p>
   * or
   * <p>
   * <p>
   * <pre>
   * R<decimal>
   * </pre>
   *
   * @param val    string to be formatted
   * @param format of the string
   * @return the formatted string
   * @throws EvaluationException
   */

  public static String format(String val, String format) throws EvaluationException {
    StringBuilder bldr = new StringBuilder();
    int size = val.length();

    int width = Integer.parseInt(format.substring(1));

    switch (format.charAt(0)) {
      case 'L': {
        if (width < size)
          return val.substring(0, width);
        else {
          bldr.append(val);
          for (int ix = 0; ix < size - width; ix++)
            bldr.append(' ');
        }
        return bldr.toString();
      }
      case 'C': {
        if (width < size)
          return val.substring(0, width);
        else {
          int ix;
          for (ix = 0; ix < (size - width) / 2; ix++)
            bldr.append(' ');
          bldr.append(val);
          for (; ix < size - width; ix++)
            bldr.append(' ');
        }
        return bldr.toString();
      }
      case 'R': {
        if (width < size)
          return val.substring(0, width);
        else {
          for (int ix = 0; ix < size - width; ix++)
            bldr.append(' ');
          bldr.append(val);
        }
        return bldr.toString();
      }
      default:
        throw new EvaluationException("**Format Error**");
    }
  }

  /**
   * Construct a unique string.
   *
   * @author fgm
   */
  public static class GenerateSym implements IFunction {
    public static final String name = "gensym";

    @Override
    public IType getType() {
      return programType();
    }

    @CafeEnter
    public static IValue enter(IValue prefix) throws EvaluationException {
      return Factory.newString(GenSym.genSym(Factory.stringValue(prefix)));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter(args[0]);
    }

    public static IType programType() {
      return TypeUtils.functionType(StandardTypes.stringType, StandardTypes.stringType);
    }
  }

  public static class StringChar implements IFunction {
    public static final String name = "__get_char";

    @CafeEnter
    public static int enter(String src, int ix) throws EvaluationException {
      return src.codePointAt(src.offsetByCodePoints(0, ix));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newChar(enter(Factory.stringValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, rawIntType, StandardTypes.rawCharType);
    }
  }

  public static class SubString implements IFunction {
    public static final String name = "__sub_string";

    @CafeEnter
    public static String enter(String s1, int from, int count) {
      int base = s1.offsetByCodePoints(0, from);
      int end = s1.offsetByCodePoints(base, count);

      return s1.substring(base, end);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory
              .newString(enter(Factory.stringValue(args[0]), Factory.intValue(args[1]), Factory.intValue(args[2])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType stringType = rawStringType;
      IType integerType = rawIntType;

      return TypeUtils.functionType(stringType, integerType, integerType, stringType);
    }
  }

  public static class SubstituteChar implements IFunction {
    public static final String name = "__substitute_char";

    @CafeEnter
    public static String enter(String src, int pos, int ch) throws EvaluationException {
      StringBuilder blder = new StringBuilder();
      int px = src.offsetByCodePoints(0, pos);
      int len = src.length();

      blder.append(src, 0, px);
      blder.appendCodePoint(ch);
      blder.append(src, src.offsetByCodePoints(px, 1), len);

      return blder.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0]), Factory.intValue(args[1]), Factory
              .charValue(args[2])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, rawIntType, StandardTypes.rawCharType, rawStringType);
    }
  }

  public static class DeleteChar implements IFunction {
    public static final String name = "__delete_char";

    @CafeEnter
    public static String enter(String src, int pos) throws EvaluationException {
      StringBuilder blder = new StringBuilder();
      int px = src.offsetByCodePoints(0, pos);
      int len = src.length();

      blder.append(src, 0, px);
      blder.append(src, src.offsetByCodePoints(px, 1), len);

      return blder.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType() {
      return programType();
    }

    public static IType programType() {
      return TypeUtils.functionType(rawStringType, rawIntType, rawStringType);
    }
  }

  public static class CharPresent implements IFunction {
    public static final String name = "__char_present";

    @CafeEnter
    public static boolean enter(String src, int ch) throws EvaluationException {
      return src.indexOf(ch) >= 0;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newBool(enter(Factory.stringValue(args[0]), Factory.charValue(args[2])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, StandardTypes.rawCharType, StandardTypes.booleanType);
    }
  }

  public static class StringSlice implements IFunction {
    public static final String name = "__string_slice";

    @CafeEnter
    public static String enter(String src, int pos, int to) {
      int base = src.offsetByCodePoints(0, pos);
      int rem = src.codePointCount(0, src.length());

      return src.substring(base, src.offsetByCodePoints(0, Math.min(rem, to)));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory
              .newString(enter(Factory.stringValue(args[0]), Factory.intValue(args[1]), Factory.intValue(args[2])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType stringType = rawStringType;
      IType integerType = rawIntType;

      return TypeUtils.functionType(stringType, integerType, integerType, stringType);
    }
  }

  public static class StringSplice implements IFunction {
    public static final String name = "__string_splice";

    @CafeEnter
    public static String enter(String src, int pos, int to, String replace) {
      int base = src.offsetByCodePoints(0, pos);
      int end = src.offsetByCodePoints(0, to);

      StringBuilder blder = new StringBuilder();

      blder.append(src, 0, base);
      blder.append(replace);
      blder.append(src, end, src.length());

      return blder.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0]), Factory.intValue(args[1]),
              Factory.intValue(args[2]), Factory.stringValue(args[3])));
    }

    @Override
    public IType getType() {
      return programType();
    }

    public static IType programType() {

      return TypeUtils.functionType(rawStringType, rawIntType, rawIntType, rawStringType, rawStringType);
    }
  }

  public static class ToUpperCase implements IFunction {
    public static final String name = "__uppercase";

    @CafeEnter
    public static String enter(String data) throws EvaluationException {
      return data.toUpperCase();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, rawStringType);
    }
  }

  public static class ToLowerCase implements IFunction {
    public static final String name = "__lowercase";

    @CafeEnter
    public static String enter(String data) throws EvaluationException {
      return data.toLowerCase();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newString(enter(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(rawStringType, rawStringType);
    }
  }
}
