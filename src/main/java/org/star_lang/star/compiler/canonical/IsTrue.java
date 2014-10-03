package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
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
public class IsTrue extends Condition
{
  private final IContentExpression exp;

  public IsTrue(Location loc, IContentExpression exp)
  {
    super(loc);
    this.exp = exp;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    exp.prettyPrint(disp);
  }

  public IContentExpression getExp()
  {
    return exp;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitIsTrue(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformIsTrue(this, context);
  }
}
