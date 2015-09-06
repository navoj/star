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
import org.star_lang.star.operators.assignment.runtime.AtomicCell.AtomCell;

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
public class AtomicOps
{
  public static class AtomReference implements IFunction
  {
    public static final String name = "__atomic_reference";

    @CafeEnter
    public static IValue enter(AtomCell cell)
    {
      return cell.get();
    }

    @Override
    public IValue enter(IValue... args)
    {
      return enter((AtomCell) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(AtomicCell.typeExp(tv), tv));
    }
  }

  public static class AtomicAssignment implements IFunction
  {
    public static final String name = "__atomic_assign";

    @CafeEnter
    public static IValue enter(AtomCell cell, IValue value)
    {
      cell.set(value);
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args)
    {
      return enter((AtomCell) args[0], args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.procedureType(AtomicCell.typeExp(tv), tv));
    }
  }

  public static class AtomTestAndSet implements IFunction
  {
    public static final String name = "__atomic_test_n_set";

    @CafeEnter
    public static IValue enter(AtomCell cell, IValue expected, IValue actual)
    {
      return Factory.newBool(cell.compareAndSet(expected, actual));
    }

    @Override
    public IValue enter(IValue... args)
    {
      return enter((AtomCell) args[0], args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(AtomicCell.typeExp(tv), tv, tv, StandardTypes.booleanType));
    }
  }
}
