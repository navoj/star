package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.cafe.compile.Utils;
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
public class PatternApplication extends BasePattern
{
  private final IContentExpression ptn;
  private final IContentPattern arg;

  public PatternApplication(Location loc, IType type, IContentExpression ptn, IContentPattern arg)
  {
    super(loc, type);
    this.ptn = ptn;
    this.arg = arg;
    assert arg != null;
  }

  public PatternApplication(Location loc, IType type, IContentExpression ptn, IContentPattern... args)
  {
    super(loc, type);
    assert ptn!=null && Utils.noNulls(args);
    this.ptn = ptn;
    this.arg = new ConstructorPtn(loc, args);
  }

  public IContentExpression getAbstraction()
  {
    return ptn;
  }

  public IContentPattern getArg()
  {
    return arg;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ptn.prettyPrint(disp);
    arg.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitPatternApplication(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformPatternApplication(this, context);
  }
}
