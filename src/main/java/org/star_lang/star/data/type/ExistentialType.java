package org.star_lang.star.data.type;

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
public class ExistentialType extends QuantifiedType
{
  /**
   * An Existential has a bound variable and a bound type.
   * 
   * NOTE: the order of the bound variables is important: variables in the list should appear in
   * 'order of appearance' within the bound type.
   * 
   * @param boundVar
   *          the bound variable of the universal type
   * @param boundType
   *          the bound type.
   */

  public ExistentialType(TypeVar boundVar, IType boundType)
  {
    super(StandardNames.EXISTS, boundVar, boundType);
  }

  @Override
  public <X> void accept(ITypeVisitor<X> visitor, X cxt)
  {
    visitor.visitExistentialType(this, cxt);
  }

  @Override
  public <T, C, X> T transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformExistentialType(this, cxt);
  }

  public static IType exist(Collection<TypeVar> vars, IType bound)
  {
    if (!vars.isEmpty()) {
      HistoricalMap<String, TypeVar> tVars = TypeVarFinder.findTypeVars(bound);
      List<TypeVar> boundVars = new ArrayList<>();
      for (Entry<String, TypeVar> entry : tVars.entrySet()) {
        if (vars.contains(entry.getValue()))
          boundVars.add(0, entry.getValue());
      }
      for (TypeVar v : boundVars)
        bound = new ExistentialType(v, bound);
    }
    return bound;
  }
}
