package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;

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
@SuppressWarnings("serial")
public class FunctionLiteral extends ProgramLiteral implements IRule<IContentExpression>
{
  private final IContentPattern args[];
  private final IContentExpression result;

  public FunctionLiteral(Location loc, String name, IType type, IContentPattern args[], IContentExpression result,
      Variable[] freeVars)
  {
    super(loc, type, name, freeVars);

    this.args = args;
    this.result = result;
  }

  @Override
  public IContentPattern[] getArgs()
  {
    return args;
  }

  @Override
  public IContentExpression getBody()
  {
    return result;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(name);
    disp.append("(");
    String sep = "";
    for (int ix = 0; ix < args.length; ix++) {
      disp.append(sep);
      sep = ", ";
      args[ix].prettyPrint(disp);
    }
    disp.append(")");
    disp.appendWord(StandardNames.IS);
    result.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitFunctionLiteral(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformFunctionLiteral(this, context);
  }
}
