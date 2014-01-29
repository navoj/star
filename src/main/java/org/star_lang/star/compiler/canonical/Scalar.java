package org.star_lang.star.compiler.canonical;

import java.math.BigDecimal;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.QuoteDisplay;

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
public class Scalar extends BaseExpression
{
  private final IValue value;

  public Scalar(Location loc, IType type, IValue value)
  {
    super(loc, type);
    this.value = value;
  }

  public Scalar(Location loc, IValue value)
  {
    this(loc, value.getType(), value);
  }

  public Scalar(Location loc, IType type, char ch)
  {
    this(loc, type, Factory.newChar(ch));
  }

  public Scalar(Location loc, IType type, int value)
  {
    this(loc, type, Factory.newInt(value));
  }

  public Scalar(Location loc, IType type, long value)
  {
    this(loc, type, Factory.newLng(value));
  }

  public Scalar(Location loc, IType type, String value)
  {
    this(loc, type, Factory.newString(value));
  }

  public Scalar(Location loc, IType type, BigDecimal value)
  {
    this(loc, type, Factory.newDecimal(value));
  }

  public Scalar(Location loc, IType type, double value)
  {
    this(loc, type, Factory.newFlt(value));
  }

  public IValue getValue()
  {
    return value;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    QuoteDisplay.display(disp, value);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitScalar(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformScalar(this, context);
  }
}
