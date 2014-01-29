package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.StringWrap;

/**
 * Implement display of stuff
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

public class DisplayTerm implements IFunction
{
  public static final String name = "__display";

  @CafeEnter
  public static StringWrap enter(IValue value) throws EvaluationException
  {
    return Factory.newString(ValueDisplay.display(value));
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter(args[0]);
  }

  @Override
  public IType getType()
  {
    return funType();
  }

  public static IType funType()
  {
    TypeVar tv = new TypeVar();
    return new UniversalType(tv, TypeUtils.functionType(tv, StandardTypes.stringType));
  }
}
