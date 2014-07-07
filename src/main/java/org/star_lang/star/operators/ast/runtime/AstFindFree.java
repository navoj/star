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
public class AstFindFree implements IFunction
{
  public static final String name = "__find_free";

  @CafeEnter
  public static IAbstract enter(IAbstract exp, IAbstract non, IAbstract cons, IAbstract nil)
  {
    Set<Name> excludes = search(new HashSet<Name>(), non);
    Set<Name> free = search(excludes, exp);
    Location loc = exp.getLoc();

    IAbstract reslt = nil;
    for (Name nm : free) {
      reslt = Abstract.binary(loc, cons, nm, reslt);
    }

    return reslt;
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
    return enter((IAbstract) args[0], (IAbstract) args[1], (IAbstract) args[2], (IAbstract) args[3]);
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
