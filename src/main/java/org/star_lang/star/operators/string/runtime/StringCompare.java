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