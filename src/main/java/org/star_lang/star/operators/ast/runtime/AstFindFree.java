package org.star_lang.star.operators.ast.runtime;

import java.util.HashSet;
import java.util.Set;

import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAbstractVisitor;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.CafeEnter;

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
public class AstFindFree implements IFunction {
  public static final String name = "__find_free";

  @CafeEnter
  public static IAbstract enter(IAbstract exp, IAbstract non, IAbstract cons, IAbstract nil) {
    Set<Name> excludes = search(new HashSet<>(), non);
    Set<Name> free = search(excludes, exp);
    Location loc = exp.getLoc();

    IAbstract reslt = nil;
    for (Name nm : free) {
      reslt = Abstract.binary(loc, cons, nm, reslt);
    }

    return reslt;
  }

  private static Set<Name> search(Set<Name> excludes, IAbstract term) {
    final Set<Name> found = new HashSet<>();

    IAbstractVisitor finder = new AstFreeFinder(found, excludes);

    term.accept(finder);
    return found;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException {
    return enter((IAbstract) args[0], (IAbstract) args[1], (IAbstract) args[2], (IAbstract) args[3]);
  }

  @Override
  public IType getType() {
    return type();
  }

  public static IType type() {
    return TypeUtils.functionType(ASyntax.type, ASyntax.type, ASyntax.type, ASyntax.type, ASyntax.type);
  }

}
