package org.star_lang.star.operators.arith.runtime;

import java.math.BigDecimal;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

/**
 * Binary arithmetic functions
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


public class BignumBinary
{

  public static class BigNumPlus implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1, BigDecimal s2)
    {
      return s1.add(s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, decimalType);
    }
  }

  public static class BigNumMinus implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1, BigDecimal s2)
    {
      return s1.subtract(s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, decimalType);
    }
  }

  public static class BigNumTimes implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1, BigDecimal s2)
    {
      return s1.multiply(s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, decimalType);
    }
  }

  public static class BigNumDivide implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1, BigDecimal s2)
    {
      return s1.divide(s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, decimalType);
    }
  }

  public static class BigNumRemainder implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1, BigDecimal s2)
    {
      return s1.remainder(s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, decimalType);
    }
  }

  public static class BigNumMin implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1, BigDecimal s2)
    {
      return s1.min(s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, decimalType);
    }
  }

  public static class BigNumMax implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1, BigDecimal s2)
    {
      return s1.max(s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, decimalType);
    }
  }

  public static class BigNumPwr implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1, BigDecimal s2)
    {
      return s1.pow(s2.intValue());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, decimalType);
    }
  }
}
