package org.star_lang.star.operators.skew.runtime;

import java.util.ArrayList;
import java.util.List;

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
import org.star_lang.star.data.value.SkewList;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.relation.runtime.ValueSort;

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
public class SkewUpdate
{

  public static class SkewSetElement implements IFunction
  {
    public static final String name = "__skew_set_element";

    @CafeEnter
    public static IList enter(IList skew, int index, IValue el) throws EvaluationException
    {
      return skew.substituteCell(index, el);
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
      TypeVar tv = new TypeVar();
      IType skewType = TypeUtils.skewType(tv);
      return new UniversalType(tv, TypeUtils.functionType(skewType, StandardTypes.rawIntegerType, tv, skewType));
    }
  }

  public static class UpdateIntoSkew implements IFunction
  {
    public static final String name = "__update_into_skew";

    @CafeEnter
    public static IList enter(SkewList skew, final IPattern filter, final IFunction transform)
        throws EvaluationException
    {
      IFunction handle = new IFunction() {

        @Override
        public IValue enter(IValue... args) throws EvaluationException
        {
          IValue el = args[0];
          if (filter.match(el) != null)
            return transform.enter(el);
          else
            return el;
        }

        @Override
        public IType getType()
        {
          throw new UnsupportedOperationException();
        }

      };
      return skew.mapOver(handle);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0], (IPattern) args[1], (IFunction) args[2]);
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
      IType ptnType = TypeUtils.patternType(TypeUtils.tupleType(), tv);
      IType funType = TypeUtils.functionType(tv, tv);
      return new UniversalType(tv, TypeUtils.functionType(skewType, ptnType, funType, skewType));
    }
  }

  public static class SkewDelete implements IFunction
  {
    public static final String name = "__delete_from_skew";

    @CafeEnter
    public static SkewList enter(SkewList skew, final IPattern filter) throws EvaluationException
    {
      IFunction deleteFun = new IFunction() {

        @Override
        public IType getType()
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public IValue enter(IValue... args) throws EvaluationException
        {
          IValue el = args[0];
          SkewList soFar = (SkewList) args[1];
          if (filter.match(el) != null)
            return soFar;
          else
            return soFar.consCell(el);
        }
      };

      return (SkewList) skew.rightFold(deleteFun, SkewList.empty());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0], (IPattern) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType aryType = TypeUtils.skewType(tv);
      return new UniversalType(tv, TypeUtils.functionType(aryType, TypeUtils.patternType(TypeUtils.tupleType(), tv),
          aryType));
    }
  }

  public static class SkewSort implements IFunction
  {
    public static final String name = "__skew_sort";

    @CafeEnter
    public static SkewList enter(SkewList src, IFunction comparator) throws EvaluationException
    {
      List<IValue> tmp = new ArrayList<IValue>(src.size());
      for (IValue el : src)
        tmp.add(el);

      List<IValue> sorted = ValueSort.quickSort(tmp, comparator);
      return SkewList.newSkewList(sorted);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType comparatorType = TypeUtils.functionType(tv, tv, StandardTypes.booleanType);
      IType colType = TypeUtils.skewType(tv);
      return new UniversalType(tv, TypeUtils.functionType(colType, comparatorType, colType));
    }
  }
}
