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



public class BigNumUnary
{
  public static class DecimalAbs implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1)
    {
      return s1.abs();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType);
    }
  }

  public static class DecimalUMinus implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1)
    {
      return s1.negate();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType);
    }
  }

  public static class DecimalRandom implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1)
    {
      double random = Math.random();
      return s1.multiply(new BigDecimal(random));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType);
    }
  }

  public static class DecimalSqrt implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1) throws EvaluationException
    {
      throw new EvaluationException("sqrt not implemented for decimal numbers");
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType);
    }
  }

  public static class DecimalHash implements IFunction {
    public static final String name = "__decimal_hash";

    @CafeEnter
    public static int enter(BigDecimal s1) {
      return  s1.hashCode();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newInt(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type(){
      return TypeUtils.functionType(StandardTypes.rawDecimalType, StandardTypes.rawIntegerType);
    }
  }
}
