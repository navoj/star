package org.star_lang.star.operators.arrays.runtime;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.relation.runtime.ValueSort;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IArray;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IList;
import com.starview.platform.data.IPattern;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;
import com.starview.platform.data.value.Array;
import com.starview.platform.data.value.Factory;

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
      List<IValue> tmp = new ArrayList<IValue>(src.size());
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
