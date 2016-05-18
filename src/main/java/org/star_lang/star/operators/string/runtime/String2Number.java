package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.*;
import org.star_lang.star.operators.CafeEnter;

import java.math.BigDecimal;

/*
 * Runtime parts of the String2Number functions that parse strings into different kinds of numbers
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

public abstract class String2Number {
  public static class String2Boolean implements IFunction {
    public static final String name = "__string_boolean";

    @CafeEnter
    public static BoolWrap __string_boolean(String txt) throws EvaluationException {
      try {
        return Factory.newBool(Boolean.parseBoolean(txt));
      } catch (NumberFormatException e) {
        throw new EvaluationException(StringUtils.quoteString(txt) + " cannot be parsed as a boolean");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return __string_boolean(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.booleanType);
    }
  }

  public static class String2Integer implements IFunction {
    public static final String name = "__string_integer";

    @CafeEnter
    public static IntWrap __string_integer(String txt) throws EvaluationException {
      try {
        return Factory.newInt(Integer.parseInt(txt));
      } catch (NumberFormatException e) {
        return IntWrap.nonIntegerEnum;
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return __string_integer(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.integerType);
    }
  }

  public static class String2Long implements IFunction {
    public static final String name = "__string_long";

    @CafeEnter
    public static LongWrap __string_long(String txt) throws EvaluationException {
      try {
        if (txt.endsWith("l"))
          txt = txt.substring(0, txt.indexOf('l'));
        else if (txt.endsWith("L"))
          txt = txt.substring(0, txt.indexOf('L'));
        return Factory.newLng(Long.parseLong(txt));
      } catch (NumberFormatException e) {
        return LongWrap.nonLongEnum;
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return __string_long(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.longType);
    }
  }

  public static class Hex2Integer implements IFunction {
    public static final String name = "__hex_integer";

    @CafeEnter
    public static IntWrap __hex_integer(String txt) throws EvaluationException {
      try {
        return Factory.newInt(Integer.parseInt(txt, 16));
      } catch (NumberFormatException e) {
        return IntWrap.nonIntegerEnum;
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return __hex_integer(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.integerType);
    }
  }

  public static class Hex2Long implements IFunction {
    public static final String name = "__hex_long";

    @CafeEnter
    public static LongWrap __hex_long(String txt) throws EvaluationException {
      try {
        if (txt.endsWith("l"))
          txt = txt.substring(0, txt.indexOf('l'));
        else if (txt.endsWith("L"))
          txt = txt.substring(0, txt.indexOf('L'));
        return Factory.newLng(Long.parseLong(txt, 16));
      } catch (NumberFormatException e) {
        return LongWrap.nonLongEnum;
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return __hex_long(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.longType);
    }
  }

  public static class String2Float implements IFunction {
    public static final String name = "__string_float";

    @CafeEnter
    public static FloatWrap __string_float(String txt) throws EvaluationException {
      try {
        return Factory.newFlt(Double.parseDouble(txt));
      } catch (NumberFormatException e) {
        return FloatWrap.nonFloatEnum;
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return __string_float(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.floatType);
    }
  }

  public static class String2Decimal implements IFunction {
    public static final String name = "__string_decimal";

    @CafeEnter
    public static BigNumWrap __string_decimal(String txt) throws EvaluationException {
      try {
        if (txt.endsWith("a"))
          txt = txt.substring(0, txt.indexOf('a'));
        else if (txt.endsWith("A"))
          txt = txt.substring(0, txt.indexOf('A'));
        return Factory.newDecimal(new BigDecimal(txt));
      } catch (NumberFormatException e) {
        throw new EvaluationException("invalid decimal number");
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return String2Decimal.__string_decimal(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.decimalType);
    }
  }
}
