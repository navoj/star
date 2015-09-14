package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.BoolWrap;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

/*
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

public abstract class StringCompare implements IFunction
{
  private static final IType rawStringStype = StandardTypes.rawStringType;

  @Override
  public IType getType()
  {
    return TypeUtils.functionType(rawStringStype, rawStringStype, StandardTypes.booleanType);
  }

  public static class StringEQ extends StringCompare
  {
    @CafeEnter
    public static BoolWrap __string_eq(String s1, String s2)
    {
      return Factory.newBool(s1.compareTo(s2) == 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __string_eq(Factory.stringValue(args[0]), Factory.stringValue(args[1]));
    }
  }

  public static class StringNE extends StringCompare
  {
    @CafeEnter
    public static BoolWrap __string_ne(String s1, String s2)
    {
      return Factory.newBool(s1.compareTo(s2) != 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __string_ne(Factory.stringValue(args[0]), Factory.stringValue(args[1]));
    }
  }

  public static class StringLT extends StringCompare
  {
    @CafeEnter
    public static BoolWrap __string_lt(String s1, String s2)
    {
      return Factory.newBool(s1.compareTo(s2) < 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __string_lt(Factory.stringValue(args[0]), Factory.stringValue(args[1]));
    }
  }

  public static class StringLE extends StringCompare
  {
    @CafeEnter
    public static BoolWrap __string_le(String s1, String s2)
    {
      return Factory.newBool(s1.compareTo(s2) <= 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __string_le(Factory.stringValue(args[0]), Factory.stringValue(args[1]));
    }
  }

  public static class StringGE extends StringCompare
  {
    @CafeEnter
    public static BoolWrap __string_ge(String s1, String s2)
    {
      return Factory.newBool(s1.compareTo(s2) >= 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __string_ge(Factory.stringValue(args[0]), Factory.stringValue(args[1]));
    }
  }

  public static class StringGT extends StringCompare
  {
    @CafeEnter
    public static BoolWrap __string_gt(String s1, String s2)
    {
      return Factory.newBool(s1.compareTo(s2) > 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __string_gt(Factory.stringValue(args[0]), Factory.stringValue(args[1]));
    }
  }
}