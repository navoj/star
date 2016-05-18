package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

import java.math.BigDecimal;

public abstract class Number2String
{
  private static final String FORMAT_ERROR = "*Error*";
  private static final IType rawIntType = StandardTypes.rawIntegerType;
  private static final IType rawLongType = StandardTypes.rawLongType;
  private static final IType rawFloatType = StandardTypes.rawFloatType;
  private static final IType rawStringType = StandardTypes.rawStringType;

  private static final int DECIMAL = 10;
  private static final int HEX = 16;

  public static class Boolean2String implements IFunction
  {
    public static final String name = "__boolean_string";

    @CafeEnter
    public static String __boolean_string(boolean b) throws EvaluationException
    {
      return Boolean.toString(b);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(__boolean_string(Factory.boolValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawBoolType, rawStringType);
    }
  }

  public static class CodePoint2String implements IFunction
  {
    public static final String name = "__cp_string";

    @CafeEnter
    public static String __cp_string(int ch) throws EvaluationException
    {
      PrettyPrintDisplay disp = new PrettyPrintDisplay();
      disp.appendChar(ch);
      return disp.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(__cp_string(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawIntType, rawStringType);
    }
  }

  public static class Integer2String implements IFunction
  {
    public static final String name = "__integer_string";

    @CafeEnter
    public static String __integer_string(int i) throws EvaluationException
    {
      return Integer.toString(i);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(__integer_string(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, rawStringType);
    }
  }

  public static class Integer2Hex implements IFunction
  {
    public static final String name = "__integer_hex";

    @CafeEnter
    public static String __integer_hex_string(int i) throws EvaluationException
    {
      return Integer.toHexString(i);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(__integer_hex_string(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, rawStringType);
    }
  }

  public static class FormatInteger implements IFunction
  {
    public static final String name = "__format_integer";

    @CafeEnter
    public static String entry(int Ix, String format) throws EvaluationException
    {
      return format(Ix, format);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(entry(Factory.intValue(args[0]), Factory.stringValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawIntType, rawStringType, rawStringType);
    }
  }

  public static class FormatLong implements IFunction
  {
    public static final String name = "__format_long";

    @CafeEnter
    public static String entry(long Ix, String format) throws EvaluationException
    {
      return format(Ix, format);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(entry(Factory.lngValue(args[0]), Factory.stringValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawLongType, rawStringType, rawStringType);
    }
  }

  private static int formatRadix(String format)
  {
    if (format.contains("X"))
      return HEX;
    else
      return DECIMAL;
  }

  public static String format(long val, String format) throws EvaluationException
  {
    boolean signed;
    if (val < 0) {
      signed = true;
      val = -val;
    } else
      signed = false;

    String rawStr = Long.toString(val, formatRadix(format));
    return formatDigits(signed, rawStr, format);
  }

  private static String formatDigits(boolean signed, String digits, String format) throws EvaluationException
  {
    StringBuilder bldr = new StringBuilder();

    int limit = digits.length();
    int sigDigits = countSignificants(digits, 0, limit, "0123456789ABCDEFabcdef");
    int formSigDigits = countSignificants(format, 0, format.length(), "09X ");
    boolean encounteredSign = false;
    int zeroDigits = countSignificants(format, 0, format.length(), "0 ");

    if (sigDigits > formSigDigits)
      return formatError(format);

    for (int ix = format.length() - 1, px = limit - 1; ix >= 0; ix = stepBack(format, ix)) {
      int formChar = format.codePointAt(ix);
      switch (formChar) {
      case '-':
        if (signed)
          attachChar(bldr, '-');
        else
          bldr.append(' ');
        break;
      case '+':
        if (signed)
          attachChar(bldr, '-');
        else
          attachChar(bldr, '+');
        break;
      case 'P':
        if (signed) {
          if (encounteredSign)
            attachChar(bldr, '(');
          else {
            attachChar(bldr, ')');
            encounteredSign = true;
          }
        } else
          bldr.append(' ');
        break;
      case '.':
        if (px >= 0 || zeroDigits > 0)
          bldr.appendCodePoint(formChar);
        break;
      case ',':
      default:
        if (px >= 0 || zeroDigits > 0)
          bldr.appendCodePoint(formChar);
        break;
      case ' ':
        if (px >= 0) { // more of the raw result to write out
          bldr.appendCodePoint(digits.codePointAt(px));
          px = stepBack(digits, px);
        } else if (zeroDigits > 0)
          bldr.append(' ');

        zeroDigits--;
        break;
      case '0':
        if (px >= 0) { // more of the raw result to write out
          bldr.appendCodePoint(digits.codePointAt(px));
          px = stepBack(digits, px);
        } else if (zeroDigits > 0)
          bldr.append('0');

        zeroDigits--;
        break;
      case '9':
      case 'X':
        if (px >= 0) { // more of the raw result to write out
          bldr.appendCodePoint(digits.codePointAt(px));
          px = stepBack(digits, px);
        }
        break;
      case 'e':
      case 'E':
      case 'L':
      case 'R':
        return formatError(format);
      }
    }

    bldr.reverse();
    return bldr.toString();
  }

  public static class FormatFloat implements IFunction
  {
    public static final String name = "__format_float";

    @CafeEnter
    public static String entry(double Dx, String format) throws EvaluationException
    {
      return format(Dx, format);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(entry(Factory.fltValue(args[0]), Factory.stringValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawFloatType, rawStringType, rawStringType);
    }
  }

