package org.star_lang.star.operators.ast.runtime;

import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.*;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.CafeEnter;

import java.util.Stack;

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

public class AstQuoter implements IFunction {

  public static final String name = "__quote";

  @CafeEnter
  public ASyntax quote(IValue term) throws EvaluationException {
    Quoter quoter = new Quoter();
    return quoter.quote(term);
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException {
    return quote(args[0]);
  }

  @Override
  public IType getType() {
    return type();
  }

  public static IType type() {
    TypeVar tv = new TypeVar();
    return UniversalType.universal(new TypeVar[]{tv}, TypeUtils.functionType(tv, ASyntax.type));
  }

  private static class Quoter implements IValueVisitor {
    private Stack<ASyntax> stack = new Stack<>();

    ASyntax quote(IValue term) {
      stack.clear();
      term.accept(this);
      assert stack.size() == 1;
      return stack.pop();
    }

    @Override
    public void visitScalar(IScalar<?> scalar) {
      stack.push(abstractValue(Location.noWhereEnum, scalar, scalar.getType()));
    }

    static ASyntax abstractValue(Location loc, Object val, IType type) {
      if (val instanceof Integer)
        return Abstract.newInteger(loc, ((Integer) val));
      else if (val instanceof Double)
        return Abstract.newFloat(loc, ((Double) val));
      else if (val instanceof String)
        return Abstract.newString(loc, ((String) val));
      else if (val instanceof Long)
        return Abstract.newLong(loc, (Long) val);
      else if (val instanceof Boolean)
        return Abstract.newBoolean(loc, (Boolean) val);
      else if (val instanceof IScalar<?>)
        return abstractValue(loc, ((IScalar<?>) val).getValue(), type);
      else
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitRecord(IRecord agg) {
      // TODO Auto-generated method stub

    }

    @Override
    public void visitList(IList list) {
      // TODO Auto-generated method stub

    }

    @Override
    public void visitFunction(IFunction fn) {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitPattern(IPattern ptn) {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitConstructor(IConstructor con) {
      // TODO Auto-generated method stub

    }

    @Override
    public void visitMap(IMap map) {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitSet(ISet set) {

    }
  }
}
