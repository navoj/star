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
