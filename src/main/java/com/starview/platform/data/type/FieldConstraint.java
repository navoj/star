package com.starview.platform.data.type;

/**
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

import java.util.Collection;

import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.standard.StandardNames;
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