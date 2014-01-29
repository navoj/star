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

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class Inliner extends DefaultTransformer<InlineContext>
{

  @Override
  public IContentExpression transformFunctionLiteral(FunctionLiteral f, InlineContext context)
  {
    // TODO Auto-generated method stub
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
    // TODO Auto-generated method stub
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