  /**
   * The floating point format looks like:
   * 
   * <code>S?(N|,)*.N+ESN+</code> where S is a sign spec, N is a number spec and . is the period.
   * 
   * @param val
   * @param format
   * @return
   * @throws EvaluationException
   */

  public static String format(double val, String format) throws EvaluationException
  {
    boolean signedVal;
    if (val < 0) {
      signedVal = true;
      val = -val;
    } else
      signedVal = false;

    // We need to split the format into its segments
    int dotPos = format.indexOf('.');
    if (dotPos < 0)
      return formatError(format);
    int ePos = format.indexOf('e', dotPos);
    if (ePos < 0)
      ePos = format.indexOf('E', dotPos);

    int beforePeriod = countSignificants(format, 0, dotPos, "09 ");
    int afterPeriod = countSignificants(format, dotPos, ePos >= 0 ? ePos : format.length(), "09 ");
    int precision = countSignificants(format, 0, ePos >= 0 ? ePos : format.length(), "09 ");
    StringBuilder dbld = new StringBuilder();
    int exp10 = number2Str(val, precision, dbld);
    String digits = dbld.toString();

    // adjust exp10 to reflect beforePeriod digits
    exp10 -= beforePeriod - 1;

    // Check that we can fit the number in
    if (ePos < 0) {
      // no space for an exponent
      if (exp10 > beforePeriod || exp10 < -afterPeriod)
        throw new EvaluationException(StringUtils.msg("cannot format ", val, " using ", format));
      else
        return formatDigits(signedVal, digits, format);

    } else {
      String mantissa = formatDigits(signedVal, digits, format.substring(0, ePos));
      String exponent = formatDigits(exp10 < 0, Long.toString(Math.abs(exp10)), format.substring(ePos + 1));
      return mantissa + format.charAt(ePos) + exponent;
    }
  }

  static private final double bitValues[] = new double[] { 1.0e1, 1.0e2, 1.0e4, 1.0e8, 1.0e16, 1.0e32, 1.0e64, 1.0e128,
      1.0e256 };

  static private int number2Str(double x, int precision, StringBuilder blder)
  {
    if (x == 0.0) {
      blder.append("0");
      return 0;
    } else if (x > Double.MAX_VALUE) {
      blder.append("infinity");
      return 1;
    } else {
      int exp2 = Math.getExponent(x);
      int exp10 = (int) (exp2 * Math.log10(2.0));
      double fraction = x;

      int n = exp10;
      int px = 0; // index into bitValues
      if (n < 0) { // scale fraction
        for (n = -n; n != 0; px++, n >>= 1)
          if ((n & 1) == 1)
            fraction *= bitValues[px];

      } else if (n > 0) {
        double f = 1.0;
        for (; n != 0; px++, n >>= 1)
          if ((n & 1) == 1)
            f *= bitValues[px];
        fraction /= f;
      }

      while (fraction > 10.0) {
        fraction *= 0.1;
        exp10++;
      }

      while (fraction < 1) {
        fraction *= 10.0;
        exp10--;
      }

      while (precision-- > 0) {
        double front = Math.floor(fraction);
        fraction = 10 * (fraction - front);

        blder.appendCodePoint(Character.forDigit((int) front, 10));
      }
      return exp10;
    }
  }

  public static String formatError(String format)
  {
    if (format.length() < FORMAT_ERROR.length())
      return FORMAT_ERROR.substring(0, format.length());
    else
      return FORMAT_ERROR + StringUtils.spaces(format.length() - FORMAT_ERROR.length());
  }

  private static void attachChar(StringBuilder b, int ch)
  {
    int lx = b.length();
    while (lx > 0 && b.codePointAt(lx - 1) == ' ')
      lx--;
    b.insert(lx, (char) ch);
  }

  private static int stepBack(String str, int px)
  {
    if (px > 0)
      return str.offsetByCodePoints(px, -1);
    else
      return -1;
  }

  private static int countSignificants(String str, int from, int limit, String test)
  {
    int cx = 0;
    for (int ix = from; ix < limit; ix = str.offsetByCodePoints(ix, 1)) {
      if (test.indexOf(str.codePointAt(ix)) >= 0)
        cx++;
    }
    return cx;
  }

  public static class Long2String implements IFunction
  {
    public static final String name = "__long_string";

    @CafeEnter
    public static String __long_string(long i) throws EvaluationException
    {
      return Long.toString(i);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(__long_string(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawLongType, rawStringType);
    }
  }

  public static class Long2Hex implements IFunction
  {
    public static final String name = "__long_hex";

    @CafeEnter
    public static String __long_hex_string(long i) throws EvaluationException
    {
      return Long.toHexString(i);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(__long_hex_string(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawLongType, rawStringType);
    }
  }

  public static class Float2String implements IFunction
  {
    public static final String name = "__float_string";

    @CafeEnter
    public static String __float_string(double dx) throws EvaluationException
    {
      return Double.toString(dx);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(__float_string(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawFloatType, rawStringType);
    }
  }

  public static class Decimal2String implements IFunction
  {
    public static final String name = "__decimal_string";

    @CafeEnter
    public static String __decimal_string(BigDecimal bg) throws EvaluationException
    {
      return bg.toString();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newString(__decimal_string(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawDecimalType, rawStringType);
    }
  }
}
