package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

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
public class Matches extends Condition
{
  private final IContentExpression exp;
  private final IContentPattern ptn;

  /**
   * The Matches condition encodes a matches predicate
   * 
   * @param lhs
   *          the expression being matched
   * @param rhs
   *          the pattern being used to match
   */
  public Matches(Location loc, IContentExpression lhs, IContentPattern rhs)
  {
    super(loc);
    this.exp = lhs;
    this.ptn = rhs;
  }

  public IContentExpression getExp()
  {
    return exp;
  }

  public IContentPattern getPtn()
  {
    return ptn;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    exp.prettyPrint(disp);
    disp.appendWord(StandardNames.MATCHES);
    ptn.prettyPrint(disp);
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitMatches(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformMatches(this, context);
  }
}
