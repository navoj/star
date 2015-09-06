package org.star_lang.star.compiler.transform;

import org.star_lang.star.compiler.FreeVariables;
import org.star_lang.star.compiler.canonical.Canonical;
import org.star_lang.star.compiler.canonical.ExpressionTransformer;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.IRule;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.type.Dictionary;
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

class Substituter extends ExpressionTransformer
{
  private final Canonical old;
  private final Canonical repl;

  Substituter(Canonical old, Canonical repl, Dictionary cxt)
  {
    super(cxt);
    this.old = old;
    this.repl = repl;

    install(new VariablePtnTrans());
    install(new VariableTrans());
    install(new OverloadedVariableTrans());
  }

  @Override
  public IContentExpression transform(IContentExpression term)
  {
    if (old.equals(term))
      return (IContentExpression) repl;
    else
      return super.transform(term);
  }

  @Override
  public IContentPattern transform(IContentPattern ptn)
  {
    if (old.equals(ptn))
      return (IContentPattern) repl;
    else
      return super.transform(ptn);
  }

  @Override
  public IStatement transform(IStatement stmt)
  {
    if (stmt instanceof IRule<?>) {
      IRule<?> rule = (IRule<?>) stmt;

      // Implement scope hiding for rules
      if (FreeVariables.varPresent((Variable) old, rule.getArgs()))
        return stmt;
    }
    return super.transform(stmt);
  }

  private class VariableTrans implements TransformExpression
  {
    @Override
    public Class<? extends IContentExpression> transformClass()
    {
      return Variable.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression term)
    {
      if (old.equals(term))
        return (Variable) repl;
      else
        return term;
    }
  }

  private class OverloadedVariableTrans implements TransformExpression
  {
    @Override
    public Class<? extends IContentExpression> transformClass()
    {
      return OverloadedVariable.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression term)
    {
      OverloadedVariable over = (OverloadedVariable) term;
      if (old instanceof Variable && ((Variable) old).getName().equals(over.getName())
          && ((Variable) old).getType().equals(over.getType()))
        return (Variable) repl;
      else
        return term;
    }
  }

  private class VariablePtnTrans implements TransformPattern
  {
    @Override
    public Class<? extends IContentPattern> transformClass()
    {
      return Variable.class;
    }

    @Override
    public IContentPattern transformPtn(IContentPattern term)
    {
      if (old.equals(term))
        return (Variable) repl;
      else
        return term;
    }
  }
}