package org.star_lang.star.operators.ast.runtime;

import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;

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
public class AstLocation implements IFunction
{
  public static final String name = "astLocation";

  @CafeEnter
  public static IValue enter(ASyntax term)
  {
    return term.getLoc();
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((ASyntax) args[0]);
  }

  @Override
  public IType getType()
  {
    return funType();
  }

  public static IType funType()
  {
    return TypeUtils.functionType(ASyntax.type, Location.type);
  }
}
