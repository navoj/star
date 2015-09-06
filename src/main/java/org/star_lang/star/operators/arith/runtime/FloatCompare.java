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


public abstract class FloatCompare
{
  private static final IType floatType = StandardTypes.rawFloatType;

  public static class FloatEQ implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(double ix1, double ix2)
    {
      return Factory.newBool(ix1 == ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(floatType, floatType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.fltValue(args[0]), Factory.fltValue(args[1]));
    }
  }

  public static class FloatNE implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(double ix1, double ix2)
    {
      return Factory.newBool(ix1 != ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(floatType, floatType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.fltValue(args[0]), Factory.fltValue(args[1]));
    }
  }

  public static class FloatLE implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(double ix1, double ix2)
    {
      return Factory.newBool(ix1 <= ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(floatType, floatType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.fltValue(args[0]), Factory.fltValue(args[1]));
    }
  }

  public static class FloatLT implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(double ix1, double ix2)
    {
      return Factory.newBool(ix1 < ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(floatType, floatType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.fltValue(args[0]), Factory.fltValue(args[1]));
    }
  }

  public static class FloatGT implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(double ix1, double ix2)
    {
      return Factory.newBool(ix1 > ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(floatType, floatType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.fltValue(args[0]), Factory.fltValue(args[1]));
    }
  }

  public static class FloatGE implements IFunction
  {
    @CafeEnter
    public static BoolWrap enter(double ix1, double ix2)
    {
      return Factory.newBool(ix1 >= ix2);
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(floatType, floatType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.fltValue(args[0]), Factory.fltValue(args[1]));
    }
  }

}
