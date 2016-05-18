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
import org.star_lang.star.operators.assignment.runtime.RefCell.*;

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
public class GetRefValue {
  public static class GetRef implements IFunction {
    public static final String name = "__dereference";

    @CafeEnter
    public static IValue enter(Cell cell) {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args) {
      return enter((Cell) args[0]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.referenceType(tv), tv));
    }
  }

  public static class GetRawBoolRef implements IFunction {
    public static final String name = "__deref_bool";

    @CafeEnter
    public static boolean enter(BoolCell cell) {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args) {
      return Factory.newBool(enter((BoolCell) args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType tv = StandardTypes.rawBoolType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class GetRawIntegerRef implements IFunction {
    public static final String name = "__deref_integer";

    @CafeEnter
    public static int enter(IntegerCell cell) {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args) {
      return Factory.newInt(enter((IntegerCell) args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType tv = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class GetRawLongRef implements IFunction {
    public static final String name = "__deref_long";

    @CafeEnter
    public static long enter(LongCell cell) {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args) {
      return Factory.newLng(enter((LongCell) args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType tv = StandardTypes.rawLongType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }

  public static class GetRawFloatRef implements IFunction {
    public static final String name = "__deref_float";

    @CafeEnter
    public static double enter(FloatCell cell) {
      return cell.value;
    }

    @Override
    public IValue enter(IValue... args) {
      return Factory.newFloat(enter((FloatCell) args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      IType tv = StandardTypes.rawFloatType;
      return TypeUtils.functionType(TypeUtils.referenceType(tv), tv);
    }
  }
}
