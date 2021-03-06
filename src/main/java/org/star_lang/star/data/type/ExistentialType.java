package org.star_lang.star.data.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.TypeVarFinder;
import org.star_lang.star.compiler.util.HistoricalMap;

/**
 * A UniversalType denote a universally quantified type expression -- the type expression denotes a
 * type for every possible substitution of the bound type variable (provided that the bound type
 * implements the contract requirements)
 *
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
