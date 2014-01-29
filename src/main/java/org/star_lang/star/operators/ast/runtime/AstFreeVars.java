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
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IArray;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.value.Array;

/**
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
public class AstFreeVars implements IFunction
{
  public static final String name = "__quoted_free";

  @CafeEnter
  public static IArray enter(IAbstract exp, IArray non, IArray init)
  {
    Set<Name> excludes = hashSetOfNames(non);

    List<IValue> freeList = new ArrayList<>();
    for (Name v : search(excludes, exp))
      freeList.add(v);

    return new Array(freeList);
  }

  public static Set<Name> hashSetOfNames(IArray list)
  {
    Set<Name> set = new HashSet<>();
    for (IValue el : list) {
      if (el instanceof Name)
        set.add((Name) el);
    }
    return set;
  }

  private static Set<Name> search(Set<Name> excludes, IAbstract term)
  {
    final Set<Name> found = new HashSet<>();

    IAbstractVisitor finder = new AstFreeFinder(found, excludes);

    term.accept(finder);
    return found;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((IAbstract) args[0], (IArray) args[1], (IArray) args[2]);
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.functionType(ASyntax.type, ASyntax.type, ASyntax.type, ASyntax.type, ASyntax.type);
  }

}
