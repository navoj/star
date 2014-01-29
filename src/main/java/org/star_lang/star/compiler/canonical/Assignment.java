package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;

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
public class Assignment extends Action
{
  private final IContentExpression lValue;
  private final IContentExpression value;

  public Assignment(Location loc, IContentExpression lValue, IContentExpression value)
  {
    super(loc, StandardTypes.unitType);
    this.lValue = lValue;
    this.value = value;
  }

  public IContentExpression getLValue()
  {
    return lValue;
  }

  public IContentExpression getValue()
  {
    return value;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    lValue.prettyPrint(disp);
    disp.append(StandardNames.ASSIGN);
    value.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitAssignment(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitAssignment(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformAssignment(this, context);
  }
}
