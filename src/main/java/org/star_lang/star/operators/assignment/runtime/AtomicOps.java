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

/**
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
