package org.star_lang.star.data.type;

import java.util.Collection;

import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/*
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
public class InstanceOf implements ITypeConstraint, PrettyPrintable
{
  private final TypeVar var;
  private final IType type;

  public InstanceOf(TypeVar var, IType type)
  {
    this.var = var;
    this.type = type;
    assert type != null;
  }

  public TypeVar getVar()
  {
    return var;
  }

  public IType getType()
  {
    return type;
  }

  @Override
  public Collection<TypeVar> affectedVars()
  {
    return FixedList.create(var);
  }

  @Override
  public void checkBinding(IType candidate, Location loc, Dictionary cxt) throws TypeConstraintException
  {
    Subsume.subsume(candidate, type, loc, cxt);
  }

  @Override
  public boolean sameConstraint(ITypeConstraint other, Location loc, Dictionary dict) throws TypeConstraintException
  {
    if (other instanceof InstanceOf) {
      InstanceOf o = (InstanceOf) other;
      if (o == this)
        return true;
      else if (o.var.equals(var)) {
        try {
          Subsume.same(type, o.type, loc, dict);
          return true;
        } catch (TypeConstraintException e) {
          return false;
        }
      }
    }
    return false;
  }

  @Override
  public <X> void accept(ITypeVisitor<X> visitor, X cxt)
  {
    type.accept(visitor, cxt);
  }

  @Override
  public <T, C, X> C transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformInstanceOf(this, cxt);
  }

  @Override
  public int hashCode()
  {
    return type.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof InstanceOf && ((InstanceOf) obj).type.equals(type);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    showConstraint(new DisplayType(disp));
  }

  @Override
  public void showConstraint(DisplayType disp)
  {
    getVar().accept(disp, null);
    disp.getDisp().appendWord(StandardNames.INSTANCE_OF);
    disp.show(getType(), Operators.INSTANCE_OF_PRIORITY - 1);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
