package com.starview.platform.data.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeVarFinder;
import org.star_lang.star.compiler.util.HistoricalMap;

/**
 * A UniversalType denote a universally quantified type expression -- the type expression denotes a
 * type for every possible substitution of the bound type variable (provided that the bound type
 * implements the contract requirements)
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

@SuppressWarnings("serial")
public class UniversalType extends QuantifiedType
{

  /**
   * A UniversalType has a bound variable and a bound type.
   * 
   * NOTE: the order of the bound variables is important: variables in the list should appear in
   * 'order of appearance' within the bound type.
   * 
   * @param boundVars
   *          the bound variables of the universal type
   * @param boundType
   *          the bound type.
   */

  public UniversalType(TypeVar boundVar, IType boundType)
  {
    super(StandardNames.FOR_ALL, boundVar, boundType);
  }

  @Override
  public <C> void accept(ITypeVisitor<C> visitor, C cxt)
  {
    visitor.visitUniversalType(this, cxt);
  }

  @Override
  public <T, C, X> T transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformUniversalType(this, cxt);
  }

  public static IType univ(Collection<TypeVar> vars, IType bound)
  {
    if (!vars.isEmpty()) {
      HistoricalMap<String, TypeVar> tVars = TypeVarFinder.findTypeVars(bound);
      List<TypeVar> boundVars = new ArrayList<TypeVar>();
      for (Entry<String, TypeVar> entry : tVars.entrySet()) {
        if (vars.contains(entry.getValue()))
          boundVars.add(0, entry.getValue());
      }
      for (TypeVar v : boundVars)
        bound = new UniversalType(v, bound);
    }
    return bound;
  }

  public static IType universal(TypeVar[] vars, IType bound)
  {
    for (TypeVar v : vars)
      bound = new UniversalType(v, bound);
    return bound;
  }

  public static IType universal(Collection<TypeVar> vars, IType bound)
  {
    for (TypeVar v : vars)
      bound = new UniversalType(v, bound);
    return bound;
  }
}
