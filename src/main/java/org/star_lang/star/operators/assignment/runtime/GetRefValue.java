package org.star_lang.star.operators.assignment.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.assignment.runtime.RefCell.BoolCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.Cell;
import org.star_lang.star.operators.assignment.runtime.RefCell.CharCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.FloatCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.IntegerCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.LongCell;

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
public class GetRefValue
{
  public static class GetRef implements IFunction
  {
    public static final String name = "__dereference";

    @CafeEnter
    public static IValue enter(Cell cell)
    {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args)
    {
      return enter((Cell) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.referenceType(tv), tv));
    }
  }

  public static class GetRawBoolRef implements IFunction
  {
    public static final String name = "__deref_bool";

    @CafeEnter
    public static boolean enter(BoolCell cell)
    {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args)
    {
      return Factory.newBool(enter((BoolCell) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawBoolType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class GetRawCharRef implements IFunction
  {
    public static final String name = "__deref_char";

    @CafeEnter
    public static int enter(CharCell cell)
    {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args)
    {
      return Factory.newChar(enter((CharCell) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawCharType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class GetRawIntegerRef implements IFunction
  {
    public static final String name = "__deref_integer";

    @CafeEnter
    public static int enter(IntegerCell cell)
    {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args)
    {
      return Factory.newInt(enter((IntegerCell) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class GetRawLongRef implements IFunction
  {
    public static final String name = "__deref_long";

    @CafeEnter
    public static long enter(LongCell cell)
    {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args)
    {
      return Factory.newLng(enter((LongCell) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawLongType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class GetRawFloatRef implements IFunction
  {
    public static final String name = "__deref_float";

    @CafeEnter
    public static double enter(FloatCell cell)
    {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args)
    {
      return Factory.newFloat(enter((FloatCell) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawFloatType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }
}
