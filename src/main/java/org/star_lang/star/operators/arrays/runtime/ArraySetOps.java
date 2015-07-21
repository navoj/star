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

/**
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author fgm
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
      return new UniversalType(v, TypeUtils.functionType(colType, v, eqType,  StandardTypes.booleanType));
    }
  }
}
