package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

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
    disp.appendWord(StandardNames.FUN);
    disp.appendId(name);
    disp.append("(");
    String sep = "";
    for (IContentPattern arg : args) {
      disp.append(sep);
      sep = ", ";
      arg.prettyPrint(disp);
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
