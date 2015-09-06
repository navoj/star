package org.star_lang.star.operators.arrays.runtime;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Array;
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
public class ArrayUpdate
{

  public static class ArraySetElement implements IFunction
  {
    public static final String name = "__array_set_element";

    @CafeEnter
    public static IList enter(IList array, int index, IValue el) throws EvaluationException
    {
      return array.substituteCell(index, el);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IList) args[0], Factory.intValue(args[1]), args[2]);
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
      return new UniversalType(v, TypeUtils.functionType(arrayType, StandardTypes.rawIntegerType, v, arrayType));
    }
  }

  public static class UpdateIntoArray implements IFunction
  {
    public static final String name = "__update_into_array";

    @CafeEnter
    public static IArray enter(Array array, IPattern filter, IFunction transform) throws EvaluationException
    {
      return array.updateUsingPattern(filter, transform);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Array) args[0], (IPattern) args[1], (IFunction) args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType arrayType = TypeUtils.arrayType(tv);
      IType ptnType = TypeUtils.patternType(TypeUtils.tupleType(), tv);
      IType funType = TypeUtils.functionType(tv, tv);
      return new UniversalType(tv, TypeUtils.functionType(arrayType, ptnType, funType, arrayType));
    }
  }

  public static class ArrayDelete implements IFunction
  {
    public static final String name = "__delete_from_array";

    @CafeEnter
    public static IArray enter(Array array, IPattern filter) throws EvaluationException
    {
      return array.deleteUsingPattern(filter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Array) args[0], (IPattern) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType aryType = TypeUtils.arrayType(tv);
      return new UniversalType(tv, TypeUtils.functionType(aryType, TypeUtils.patternType(TypeUtils.tupleType(), tv),
          aryType));
    }
  }

  public static class ArraySort implements IFunction
  {
    public static final String name = "__array_sort";

    @CafeEnter
    public static Array enter(Array src, IFunction comparator) throws EvaluationException
    {
      List<IValue> tmp = new ArrayList<>(src.size());
      for (IValue el : src)
        tmp.add(el);

      List<IValue> sorted = ValueSort.quickSort(tmp, comparator);
      return Array.newArray(sorted);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Array) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType comparatorType = TypeUtils.functionType(v, v, StandardTypes.booleanType);
      IType colType = TypeUtils.arrayType(v);
      return new UniversalType(v, TypeUtils.functionType(colType, comparatorType, colType));
    }
  }
}
