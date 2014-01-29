package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;



/**
 * Binary arithmetic functions
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


public abstract class LongBinary
{

  public static class LongPlus implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 + s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongMinus implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 - s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongTimes implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 * s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongDivide implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 / s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongRemainder implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 % s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongMin implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return Math.min(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongMax implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return Math.max(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongPwr implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return (long) Math.pow(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongLeft implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return (long) s1 << s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongRight implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return (long) s1 >> s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }
}
