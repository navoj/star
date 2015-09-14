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
