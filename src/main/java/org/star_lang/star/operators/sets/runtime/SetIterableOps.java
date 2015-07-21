package org.star_lang.star.operators.sets.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.*;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.arrays.runtime.ArrayOps;

import java.util.Iterator;

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

public class SetIterableOps {

  public static class SetIterate implements IFunction {
    public static final String name = "__set_iterate";

    @CafeEnter
    public static IValue enter(ISet set, IFunction iter, IValue state) throws EvaluationException {
      for (Iterator<IValue> it = set.iterator(); it.hasNext() && ArrayOps.moreToDo(state); ) {
        IValue entry = it.next();
        state = iter.enter(entry, state);
      }

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      // %v~%s~(set of (%v),(%v,%s)=>%s,%s) => %s

      TypeVar v = new TypeVar();
      TypeVar s = new TypeVar();

      IType setType = TypeUtils.setType(v);

      IType funType = TypeUtils.functionType(setType, TypeUtils.functionType(v, s, s), s, s);
      return new UniversalType(v, new UniversalType(s, funType));
    }
  }

  public static class SetLeftFold implements IFunction {
    public static final String name = "__set_left_fold";

    @CafeEnter
    public static IValue enter(ISet src, IFunction transform, IValue state) throws EvaluationException {
      for (Iterator<IValue> it = src.iterator(); it.hasNext(); ) {
        IValue entry = it.next();
        state = transform.enter(state, entry);
      }
      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.setType(el), TypeUtils.functionType(st, el, st), st, st);
      return new UniversalType(el, new UniversalType(st, funType));
    }
  }

  public static class SetLeftFold1 implements IFunction {
    public static final String name = "__set_left_fold1";

    @CafeEnter
    public static IValue enter(ISet src, IFunction transform) throws EvaluationException {
      Iterator<IValue> it = src.iterator();
      IValue state = it.next();

      if (it.hasNext()) {
        while (it.hasNext()) {
          IValue entry = it.next();
          state = transform.enter(state, entry);
        }
        return state;
      } else
        throw new EvaluationException("set is empty");
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.setType(el), TypeUtils.functionType(st, el, st), st);
      return new UniversalType(el, new UniversalType(st, funType));
    }
  }

  public static class SetRightFold implements IFunction {
    public static final String name = "__set_right_fold";

    @CafeEnter
    public static IValue enter(ISet src, IFunction transform, IValue state) throws EvaluationException {
      for (Iterator<IValue> it = src.reverseIterator(); it.hasNext(); ) {
        IValue entry = it.next();
        state = transform.enter(entry, state);
      }
      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.setType(el), TypeUtils.functionType(el, st, st), st, st);
      return new UniversalType(el, new UniversalType(st, funType));
    }
  }

  public static class SetRightFold1 implements IFunction {
    public static final String name = "__set_right_fold1";

    @CafeEnter
    public static IValue enter(ISet src, IFunction transform) throws EvaluationException {
      Iterator<IValue> it = src.reverseIterator();
      IValue state = it.next();
      if (it.hasNext()) {
        while (it.hasNext()) {
          IValue entry = it.next();
          state = transform.enter(entry, state);
        }
        return state;
      } else
        throw new EvaluationException("set is empty");
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.setType(el), TypeUtils.functionType(el, st, st), st);
      return new UniversalType(el, new UniversalType(st, funType));
    }
  }

  public static class SetUpdate implements IFunction {
    public static final String name = "__set_update";

    @CafeEnter
    public static ISet enter(ISet src,IPattern filter,IFunction transform) throws EvaluationException {
      return src.updateUsingPattern(filter,transform);
    }

    @Override
    public IValue enter(IValue ... args) throws  EvaluationException {
      return enter((ISet) args[0], (IPattern) args[1], (IFunction) args[2]);
    }

    @Override
    public IType getType() { return type(); }

    public static IType type(){
      TypeVar v = new TypeVar();
      IType setType = TypeUtils.setType(v);
      IType ptnType = TypeUtils.patternType(TypeUtils.tupleType(), v);
      IType trType = TypeUtils.functionType(v,v);
      return new UniversalType(v, TypeUtils.functionType(setType, ptnType, trType, setType));
    }
  }
}
