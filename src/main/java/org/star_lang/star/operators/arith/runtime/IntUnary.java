package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

/**
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
public abstract class IntUnary
{

  public static class IntAbs implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return Math.abs(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntUMinus implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return -s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntRandom implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      double random = Math.random();
      return new Double(random * s1).intValue();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntSqrt implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.sqrt(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntCbrt implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.cbrt(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntCeil implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntFloor implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntRound implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntLog implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.log(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntLog10 implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.log10(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntExp implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.exp(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }
}
