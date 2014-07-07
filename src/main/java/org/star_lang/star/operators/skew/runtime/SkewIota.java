package org.star_lang.star.operators.skew.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.SkewList;
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
public class SkewIota
{
  public static class SkewIntegerIota implements IFunction
  {
    public static final String name = "__integer_skew_iota";

    @CafeEnter
    public static IList enter(int from, int to, int step) throws EvaluationException
    {
      int count = (to - from + step) / step;
      IList list = SkewList.empty();

      for (int ix = count - 1; ix >= 0; ix--)
        list = list.consCell(Factory.newInt(from + ix * step));

      return list;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]), Factory.intValue(args[1]), Factory.intValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType intType = StandardTypes.integerType;
      IType rawIntType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(rawIntType, rawIntType, rawIntType, TypeUtils.skewType(intType));
    }
  }

  public static class SkewLongIota implements IFunction
  {
    public static final String name = "__long_skew_iota";

    @CafeEnter
    public static IList enter(long from, long to, long step) throws EvaluationException
    {
      int count = (int) ((to - from + step) / step);

      IList list = SkewList.empty();

      for (int ix = count - 1; ix >= 0; ix--)
        list = list.consCell(Factory.newLng(from + ix * step));

      return list;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.lngValue(args[0]), Factory.lngValue(args[1]), Factory.lngValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType longType = StandardTypes.longType;
      IType lngType = StandardTypes.rawLongType;
      return TypeUtils.functionType(lngType, lngType, lngType, TypeUtils.skewType(longType));
    }
  }

  public static class SkewFloatIota implements IFunction
  {
    public static final String name = "__float_skew_iota";

    @CafeEnter
    public static IList enter(double from, double to, double step) throws EvaluationException
    {
      int count = (int) ((to - from + step) / step);

      IList list = SkewList.empty();

      for (int ix = count - 1; ix >= 0; ix--)
        list = list.consCell(Factory.newFlt(from + ix * step));

      return list;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.fltValue(args[0]), Factory.fltValue(args[1]), Factory.fltValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType floatType = StandardTypes.floatType;
      IType fltType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(fltType, fltType, fltType, TypeUtils.skewType(floatType));
    }
  }
}
