package org.star_lang.star.operators.system.runtime;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;

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
public class SimpleLog implements IFunction
{
  private static final Logger logger = Logger.getAnonymousLogger();
  public static final String name = "__log";

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.procedureType(StandardTypes.stringType);
  }

  @CafeEnter
  public static IValue enter(IValue msg) throws EvaluationException
  {
    if (logger.isLoggable(Level.INFO))
      logger.logp(Level.INFO, "", "", msg.toString());
    return StandardTypes.unit;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter(args[0]);
  }
}
