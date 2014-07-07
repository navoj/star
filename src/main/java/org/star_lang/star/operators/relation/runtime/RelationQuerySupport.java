package org.star_lang.star.operators.relation.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IRelation;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
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
public class RelationQuerySupport
{

  public static class RelationUnique implements IFunction
  {
    public static final String name = "__relation_unique";

    @CafeEnter
    public static IRelation enter(IRelation data, IFunction equalizer) throws EvaluationException
    {
      HashSet<Integer> seenHashes = new HashSet<Integer>();
      ArrayList<IValue> reslt = new ArrayList<IValue>(data.size());

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
      return Factory.newRelation(elementType(reslt), reslt);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], (IFunction) args[1]);
    }

    private static IType elementType(Iterable<IValue> data)
    {
      for (IValue el : data)
        return el.getType();
      return new TypeVar();
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
      IType collType = TypeUtils.relationType(tv);
      return new UniversalType(tv, TypeUtils.functionType(collType, equalityType, collType));
    }
  }

  public static class RelationProject0 implements IFunction
  {
    public static final String name = "__relation_project_0";

    @CafeEnter
    public static IRelation enter(IRelation els) throws EvaluationException
    {
      List<IValue> projected = new ArrayList<IValue>();
      IType elType = new TypeVar();

      for (IValue el : els) {
        if (!(el instanceof IConstructor))
          throw new EvaluationException("illegal entry");
        else {
          IValue projEl = ((IConstructor) el).getCell(0);
          projected.add(projEl);
          elType = projEl.getType();
        }
      }

      return Factory.newRelation(elType, projected);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      TypeVar v2 = new TypeVar();
      return new UniversalType(tv, new UniversalType(v2, TypeUtils.functionType(TypeUtils.relationType(TypeUtils
          .tupleType(tv, v2)), TypeUtils.relationType(tv))));
    }
  }
}
