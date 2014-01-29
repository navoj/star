package org.star_lang.star.operators.system.runtime;

import java.util.Calendar;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.LongWrap;

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
public class Clock
{
  public static class Nanos implements IFunction
  {
    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.longType);
    }

    @CafeEnter
    public static LongWrap enter()
    {
      return Factory.newLng(System.nanoTime());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter();
    }
  }

  public static class Now implements IFunction
  {
    @Override
    public IType getType()
    {
      return funType();
    }

    public static IType funType()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType);
    }

    @CafeEnter
    public static long enter()
    {
      return System.currentTimeMillis();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLong(enter());
    }
  }

  public static class Today implements IFunction
  {
    @Override
    public IType getType()
    {
      return funType();
    }

    public static IType funType()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType);
    }

    @CafeEnter
    public static long enter()
    {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      return calendar.getTimeInMillis();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLong(enter());
    }
  }
}
