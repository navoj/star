package org.star_lang.star.operators.relation.runtime;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.Relation;
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
public class RelationSetOps
{
  public static class RelationUnion implements IFunction
  {
    public static final String name = "__relation_union";

    @CafeEnter
    public static Relation enter(Relation left, Relation right, IFunction equals) throws EvaluationException
    {
      List<IValue> tmp = new ArrayList<IValue>(left.size());
      for (IValue el : left)
        tmp.add(el);

      outer: for (IValue el : right) {
        for (IValue lftEl : tmp)
          if (Factory.boolValue(equals.enter(el, lftEl)))
            continue outer;
        tmp.add(el);
      }

      return Relation.create(tmp);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Relation) args[0], (Relation) args[1], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType eqType = TypeUtils.functionType(tv, tv, StandardTypes.booleanType);
      IType colType = TypeUtils.relationType(tv);
      return new UniversalType(tv, TypeUtils.functionType(colType, colType, eqType, colType));
    }
  }

  public static class RelationIntersect implements IFunction
  {
    public static final String name = "__relation_intersect";

    @CafeEnter
    public static Relation enter(Relation left, Relation right, IFunction equals) throws EvaluationException
    {
      List<IValue> tmp = new ArrayList<IValue>(Math.min(left.size(), right.size()));

      outer: for (IValue lEl : left) {
        for (IValue rEl : right) {
          if (Factory.boolValue(equals.enter(lEl, rEl))) {
            tmp.add(lEl);
            continue outer;
          }
        }
      }

      return Relation.create(tmp);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Relation) args[0], (Relation) args[1], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType eqType = TypeUtils.functionType(tv, tv, StandardTypes.booleanType);
      IType colType = TypeUtils.relationType(tv);
      return new UniversalType(tv, TypeUtils.functionType(colType, colType, eqType, colType));
    }
  }

  public static class RelationComplement implements IFunction
  {
    public static final String name = "__relation_complement";

    @CafeEnter
    public static Relation enter(Relation left, Relation right, IFunction equals) throws EvaluationException
    {
      List<IValue> tmp = new ArrayList<IValue>(left.size());

      outer: for (IValue lEl : left) {
        for (IValue rEl : right) {
          if (Factory.boolValue(equals.enter(lEl, rEl)))
            continue outer;
        }
        tmp.add(lEl);
      }

      return Relation.create(tmp);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Relation) args[0], (Relation) args[1], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType eqType = TypeUtils.functionType(tv, tv, StandardTypes.booleanType);
      IType colType = TypeUtils.relationType(tv);
      return new UniversalType(tv, TypeUtils.functionType(colType, colType, eqType, colType));
    }
  }
}
