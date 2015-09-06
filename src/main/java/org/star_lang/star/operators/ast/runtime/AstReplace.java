package org.star_lang.star.operators.ast.runtime;

import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Cons;
import org.star_lang.star.data.value.Cons.ConsCons;
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
public class AstReplace implements IFunction {
  public static final String name = "__astMacroReplace";

  @CafeEnter
  public static IAbstract enter(IAbstract term, Cons path, IAbstract repl) throws EvaluationException {
    if (path.equals(Cons.nilEnum))
      return repl;
    else {
      assert path instanceof ConsCons && term instanceof Apply;
      ConsCons consPath = (ConsCons) path;
      Cons tail = (Cons) consPath.get___1();
      Apply app = (Apply) term;
      IAbstract op = app.getOperator();
      IList args = app.getArgs();
      int ix = Factory.intValue(consPath.get___0());
      if (ix < 0)
        op = enter(op, tail, repl);
      else
        args = args.substituteCell(ix, enter((IAbstract) args.getCell(ix), tail, repl));

      return new Apply(app.getLoc(), op, args, app.getCategories(), app.getAttributes());
    }
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException {
    return enter((IAbstract) args[0], (Cons) args[1], (IAbstract) args[2]);
  }

  @Override
  public IType getType() {
    return type();
  }

  public static IType type() {
    return TypeUtils.functionType(StandardTypes.astType, TypeUtils.consType(StandardTypes.integerType),
        StandardTypes.astType, StandardTypes.astType);
  }
}
