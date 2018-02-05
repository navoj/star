package org.star_lang.star.data.type;

import java.util.Collection;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * A tuple constraint is imposed on a type variable if the variable must be bound to a tuple of some
 * arity
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
public class TupleConstraint implements ITypeConstraint, PrettyPrintable
{
  private final TypeVar var;

  public TupleConstraint(TypeVar con)
  {
    this.var = con;
  }

  @Override
  public Collection<TypeVar> affectedVars()
  {
    return FixedList.create(var);
  }

  @Override
  public void checkBinding(IType candidate, Location loc, Dictionary dict) throws TypeConstraintException
  {
    if (!TypeUtils.isTupleType(candidate))
      throw new TypeConstraintException("must be a tuple type", loc);
  }

  @Override
  public boolean sameConstraint(ITypeConstraint other, Location loc, Dictionary dict) throws TypeConstraintException
  {
    return other instanceof TupleConstraint;
  }

  @Override
  public <X> void accept(ITypeVisitor<X> visitor, X cxt)
  {
    var.accept(visitor, cxt);
  }

  @Override
  public <T, C, X> C transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformTupleContraint(this, cxt);
  }

  public IType getVar()
  {
    return var;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    showConstraint(new DisplayType(disp));
  }

  @Override
  public void showConstraint(DisplayType disp)
  {
    var.accept(disp, null);
    disp.getDisp().appendWord(StandardNames.IS_TUPLE);
  }
}
