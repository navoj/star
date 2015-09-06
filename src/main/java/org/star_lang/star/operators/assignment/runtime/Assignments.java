package org.star_lang.star.operators.assignment.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
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
public class Assignments
{
  public static class Assign implements IFunction
  {
    public static final String name = "__assign";

    @CafeEnter
    public static IValue enter(Cell cell, IValue value)
    {
      cell.value = value;
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args)
    {
      return enter((Cell) args[0], args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.procedureType(TypeUtils.referenceType(tv), tv));
    }
  }

  public static class AssignRawBool implements IFunction
  {
    public static final String name = "__assign_bool";

    @CafeEnter
    public static IValue enter(BoolCell cell, boolean value)
    {
      cell.value = value;
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((BoolCell) args[0], Factory.boolValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawBoolType;
      return TypeUtils.procedureType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class AssignRawChar implements IFunction
  {
    public static final String name = "__assign_char";

    @CafeEnter
    public static IValue enter(CharCell cell, int value)
    {
      cell.value = value;
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((CharCell) args[0], Factory.charValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawCharType;
      return TypeUtils.procedureType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class AssignRawInteger implements IFunction
  {
    public static final String name = "__assign_integer";

    @CafeEnter
    public static IValue enter(IntegerCell cell, int value)
    {
      cell.value = value;
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IntegerCell) args[0], Factory.intValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawIntegerType;
      return TypeUtils.procedureType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class AssignRawLong implements IFunction
  {
    public static final String name = "__assign_long";

    @CafeEnter
    public static IValue enter(LongCell cell, long value)
    {
      cell.value = value;
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((LongCell) args[0], Factory.lngValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawLongType;
      return TypeUtils.procedureType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class AssignRawFloat implements IFunction
  {
    public static final String name = "__assign_float";

    @CafeEnter
    public static IValue enter(FloatCell cell, double value)
    {
      cell.value = value;
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((FloatCell) args[0], Factory.fltValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      IType tv = StandardTypes.rawFloatType;
      return TypeUtils.procedureType(TypeUtils.referenceType(tv), tv);
    }
  }
}
