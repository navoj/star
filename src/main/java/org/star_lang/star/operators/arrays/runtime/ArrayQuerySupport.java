package org.star_lang.star.operators.arrays.runtime;

import java.util.ArrayList;
import java.util.HashSet;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
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

public class ArrayQuerySupport
{
  public static class ArrayProject0 implements IFunction
  {
    public static final String name = "__array_project_0";

    @CafeEnter
    public static Array enter(Array els) throws EvaluationException
    {
      IValue projected[] = new IValue[els.size()];
      for (int ix = 0; ix < els.size(); ix++) {
        IValue el = els.getCell(ix);
        if (!(el instanceof IConstructor))
          throw new EvaluationException("illegal entry");
        else
          projected[ix] = ((IConstructor) el).getCell(0);
      }

      return Array.newArray(projected);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Array) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v1 = new TypeVar();
      TypeVar v2 = new TypeVar();
      return new UniversalType(v1, new UniversalType(v2, TypeUtils.functionType(TypeUtils.arrayType(TypeUtils
          .tupleType(v1, v2)), TypeUtils.arrayType(v1))));
    }
  }

  public static class ArrayUnique implements IFunction
  {
    public static final String name = "__array_unique";

    @CafeEnter
    public static Array enter(Array data, IFunction equalizer) throws EvaluationException
    {
      HashSet<Integer> seenHashes = new HashSet<>();
      ArrayList<IValue> reslt = new ArrayList<>(data.size());

      // only assume that two things that hash to different things are different
      for (IValue el : data) {
        if (seenHashes.contains(el.hashCode())) {
          boolean found = false;
          for (IValue el2 : reslt) {
            if (equalizer.enter(el, el2).equals(Factory.trueValue)) {
              found = true;
              break;
            }
          }
          if (!found) {
            reslt.add(el);
          }
        } else {
          reslt.add(el);
          seenHashes.add(el.hashCode());
        }
      }
      return Array.newArray(reslt);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Array) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType equalityType = TypeUtils.functionType(v, v, StandardTypes.booleanType);
      IType listType = TypeUtils.arrayType(v);
      return new UniversalType(v, TypeUtils.functionType(listType, equalityType, listType));
    }
  }
}
