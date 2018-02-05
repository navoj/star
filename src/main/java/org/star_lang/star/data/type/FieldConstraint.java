package org.star_lang.star.data.type;

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

import java.util.Collection;

import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

@SuppressWarnings("serial")
public class FieldConstraint implements ITypeConstraint, PrettyPrintable
{
  private final TypeVar var;
  private final String field;
  private final IType type;

  public FieldConstraint(TypeVar var, String field, IType type)
  {
    this.var = var;
    this.field = field;
    this.type = type;
    assert var != null && field != null && type != null;
  }

  @Override
  public <X> void accept(ITypeVisitor<X> visitor, X cxt)
  {
    type.accept(visitor, cxt);
  }

  @Override
  public <T, C, X> C transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformFieldConstraint(this, cxt);
  }

  public TypeVar getVar()
  {
    return var;
  }

  @Override
  public Collection<TypeVar> affectedVars()
  {
    return FixedList.create(var);
  }

  public String getField()
  {
    return field;
  }

  public IType getType()
  {
    return type;
  }

  @Override
  public void checkBinding(IType candidate, Location loc, Dictionary cxt) throws TypeConstraintException
  {
    IType memType = TypeUtils.getAttributeType(cxt, candidate, field, false);

    if (memType == null)
      throw new TypeConstraintException(FixedList.create(candidate.toString(), " does not have field ", field), loc);
    Subsume.same(type, memType, loc, cxt);
  }

  @Override
  public boolean sameConstraint(ITypeConstraint con, Location loc, Dictionary face) throws TypeConstraintException
  {
    if (con instanceof FieldConstraint) {
      FieldConstraint other = (FieldConstraint) con;
      if (other.getField().equals(field)) {
        try {
          Subsume.same(type, other.getType(), loc, face);
          return true;
        } catch (TypeConstraintException e) {
          return false;
        }
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof FieldConstraint) {
      FieldConstraint record = (FieldConstraint) obj;
      return record.field.equals(field) && record.type.equals(type);
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return field.hashCode() * 37 + type.hashCode();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    showConstraint(new DisplayType(disp));
  }

  @Override
  public void showConstraint(DisplayType disp)
  {
    PrettyPrintDisplay pp = disp.getDisp();
    disp.show(var, Operators.IMPLEMENTS_PRIORITY - 1);
    pp.appendWord(StandardNames.IMPLEMENTS);
    pp.append("{");
    pp.appendId(field);
    pp.appendWord(StandardNames.HAS_TYPE);
    disp.show(type, Operators.HAS_TYPE_PRIORITY - 1);
    pp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}