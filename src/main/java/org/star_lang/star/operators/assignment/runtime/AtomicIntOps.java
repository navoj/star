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


/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
