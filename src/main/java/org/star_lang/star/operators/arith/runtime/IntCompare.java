package org.star_lang.star.operators.arith.runtime;

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
public abstract class IntCompare
{
  private static final IType rawIntegerType = StandardTypes.rawIntegerType;

  public static class IntEQ implements IFunction
  {
    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 == ix2;
    }

    public static final String name = "__integer_eq";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawIntegerType, rawIntegerType, StandardTypes.rawBoolType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }
  }

  public static class IntNE implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(int ix1, int ix2)
    {
      return Factory.newBool(ix1 != ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawIntegerType, rawIntegerType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]), Factory.intValue(args[1]));
    }
  }

  public static class IntLE implements IFunction
  {
    public static final String name = "__integer_le";

    @CafeEnter
    public static BoolWrap enter(int ix1, int ix2)
    {
      return Factory.newBool(ix1 <= ix2);
    }

    public static final IType rawIntegerType = StandardTypes.rawIntegerType;

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawIntegerType, rawIntegerType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]), Factory.intValue(args[1]));
    }
  }

  public static class IntLT implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(int ix1, int ix2)
    {
      return Factory.newBool(ix1 < ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawIntegerType, rawIntegerType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]), Factory.intValue(args[1]));
    }
  }

  public static class IntGT implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(int ix1, int ix2)
    {
      return Factory.newBool(ix1 > ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawIntegerType, rawIntegerType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]), Factory.intValue(args[1]));
    }
  }

  public static class IntGE implements IFunction
  {
    public static final String name = "__integer_ge";

    @CafeEnter
    public static BoolWrap enter(int ix1, int ix2)
    {
      return Factory.newBool(ix1 >= ix2);
    }


    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawIntegerType, rawIntegerType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]), Factory.intValue(args[1]));
    }
  }
}
