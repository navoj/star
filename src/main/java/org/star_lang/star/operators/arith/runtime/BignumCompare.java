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
