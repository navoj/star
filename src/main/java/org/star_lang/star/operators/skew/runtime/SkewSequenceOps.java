package org.star_lang.star.operators.skew.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.SkewList;
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
public class SkewSequenceOps
{
  public static class SkewNil implements IFunction
  {
    public static final String name = "__skew_nil";

    @CafeEnter
    public static IValue enter() throws EvaluationException
    {
      return SkewList.empty();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter();
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.skewType(tv)));
    }
  }

  public static class SkewCons implements IFunction
  {
    public static final String name = "__skew_cons";

    @CafeEnter
    public static IList enter(IValue el, IList skew) throws EvaluationException
    {
      return skew.consCell(el);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(args[0], (IList) args[1]);
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
      return new UniversalType(tv, TypeUtils.functionType(tv, skewType, skewType));
    }
  }

  public static class SkewAppend implements IFunction
  {
    public static final String name = "__skew_append";

    @CafeEnter
    public static SkewList enter(SkewList skew, IValue el) throws EvaluationException
    {
      return skew.addCell(el);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0], args[1]);
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
      return new UniversalType(tv, TypeUtils.functionType(skewType, tv, TypeUtils.skewType(tv)));
    }
  }

  public static class SkewEmptyMatch implements IPattern
  {
    public static final String name = "__skew_empty_match";

    @CafeEnter
    public static IValue matches(IValue list) throws EvaluationException
    {
      IList skew = (IList) list;
      if (skew.isEmpty()) {
        return NTuple.$0Enum;
      } else
        return null;
    }

    @Override
    public IValue match(IValue list) throws EvaluationException
    {
      return matches(list);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.patternType(TypeUtils.tupleType(), TypeUtils.skewType(tv)));
    }
  }

  public static class SkewHeadMatch implements IPattern
  {
    public static final String name = "__skew_head_match";

    @CafeEnter
    public static IValue matches(SkewList skew) throws EvaluationException
    {
      if (skew.size() >= 1)
        return NTuple.tuple(skew.getCell(0), skew.tail());
      else
        return null;
    }

    @Override
    public IValue match(IValue list) throws EvaluationException
    {
      return matches((SkewList) list);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType l = TypeUtils.skewType(tv);

      return new UniversalType(tv, TypeUtils.patternType(TypeUtils.tupleType(tv, l), l));
    }
  }

  public static class SkewTailMatch implements IPattern
  {
    public static final String name = "__skew_tail_match";

    @CafeEnter
    public static IValue matches(IValue list) throws EvaluationException
    {
      SkewList skew = (SkewList) list;

      int size = skew.size();

      if (size >= 1)
        return NTuple.tuple(skew.slice(0, size - 1), skew.getCell(size - 1));
      else
        return null;
    }

    @Override
    public IValue match(IValue list) throws EvaluationException
    {
      return matches(list);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType l = TypeUtils.skewType(tv);

      return new UniversalType(tv, TypeUtils.patternType(TypeUtils.tupleType(l, tv), l));
    }
  }
}
