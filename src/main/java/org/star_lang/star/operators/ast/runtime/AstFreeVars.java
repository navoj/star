package org.star_lang.star.operators.ast.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAbstractVisitor;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.value.Array;
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
public class AstFreeVars implements IFunction {
  public static final String name = "__quoted_free";

  @CafeEnter
  public static IArray enter(IAbstract exp, IArray non, IArray init) {
    Set<Name> excludes = hashSetOfNames(non);

    List<IValue> freeList = new ArrayList<>();
    for (IValue el : init)
      freeList.add(el);
    for (Name v : search(excludes, exp))
      freeList.add(v);

    return new Array(freeList);
  }

  public static Set<Name> hashSetOfNames(IArray list) {
    Set<Name> set = new HashSet<>();
    for (IValue el : list) {
      if (el instanceof Name)
        set.add((Name) el);
    }
    return set;
  }

  private static Set<Name> search(Set<Name> excludes, IAbstract term) {
    final Set<Name> found = new HashSet<>();

    IAbstractVisitor finder = new AstFreeFinder(found, excludes);

    term.accept(finder);
    return found;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException {
    return enter((IAbstract) args[0], (IArray) args[1], (IArray) args[2]);
  }

  @Override
  public IType getType() {
    return type();
  }

  public static IType type() {
    return TypeUtils.functionType(ASyntax.type, ASyntax.type, ASyntax.type, ASyntax.type, ASyntax.type);
  }

}
