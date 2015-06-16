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
public class ConditionalExp extends BaseExpression
{
  private final ICondition cnd;
  private final IContentExpression thExp;
  private final IContentExpression elExp;

  public ConditionalExp(Location loc, IType type, ICondition cnd, IContentExpression thExp, IContentExpression elExp)
  {
    super(loc, type);
    this.cnd = cnd;
    this.thExp = thExp;
    this.elExp = elExp;
  }

  public ICondition getCnd()
  {
    return cnd;
  }

  public IContentExpression getThExp()
  {
    return thExp;
  }

  public IContentExpression getElExp()
  {
    return elExp;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("(");
    showCond(this, disp);
    disp.append(")");
    disp.popIndent(mark);
  }

  private static void showCond(IContentExpression exp, PrettyPrintDisplay disp)
  {
    while (exp instanceof ConditionalExp) {
      ConditionalExp cond = (ConditionalExp) exp;
      cond.getCnd().prettyPrint(disp);
      disp.append(StandardNames.QUESTION);
      int mark = disp.markIndent(2);
      disp.append("\n");
      cond.getThExp().prettyPrint(disp);
      disp.append(StandardNames.COLON);
      disp.popIndent(mark);
      disp.append("\n");
      exp = cond.getElExp();
    }
    exp.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitConditionalExp(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformConditionalExp(this, context);
  }
}
