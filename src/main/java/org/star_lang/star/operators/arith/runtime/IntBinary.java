package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;



/**
 * Binary arithmetic functions
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


public abstract class IntBinary
{

  public static class IntPlus implements IFunction
  {
    @CafeEnter
    public static  int enter(int s1, int s2)
    {
      return s1 + s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntMinus implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 - s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntTimes implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 * s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntDivide implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 / s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntRemainder implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 % s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntMin implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return Math.min(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntMax implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return Math.max(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntPwr implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return (int) Math.pow(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntLeft implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return (int) s1 << s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntRight implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return (int) s1 >> s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }
}
