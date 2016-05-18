package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

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

public abstract class LongUnary {

  public static class LongAbs implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return Math.abs(s1);
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongUMinus implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return -s1;
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongRandom implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      double random = Math.random();
      return new Double(random * s1).longValue();
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongSqrt implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return (long) Math.sqrt(s1);
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongCbrt implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return (long) Math.cbrt(s1);
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongCeil implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return s1;
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongFloor implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return s1;
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongRound implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return s1;
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongLog implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return (long) Math.log(s1);
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongLog10 implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return (long) Math.log10(s1);
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongExp implements IFunction {
    @CafeEnter
    public static long enter(long s1) {
      return (long) Math.exp(s1);
    }

    @Override
    public IType getType() {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongHash implements IFunction {
    public static final String name = "__long_hash";

    @CafeEnter
    public static int enter(long s1) {
      return (int) s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newInt(enter(Factory.longValue(args[0])));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type(){
      return TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawIntegerType);
    }
  }
}
