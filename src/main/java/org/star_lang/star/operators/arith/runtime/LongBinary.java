package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
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

public abstract class LongBinary
{

  public static class LongPlus implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 + s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongMinus implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 - s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongTimes implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 * s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongDivide implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 / s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongRemainder implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 % s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongMin implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return Math.min(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongMax implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return Math.max(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongPwr implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return (long) Math.pow(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  @SuppressWarnings("RedundantCast")
  public static class LongLeft implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 << s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  @SuppressWarnings("RedundantCast")
  public static class LongRight implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 >> s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }
}
