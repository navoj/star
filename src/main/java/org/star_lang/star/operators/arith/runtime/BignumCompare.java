package org.star_lang.star.operators.arith.runtime;

import java.math.BigDecimal;

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

public abstract class BignumCompare
{

  @CafeEnter
  public abstract BoolWrap enter(BigDecimal s1, BigDecimal s2);

  public static class BignumEQ implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(BigDecimal ix1, BigDecimal ix2)
    {
      return Factory.newBool(ix1.compareTo(ix2) == 0);
    }

    public static final String name = "__decimal_eq";

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, StandardTypes.booleanType);
    }
  }

  public static class BignumNE implements IFunction
  {
    public static final String name = "__decimal_ne";

    @CafeEnter
    public static BoolWrap enter(BigDecimal ix1, BigDecimal ix2)
    {
      return Factory.newBool(ix1.compareTo(ix2) != 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, StandardTypes.booleanType);
    }
  }

  public static class BignumLE implements IFunction
  {
    public static final String name = "__decimal_le";

    @CafeEnter
    public static BoolWrap enter(BigDecimal ix1, BigDecimal ix2)
    {
      return Factory.newBool(ix1.compareTo(ix2) <= 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, StandardTypes.booleanType);
    }
  }

  public static class BignumLT implements IFunction
  {
    public static final String name = "__decimal_lt";

    @CafeEnter
    public static BoolWrap enter(BigDecimal ix1, BigDecimal ix2)
    {
      return Factory.newBool(ix1.compareTo(ix2) < 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, StandardTypes.booleanType);
    }
  }

  public static class BignumGT implements IFunction
  {
    public static final String name = "__decimal_gt";

    @CafeEnter
    public static BoolWrap enter(BigDecimal ix1, BigDecimal ix2)
    {
      return Factory.newBool(ix1.compareTo(ix2) > 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, StandardTypes.booleanType);
    }
  }

  public static class BignumGE implements IFunction
  {
    public static final String name = "__decimal_ge";

    @CafeEnter
    public static BoolWrap enter(BigDecimal ix1, BigDecimal ix2)
    {
      return Factory.newBool(ix1.compareTo(ix2) >= 0);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.decimalValue(args[0]), Factory.decimalValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType, StandardTypes.booleanType);
    }
  }
}
