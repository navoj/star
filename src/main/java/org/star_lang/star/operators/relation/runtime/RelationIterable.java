package org.star_lang.star.operators.relation.runtime;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IConstructor;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IRelation;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;

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
public class RelationIterable
{
  public static class RelationIterate implements IFunction
  {
    public static final String name = "__relation_iterate";

    @CafeEnter
    public static IValue enter(IValue data, IFunction iter, IValue state) throws EvaluationException
    {
      IRelation relation = (IRelation) data;

      if (!(state instanceof IConstructor && ((IConstructor) state).getLabel().equals(StandardNames.NOMORE)))
        for (IValue el : relation) {
          state = iter.enter(el, state);
          if (state instanceof IConstructor && ((IConstructor) state).getLabel().equals(StandardNames.NOMORE))
            break;
        }
      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return RelationIterate.type();
    }

    public static IType type()
    {
      // %e~%s~(relation of %e,(%e,%s)=>%s,%s) => %s

      TypeVar e = new TypeVar();
      TypeVar s = new TypeVar();

      IType relType = TypeUtils.typeExp(StandardTypes.RELATION, e);

      IType funType = TypeUtils.functionType(relType, TypeUtils.functionType(e, s, s), s, s);
      return new UniversalType(e, new UniversalType(s, funType));
    }
  }
}
