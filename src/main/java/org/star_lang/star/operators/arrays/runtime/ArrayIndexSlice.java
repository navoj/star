package org.star_lang.star.operators.arrays.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Array;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.Option;
import org.star_lang.star.data.value.NTuple.NTpl;
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
public class ArrayIndexSlice
{

  public static class ArrayEl implements IFunction
  {
    public static final String name = "__array_el";

    @CafeEnter
    public static IValue enter(IArray ar1, int ix) throws EvaluationException
    {
      if (ix >= 0 && ix < ar1.size())
        return ar1.getCell(ix);
      else
        return null;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], Factory.intValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(array of %e,integer_)=> %e
      TypeVar e = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.arrayType(e), StandardTypes.rawIntegerType, e);
      return new UniversalType(e, funType);
    }
  }

  public static class ArrayElement implements IFunction
  {
    public static final String name = "__array_element";

    @CafeEnter
    public static IValue enter(IArray ar1, int ix) throws EvaluationException
    {
      if (ix >= 0 && ix < ar1.size())
        return Option.some(ar1.getCell(ix));
      else
        return Option.noneEnum;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], Factory.intValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(array of %e,integer_)=>option of %e
      TypeVar e = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.arrayType(e), StandardTypes.rawIntegerType, TypeUtils
          .optionType(e));
      return new UniversalType(e, funType);
    }
  }

  public static class ArraySlice implements IFunction
  {
    public static final String name = "__array_slice";

    @CafeEnter
    public static Array enter(Array array, int from, int to) throws EvaluationException
    {
      return array.slice(from, to);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Array) args[0], Factory.intValue(args[1]), Factory.intValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType listType = TypeUtils.arrayType(v);
      return new UniversalType(v, TypeUtils.functionType(listType, StandardTypes.rawIntegerType,
          StandardTypes.rawIntegerType, listType));
    }
  }

  public static class ArraySplice implements IFunction
  {
    public static final String name = "__array_splice";

    @CafeEnter
    public static IArray enter(Array array, int from, int to, Array sub) throws EvaluationException
    {
      return array.spliceList(sub, from, to);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Array) args[0], Factory.intValue(args[1]), Factory.intValue(args[2]), (Array) args[3]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType arrayType = TypeUtils.arrayType(v);
      return new UniversalType(v, TypeUtils.functionType(arrayType, StandardTypes.rawIntegerType,
          StandardTypes.rawIntegerType, arrayType, arrayType));
    }
  }

  public static class ArrayDeleteElement implements IFunction
  {
    public static final String name = "__array_delete_element";

    @CafeEnter
    public static IValue enter(IArray ar1, int ix) throws EvaluationException
    {
      return ar1.removeCell(ix);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], Factory.intValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(array of %e,integer_)=> array of %e
      TypeVar e = new TypeVar();
      IType arrayType = TypeUtils.arrayType(e);

      return new UniversalType(e, TypeUtils.functionType(arrayType, StandardTypes.rawIntegerType, arrayType));
    }
  }

  public static class ArrayIndexElement implements IPattern
  {
    public static final String name = "__array_index";

    @CafeEnter
    public static IValue match(NTpl args) throws EvaluationException
    {
      try {
        IArray ar1 = (IArray) args.getCell(0);
        int ix = Factory.intValue(args.getCell(1));
        return NTuple.tuple(ar1.getCell(ix));
      } catch (IndexOutOfBoundsException e) {
        return null;
      }
    }

    @Override
    public IValue match(IValue arg) throws EvaluationException
    {
      return match((NTpl) arg);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // for all e such that e <= (array of e,integer_)
      TypeVar e = new TypeVar();

      IType funType = TypeUtils.patternType(TypeUtils.tupleType(e), TypeUtils.tupleType(TypeUtils.arrayType(e),
          StandardTypes.integerType));
      return new UniversalType(e, funType);
    }
  }
}
