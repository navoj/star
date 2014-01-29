package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.BoolWrap;
import com.starview.platform.data.value.Factory;

/**
 * Miscellaneous unicode functions for characters
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
