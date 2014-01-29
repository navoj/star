package org.star_lang.star.operators.relation.runtime;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IRelation;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;
import com.starview.platform.data.value.Relation;

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
public class SortRelation implements IFunction
{
  public static final String name = "__relation_sort";

  @CafeEnter
  public static IRelation enter(IRelation src, IFunction comparator) throws EvaluationException
  {
    List<IValue> tmp = new ArrayList<IValue>(src.size());
    for (IValue el : src)
      tmp.add(el);

    List<IValue> sorted = ValueSort.quickSort(tmp, comparator);
    return Relation.create(sorted);
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((IRelation) args[0], (IFunction) args[1]);
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
    IType relType = TypeUtils.relationType(tv);
    return new UniversalType(tv, TypeUtils.functionType(relType, comparatorType, relType));
  }
}
