package org.star_lang.star.operators.arrays.runtime;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Array;
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
public class ArraySetOps {
  public static class ArrayUnion implements IFunction {
    public static final String name = "__array_union";

    @CafeEnter
    public static Array enter(Array left, Array right, IFunction equals) throws EvaluationException {
      List<IValue> tmp = new ArrayList<>(left.size());
      for (IValue el : left)
        tmp.add(el);

      outer:
      for (IValue el : right) {
        for (IValue lftEl : tmp)
          if (Factory.boolValue(equals.enter(el, lftEl)))
            continue outer;
        tmp.add(el);
      }

      return Array.newArray(tmp);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((Array) args[0], (Array) args[1], (IFunction) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType eqType = TypeUtils.functionType(v, v, StandardTypes.booleanType);
      IType colType = TypeUtils.arrayType(v);
      return new UniversalType(v, TypeUtils.functionType(colType, colType, eqType, colType));
    }
  }

  public static class ArrayIntersect implements IFunction {
    public static final String name = "__array_intersect";

    @CafeEnter
    public static Array enter(Array left, Array right, IFunction equals) throws EvaluationException {
      List<IValue> tmp = new ArrayList<>(Math.min(left.size(), right.size()));

      outer:
      for (IValue lEl : left) {
        for (IValue rEl : right) {
          if (Factory.boolValue(equals.enter(lEl, rEl))) {
            tmp.add(lEl);
            continue outer;
          }
        }
      }

      return Array.newArray(tmp);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((Array) args[0], (Array) args[1], (IFunction) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType eqType = TypeUtils.functionType(v, v, StandardTypes.booleanType);
      IType colType = TypeUtils.arrayType(v);
      return new UniversalType(v, TypeUtils.functionType(colType, colType, eqType, colType));
    }
  }

  public static class ArrayComplement implements IFunction {
    public static final String name = "__array_complement";

    @CafeEnter
    public static Array enter(Array left, Array right, IFunction equals) throws EvaluationException {
      List<IValue> tmp = new ArrayList<>(left.size());

      outer:
      for (IValue lEl : left) {
        for (IValue rEl : right) {
          if (Factory.boolValue(equals.enter(lEl, rEl)))
            continue outer;
        }
        tmp.add(lEl);
      }

      return Array.newArray(tmp);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((Array) args[0], (Array) args[1], (IFunction) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType eqType = TypeUtils.functionType(v, v, StandardTypes.booleanType);
      IType colType = TypeUtils.arrayType(v);
      return new UniversalType(v, TypeUtils.functionType(colType, colType, eqType, colType));
    }
  }

  public static class ArraySearch implements IFunction {
    public static final String name = "__array_search";

    @CafeEnter
    public static IValue enter(Array array, IValue el, IFunction equals) throws EvaluationException {

      for (IValue lEl : array) {
        if (Factory.boolValue(equals.enter(lEl, el)))
          return Factory.newBool(true);
      }

      return Factory.newBool(false);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((Array) args[0], args[1], (IFunction) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType eqType = TypeUtils.functionType(v, v, StandardTypes.booleanType);
      IType colType = TypeUtils.arrayType(v);
      return new UniversalType(v, TypeUtils.functionType(colType, v, eqType, StandardTypes.booleanType));
    }
  }
}
