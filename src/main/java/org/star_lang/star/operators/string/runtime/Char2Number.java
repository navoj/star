package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

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

public class Char2Number
{
  public static class Char2Int implements IFunction
  {
    public static final String name = "__char_integer";

    @CafeEnter
    public static int enter(int ch)
    {
      return ch;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawCharType, StandardTypes.rawIntegerType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.charValue(args[0])));
    }
  }

  public static class Int2Char implements IFunction
  {
    public static final String name = "__integer_char";

    @CafeEnter
    public static int enter(int ch)
    {
      return ch;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawCharType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newChar(enter(Factory.intValue(args[0])));
    }
  }
}
