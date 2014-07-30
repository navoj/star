package org.star_lang.star.operators.skew.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.Option;
import org.star_lang.star.data.value.SkewList;
import org.star_lang.star.data.value.NTuple.NTpl;
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
public class SkewSliceOps
{
  public static class SkewElement implements IFunction
  {
    public static final String name = "__skew_element";

    @CafeEnter
    public static IValue enter(IList ar1, int ix) throws EvaluationException
    {
      try {
        return Option.some(ar1.getCell(ix));
      } catch (IndexOutOfBoundsException e) {
        return Option.noneEnum;
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IList) args[0], Factory.intValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(skew of %e,integer_)=>%e
      TypeVar tv = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.skewType(tv), StandardTypes.rawIntegerType, TypeUtils
          .optionType(tv));
      return new UniversalType(tv, funType);
    }
  }

  public static class SkewSlice implements IFunction
  {
    public static final String name = "__skew_slice";

    @CafeEnter
    public static SkewList enter(SkewList skew, int from, int to) throws EvaluationException
    {
      return skew.slice(from, to);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0], Factory.intValue(args[1]), Factory.intValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType listType = TypeUtils.skewType(tv);
      return new UniversalType(tv, TypeUtils.functionType(listType, StandardTypes.rawIntegerType,
          StandardTypes.rawIntegerType, listType));
    }
  }

  public static class SkewSplice implements IFunction
  {
    public static final String name = "__skew_splice";

    @CafeEnter
    public static IList enter(SkewList skew, int from, int to, SkewList sub) throws EvaluationException
    {
      return skew.splice(sub, from, to);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0], Factory.intValue(args[1]), Factory.intValue(args[2]), (SkewList) args[3]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType skewType = TypeUtils.skewType(tv);
      return new UniversalType(tv, TypeUtils.functionType(skewType, StandardTypes.rawIntegerType,
          StandardTypes.rawIntegerType, skewType, skewType));
    }
  }

  public static class SkewDeleteElement implements IFunction
  {
    public static final String name = "__skew_delete_element";

    @CafeEnter
    public static IValue enter(SkewList ar1, int ix) throws EvaluationException
    {
      return ar1.remove(ix, ix + 1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0], Factory.intValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(skew of %e,integer_)=> skew of %e
      TypeVar tv = new TypeVar();
      IType skewType = TypeUtils.skewType(tv);

      return new UniversalType(tv, TypeUtils.functionType(skewType, StandardTypes.rawIntegerType, skewType));
    }
  }

  public static class SkewIndexElement implements IPattern
  {
    public static final String name = "__skew_index";

    @CafeEnter
    public static IValue match(NTpl args) throws EvaluationException
    {
      try {
        IList ar1 = (IList) args.getCell(0);
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
      // for all e such that e <= (skew of e,integer_)
      TypeVar tv = new TypeVar();

      return new UniversalType(tv, TypeUtils.patternType(TypeUtils.tupleType(tv), TypeUtils.tupleType(TypeUtils
          .skewType(tv), StandardTypes.integerType)));
    }
  }
}
