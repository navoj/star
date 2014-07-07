package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
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
public class Overloaded extends BaseExpression
{
  private final IContentExpression inner;
  private final IType dictType;

  public Overloaded(Location loc, IType type, IType dictType, IContentExpression inner)
  {
    super(loc, type);
    this.dictType = dictType;
    this.inner = inner;
  }

  public IContentExpression getInner()
  {
    return inner;
  }

  public IType getDictType()
  {
    return dictType;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitOverloaded(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformOverloaded(this, context);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    inner.prettyPrint(disp);
    disp.append("ø");
  }
}
