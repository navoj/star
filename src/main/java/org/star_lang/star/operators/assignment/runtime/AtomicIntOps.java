package org.star_lang.star.operators.assignment.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.assignment.runtime.AtomicInt.AtomIntCell;

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
public class AtomicIntOps
{
  private static final IType rawIntType = StandardTypes.rawIntegerType;

  public static class AtomIntReference implements IFunction
  {
    public static final String name = "__atomic_int_reference";

    @CafeEnter
    public static int enter(AtomIntCell cell)
    {
      return cell.get();
    }

    @Override
    public IValue enter(IValue... args)
    {
      return Factory.newInt(enter((AtomIntCell) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(AtomicInt.type, StandardTypes.rawIntegerType);
    }
  }

  public static class AtomIntAssignment implements IFunction
  {
    public static final String name = "__atomic_int_assign";

    @CafeEnter
    public static IValue enter(AtomIntCell cell, int value)
    {
      cell.set(value);
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((AtomIntCell) args[0], Factory.intValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.procedureType(AtomicInt.type, rawIntType);
    }
  }

  public static class AtomIntTestAndSet implements IFunction
  {
    public static final String name = "__atomic_int_test_n_set";

    @CafeEnter
    public static IValue enter(AtomIntCell cell, int expected, int actual)
    {
      return Factory.newBool(cell.compareAndSet(expected, actual));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((AtomIntCell) args[0], Factory.intValue(args[1]), Factory.intValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(AtomicInt.type, rawIntType, rawIntType, StandardTypes.booleanType);
    }
  }
}
