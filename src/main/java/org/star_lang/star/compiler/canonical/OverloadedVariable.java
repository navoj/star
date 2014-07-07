package org.star_lang.star.compiler.canonical;

import static org.star_lang.star.data.type.Location.merge;

import java.util.Map;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Quantifier;
import org.star_lang.star.data.type.TypeConstraintException;

/**
 * Overloaded Variables have a type that includes contract dependencies. They must be handled
 * carefully; or they will blow up (fail to unify)
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
public class OverloadedVariable extends Variable
{
  private final IType dictType;

  public OverloadedVariable(Location loc, IType type, IType dictType, String name)
  {
    super(loc, type, name);
    this.dictType = dictType;
  }

  public IType getDictType()
  {
    return dictType;
  }

  @Override
  public Variable copy()
  {
    return new OverloadedVariable(getLoc(), getType(), dictType, GenSym.genSym(getName()));
  }

  @Override
  public Variable underLoad()
  {
    return new Variable(getLoc(), getType(), getName());
  }

  @Override
  public boolean isRealVariable()
  {
    return false;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitOverloadedVariable(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformOverloadVariable(this, context);
  }

  @Override
  public Variable verifyType(Location loc, ErrorReport errors, IType expectedType, Dictionary dict, boolean checkForRaw)
  {
    Pair<IType, Map<String, Quantifier>> f = TypeUtils.overloadedRefresh(dictType);

    try {
      Subsume.subsume(expectedType, TypeUtils.getOverloadedType(f.left), loc, dict);
    } catch (TypeConstraintException e) {
      errors.reportError(StringUtils.msg(getName(), " has type ", getType(), "\nwhich is not consistent with ",
          expectedType, "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
    }
    return new OverloadedVariable(loc, expectedType, f.left, getName());
  }
}
