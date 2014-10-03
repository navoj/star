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

/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
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