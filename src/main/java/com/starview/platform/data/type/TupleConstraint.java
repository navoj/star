package com.starview.platform.data.type;

import java.util.Collection;

import org.star_lang.star.compiler.standard.StandardNames;
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
