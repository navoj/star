package org.star_lang.star.operators.sets.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.*;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.BoolWrap;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.SetTree;
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

public class SetOps {
  public static class SetCreate implements IFunction {
    public static final String name = "__set_create";

    @CafeEnter
    public static ISet enter() throws EvaluationException {
      return new SetTree();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter();
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      return new UniversalType(v, TypeUtils.functionType(TypeUtils.setType(v)));
    }
  }

  public static class SetContains implements IFunction {
    public static final String name = "__set_contains";

    @CafeEnter
    public static boolean enter(ISet set, IValue key) {
      return set.contains(key);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newBool(enter((ISet) args[0], args[1]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      return new UniversalType(v, TypeUtils.functionType(TypeUtils.setType(v), v, StandardTypes.booleanType));
    }
  }

  public static class SetEqual implements IFunction {
    public static final String name = "__set_equal";

    @CafeEnter
    public static boolean enter(ISet ar1, ISet ar2) throws EvaluationException {
      return ar1.equals(ar2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newBool(enter((ISet) args[0], (ISet) args[1]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      // for all %v st (set of (%v),set of (%v),(%v,%v)=>boolean) => boolean
      TypeVar v = new TypeVar("%v");

      IType setType = TypeUtils.setType(v);
      IType boolType = StandardTypes.booleanType;

      IType funType = TypeUtils.functionType(setType, setType, boolType);
      return new UniversalType(v, funType);
    }
  }

  public static class SetInsert implements IFunction {
    public static final String name = "__set_insert";

    @CafeEnter
    public static ISet enter(ISet lhs, IValue el) throws EvaluationException {
      return lhs.insert(el);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType setType = TypeUtils.setType(v);
      return new UniversalType(v, TypeUtils.functionType(setType, v, setType));
    }
  }

  public static class SetDelete implements IFunction {
    public static final String name = "__set_delete";

    @CafeEnter
    public static ISet enter(ISet lhs, IValue el) throws EvaluationException {
      return lhs.delete(el);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType setType = TypeUtils.setType(v);
      return new UniversalType(v, TypeUtils.functionType(setType, v, setType));
    }
  }

  public static class SetIntersect implements IFunction {
    public static final String name = "__set_intersect";

    @CafeEnter
    public static ISet enter(ISet lhs, ISet rhs) throws EvaluationException {
      ISet result = new SetTree();

      for (IValue entry : lhs)
        if (rhs.contains(entry))
          result = result.insert(entry);
      return result;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (ISet) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType setType = TypeUtils.setType(v);
      return new UniversalType(v, TypeUtils.functionType(setType, setType, setType));
    }
  }

  public static class SetUnion implements IFunction {
    public static final String name = "__set_union";

    @CafeEnter
    public static ISet enter(ISet set, ISet rhs) throws EvaluationException {
      for (IValue entry : rhs)
        set = set.insert(entry);
      return set;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (ISet) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType setType = TypeUtils.setType(v);
      return new UniversalType(v, TypeUtils.functionType(setType, setType, setType));
    }
  }

  public static class SetDifference implements IFunction {
    public static final String name = "__set_difference";

    @CafeEnter
    public static ISet enter(ISet lhs, ISet rhs) throws EvaluationException {
      ISet result = new SetTree();

      for (IValue entry : lhs)
        if (!rhs.contains(entry))
          result = result.insert(entry);
      return result;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (ISet) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType setType = TypeUtils.setType(v);
      return new UniversalType(v, TypeUtils.functionType(setType, setType, setType));
    }
  }

  public static class SetFilter implements IFunction {
    public static final String name = "__set_filter_out";

    @CafeEnter
    public static ISet enter(ISet set, IPattern filter) throws EvaluationException {
      return set.filterOut(filter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0], (IPattern) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      IType setType = TypeUtils.setType(v);
      IType ptnType = TypeUtils.patternType(TypeUtils.tupleType(), v);
      return new UniversalType(v, TypeUtils.functionType(setType, ptnType, setType));
    }
  }

  public static class SetMap implements IFunction {
    public static final String name = "__set_map";

    @CafeEnter
    public static ISet enter(IFunction fn,ISet set) throws EvaluationException {
      return set.map(fn);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((IFunction)args[0],(ISet) args[1]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      TypeVar r = new TypeVar();
      IType setType = TypeUtils.setType(v);
      IType resType = TypeUtils.setType(r);
      IType fnType = TypeUtils.functionType(v, r);
      return new UniversalType(v, new UniversalType(r, TypeUtils.functionType(fnType, setType, resType)));
    }
  }

  public static class SetEmpty implements IFunction {
    public static final String name = "__set_empty";

    @CafeEnter
    public static BoolWrap enter(ISet set) {
      return Factory.newBool(set.isEmpty());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return enter((ISet) args[0]);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar v = new TypeVar();
      return new UniversalType(v, TypeUtils.functionType(TypeUtils.setType(v),
              StandardTypes.booleanType));
    }
  }

  public static class SetSize implements IFunction {
    public static final String name = "__set_size";

    @CafeEnter
    public static int enter(ISet set) {
      return set.size();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return Factory.newInt(enter((ISet) args[0]));
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      TypeVar vl = new TypeVar();
      return new UniversalType(vl, TypeUtils.functionType(TypeUtils.setType(vl),
              StandardTypes.rawIntegerType));
    }
  }
}
