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
