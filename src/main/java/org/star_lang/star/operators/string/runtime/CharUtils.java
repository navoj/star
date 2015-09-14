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

/**
 * Miscellaneous unicode functions for characters
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

public class CharUtils
{
  private static final IType rawCharType = StandardTypes.rawCharType;

  public static class IsIdentifierStart implements IFunction
  {
    public static final String name = "__isIdentifierStart";

    @CafeEnter
    public static BoolWrap enter(int ch)
    {
      return Factory.newBool(Character.isUnicodeIdentifierStart(ch) || ch == '_');
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.charValue(args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, StandardTypes.booleanType);
    }
  }

  public static class IsIdentifierPart implements IFunction
  {
    public static final String name = "__isIdentifierPart";

    @CafeEnter
    public static BoolWrap __isIdenChar(int ch)
    {
      return Factory.newBool(Character.isUnicodeIdentifierPart(ch) || ch == '_');
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __isIdenChar(Factory.charValue(args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, StandardTypes.booleanType);
    }
  }

  public static class IsUnicodeIdentifier implements IFunction
  {
    public static final String name = "__isUnicodeIdentifier";

    @CafeEnter
    public static BoolWrap __isUnicodeIdentifier(String id)
    {
      int ix = 0;
      int mx = id.codePointCount(0, id.length());
      int cp = id.codePointAt(0);
      if (!Character.isUnicodeIdentifierStart(cp) && cp != '_')
        return Factory.newBool(false);
      else
        ix = id.offsetByCodePoints(ix, 1);
      while (ix < mx) {
        cp = id.codePointAt(ix);
        if (!Character.isUnicodeIdentifierPart(cp) && cp != '_')
          return Factory.newBool(false);
        else
          ix = id.offsetByCodePoints(ix, 1);
      }
      return Factory.newBool(true);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return __isUnicodeIdentifier(Factory.stringValue(args[0]));
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

  public static class IsLowerCase implements IFunction
  {
    public static final String name = "__isLowerCase";

    @CafeEnter
    public static boolean enter(int ch)
    {
      return Character.isUpperCase(ch);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, StandardTypes.booleanType);
    }
  }

  public static class IsUpperCase implements IFunction
  {
    public static final String name = "__isUpperCase";

    @CafeEnter
    public static boolean enter(int ch)
    {
      return Character.isUpperCase(ch);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, StandardTypes.booleanType);
    }
  }
}
