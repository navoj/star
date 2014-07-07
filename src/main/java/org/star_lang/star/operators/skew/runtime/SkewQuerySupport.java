package org.star_lang.star.operators.skew.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
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
public class SkewQuerySupport
{
  public static class SkewProject0 implements IFunction
  {
    public static final String name = "__skew_project_0";

    @CafeEnter
    public static SkewList enter(SkewList skew) throws EvaluationException
    {
      IFunction proj = new IFunction() {

        @Override
        public IType getType()
        {
          return null;
        }

        @Override
        public IValue enter(IValue... args) throws EvaluationException
        {
          IValue el = args[0];
          if (!(el instanceof IConstructor))
            throw new EvaluationException("illegal entry");
          else
            return ((IConstructor) el).getCell(0);
        }
      };

      return skew.mapOver(proj);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v1 = new TypeVar();
      TypeVar v2 = new TypeVar();
      return new UniversalType(v1, new UniversalType(v2, TypeUtils.functionType(TypeUtils.skewType(TypeUtils.tupleType(
          v1, v2)), TypeUtils.skewType(v1))));
    }
  }

  public static class SkewUnique implements IFunction
  {
    public static final String name = "__skew_unique";

    @CafeEnter
    public static SkewList enter(SkewList data, IFunction equalizer) throws EvaluationException
    {
      HashSet<Integer> seenHashes = new HashSet<Integer>();
      List<IValue> reslt = new ArrayList<IValue>(data.size());

      // only assume that two things that hash to different things are different
      for (IValue el : data) {
        if (seenHashes.contains(el.hashCode())) {
          boolean found = false;
          for (IValue el2 : reslt) {
            if (equalizer.enter(el, el2).equals(Factory.trueValue)) {
              found = true;
              break;
            }
          }
          if (!found) {
            reslt.add(el);
          }
        } else {
          reslt.add(el);
          seenHashes.add(el.hashCode());
        }
      }
      return SkewList.newSkewList(reslt);
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
      IType equalityType = TypeUtils.functionType(tv, tv, StandardTypes.booleanType);
      IType listType = TypeUtils.skewType(tv);
      return new UniversalType(tv, TypeUtils.functionType(listType, equalityType, listType));
    }
  }
}
