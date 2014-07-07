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
public class Conjunction extends Condition
{
  private final ICondition lhs, rhs;

  public Conjunction(Location loc, ICondition lhs, ICondition rhs)
  {
    super(loc);
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public ICondition getLhs()
  {
    return lhs;
  }

  public ICondition getRhs()
  {
    return rhs;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    lhs.prettyPrint(disp);
    disp.appendWord(StandardNames.AND);
    rhs.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitConjunction(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformConjunction(this, context);
  }
}
