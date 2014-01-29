package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;

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
public class DisplayLocation implements IFunction
{
  public static final String name = "__display_location";

  @CafeEnter
  public static String enter(Location loc)
  {
    return loc.toString();
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return Factory.newString(enter((Location) args[0]));
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.functionType(Location.type, StandardTypes.rawStringType);
  }

}
