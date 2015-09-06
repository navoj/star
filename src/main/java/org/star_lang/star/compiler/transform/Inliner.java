package org.star_lang.star.compiler.transform;

import org.star_lang.star.compiler.canonical.DefaultTransformer;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.PatternAbstraction;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
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

public class Inliner extends DefaultTransformer<InlineContext>
{

  @Override
  public IContentExpression transformFunctionLiteral(FunctionLiteral f, InlineContext context)
  {
    return super.transformFunctionLiteral(f, context);
  }

  @Override
  public IContentExpression transformLetTerm(LetTerm let, InlineContext context)
  {
    InlineContext sub = context.fork();
    for (IStatement stmt : let.getEnvironment()) {
      if (stmt instanceof VarEntry)
        sub.exclude(stmt.definedFields());
    }
    return super.transformLetTerm(let, sub);
  }

  @Override
  public IContentExpression transformPatternAbstraction(PatternAbstraction pattern, InlineContext context)
  {
    return super.transformPatternAbstraction(pattern, context);
  }

  @Override
  public IContentExpression transformVariable(Variable var, InlineContext context)
  {
    return context.replaceVar(var);
  }

  @Override
  public IContentAction transformLetAction(LetAction let, InlineContext context)
  {
    InlineContext sub = context.fork();
    for (IStatement stmt : let.getEnvironment()) {
      if (stmt instanceof VarEntry)
        sub.exclude(stmt.definedFields());
    }

    return super.transformLetAction(let, sub);
  }
}
