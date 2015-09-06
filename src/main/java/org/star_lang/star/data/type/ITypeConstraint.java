package org.star_lang.star.data.type;

import java.io.Serializable;
import java.util.Collection;

import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;

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
public interface ITypeConstraint extends Serializable
{
  Collection<TypeVar> affectedVars();

  void checkBinding(IType candidate, Location loc, Dictionary cxt) throws TypeConstraintException;

  boolean sameConstraint(ITypeConstraint other, Location loc, Dictionary cxt) throws TypeConstraintException;

  <X> void accept(ITypeVisitor<X> visitor, X cxt);

  void showConstraint(DisplayType disp);

  <T, C, X> C transform(TypeTransformer<T, C, X> trans, X cxt);
}