package org.star_lang.star.operators.string.runtime;

import java.math.BigDecimal;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.BoolWrap;
import com.starview.platform.data.value.Factory;

/**
 * Runtime parts of the String2Number functions that parse strings into different kinds of numbers
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

public abstract class String2Number
{
  public static class String2Boolean implements IFunction
  {
    public static final String name = "__string_boolean";

    @CafeEnter
    public static BoolWrap __string_boolean(String txt) throws EvaluationException
    {
      try {
        return Factory.newBool(Boolean.parseBoolean(txt));
      } catch (NumberFormatException e) {
        throw new EvaluationException(StringUtils.quoteString(txt) + " cannot be parsed as a boolean");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __string_boolean(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.booleanType);
    }
  }

  public static class String2Char implements IFunction
  {
    public static final String name = "__string_char";

    @CafeEnter
    public static int __string_char(String txt) throws EvaluationException
    {
      return txt.codePointAt(0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newChar(__string_char(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.rawCharType);
    }
  }

  public static class String2Integer implements IFunction
  {
    public static final String name = "__string_integer";

    @CafeEnter
    public static int __string_integer(String txt) throws EvaluationException
    {
      try {
        return Integer.parseInt(txt);
      } catch (NumberFormatException e) {
        throw new EvaluationException(StringUtils.quoteString(txt) + " cannot be parsed as an integer");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(__string_integer(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.rawIntegerType);
    }
  }

  public static class String2Long implements IFunction
  {
    public static final String name = "__string_long";

    @CafeEnter
    public static long __string_long(String txt) throws EvaluationException
    {
      try {
        if (txt.endsWith("l"))
          txt = txt.substring(0, txt.indexOf('l'));
        else if (txt.endsWith("L"))
          txt = txt.substring(0, txt.indexOf('L'));
        return Long.parseLong(txt);
      } catch (NumberFormatException e) {
        throw new EvaluationException(StringUtils.quoteString(txt) + " cannot be parsed as an integer");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(__string_long(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.rawLongType);
    }
  }

  public static class Hex2Integer implements IFunction
  {
    public static final String name = "__hex_integer";

    @CafeEnter
    public static int __hex_integer(String txt) throws EvaluationException
    {
      try {
        return Integer.parseInt(txt, 16);
      } catch (NumberFormatException e) {
        throw new EvaluationException(StringUtils.quoteString(txt) + " cannot be parsed as an integer");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(__hex_integer(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.rawIntegerType);
    }
  }

  public static class Hex2Long implements IFunction
  {
    public static final String name = "__hex_long";

    @CafeEnter
    public static long __hex_long(String txt) throws EvaluationException
    {
      try {
        if (txt.endsWith("l"))
          txt = txt.substring(0, txt.indexOf('l'));
        else if (txt.endsWith("L"))
          txt = txt.substring(0, txt.indexOf('L'));
        return Long.parseLong(txt, 16);
      } catch (NumberFormatException e) {
        throw new EvaluationException(StringUtils.quoteString(txt) + " cannot be parsed as an integer");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(__hex_long(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.rawLongType);
    }
  }

  public static class String2Float implements IFunction
  {
    public static final String name = "__string_float";

    @CafeEnter
    public static double __string_float(String txt) throws EvaluationException
    {
      try {
        return Double.parseDouble(txt);
      } catch (NumberFormatException e) {
        throw new EvaluationException(StringUtils.quoteString(txt) + " cannot be parsed as a floating point number");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(__string_float(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.rawFloatType);
    }
  }

  public static class String2Decimal implements IFunction
  {
    public static final String name = "__string_decimal";

    @CafeEnter
    public static BigDecimal __string_decimal(String txt) throws EvaluationException
    {
      try {
        if (txt.endsWith("a"))
          txt = txt.substring(0, txt.indexOf('a'));
        else if (txt.endsWith("A"))
          txt = txt.substring(0, txt.indexOf('A'));
        return new BigDecimal(txt);
      } catch (NumberFormatException e) {
        throw new EvaluationException(StringUtils.quoteString(txt) + " cannot be parsed as an decimal number");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(String2Decimal.__string_decimal(Factory.stringValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.rawDecimalType);
    }
  }
}
