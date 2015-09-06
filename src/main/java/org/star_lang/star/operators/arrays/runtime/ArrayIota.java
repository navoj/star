package org.star_lang.star.operators.arrays.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Array;
import org.star_lang.star.data.value.ArrayBase;
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
public class ArrayIota
{
  public static class ArrayIntegerIota implements IFunction
  {
    public static final String name = "__integer_array_iota";

    @CafeEnter
    public static IArray enter(int from, int to, int step) throws EvaluationException
    {
      int count = (to - from + step) / step;

      IValue[] data = new IValue[count];
      for (int ix = 0; ix < count; ix++)
        data[ix] = Factory.newInt(from + ix * step);

      return new Array(new ArrayBase(data, 0, count), 0, count);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]), Factory.intValue(args[1]), Factory.intValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType intType = StandardTypes.integerType;
      IType rawIntType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(rawIntType, rawIntType, rawIntType, TypeUtils.arrayType(intType));
    }
  }

  public static class ArrayLongIota implements IFunction
  {
    public static final String name = "__long_array_iota";

    @CafeEnter
    public static IArray enter(long from, long to, long step) throws EvaluationException
    {
      int count = (int) ((to - from + step) / step);

      IValue[] data = new IValue[count];
      for (int ix = 0; ix < count; ix++)
        data[ix] = Factory.newLng(from + ix * step);

      return new Array(new ArrayBase(data, 0, count), 0, count);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.lngValue(args[0]), Factory.lngValue(args[1]), Factory.lngValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType longType = StandardTypes.longType;
      IType lngType = StandardTypes.rawLongType;
      return TypeUtils.functionType(lngType, lngType, lngType, TypeUtils.arrayType(longType));
    }
  }

  public static class ArrayFloatIota implements IFunction
  {
    public static final String name = "__float_array_iota";

    @CafeEnter
    public static IArray enter(double from, double to, double step) throws EvaluationException
    {
      int count = (int) ((to - from + step) / step);

      IValue[] data = new IValue[count];
      for (int ix = 0; ix < count; ix++)
        data[ix] = Factory.newFlt(from + ix * step);

      return new Array(new ArrayBase(data, 0, count), 0, count);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.fltValue(args[0]), Factory.fltValue(args[1]), Factory.fltValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType floatType = StandardTypes.floatType;
      IType fltType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(fltType, fltType, fltType, TypeUtils.arrayType(floatType));
    }
  }
}
