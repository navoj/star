package org.star_lang.star.operators.arrays.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IArray;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IPattern;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;
import com.starview.platform.data.value.Array;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.NTuple;
import com.starview.platform.data.value.NTuple.NTpl;
import com.starview.platform.data.value.Option;

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
      // %e~(array of %e,_integer)=> %e
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
      // %e~(array of %e,_integer)=>option of %e
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
      // %e~(array of %e,_integer)=> array of %e
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
      // for all e such that e <= (array of e,_integer)
      TypeVar e = new TypeVar();

      IType funType = TypeUtils.patternType(TypeUtils.tupleType(e), TypeUtils.tupleType(TypeUtils.arrayType(e),
          StandardTypes.integerType));
      return new UniversalType(e, funType);
    }
  }
}
