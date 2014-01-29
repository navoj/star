package org.star_lang.star.compiler.canonical;

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
public class ScalarPtn extends ContentPattern
{
  private final IValue value;

  public ScalarPtn(Location loc, IType type, IValue value)
  {
    super(loc, type);
    this.value = value;
  }

  public ScalarPtn(Location loc, IType type, int value)
  {
    this(loc, type, Factory.newInt(value));
  }

  public ScalarPtn(Location loc, IType type, String value)
  {
    this(loc, type, Factory.newString(value));
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
    visitor.visitScalarPtn(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformScalarPtn(this, context);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof ScalarPtn)
      return value.equals(((ScalarPtn) obj).value);
    else
      return false;
  }

  public Scalar asScalar()
  {
    return new Scalar(getLoc(), getType(), getValue());
  }
}
