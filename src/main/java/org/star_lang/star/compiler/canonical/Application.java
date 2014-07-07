package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
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
public class Application extends BaseExpression
{
  private final IContentExpression function;
  private final IContentExpression args;

  public Application(Location loc, IType type, IContentExpression function, IContentExpression args)
  {
    super(loc, type);
    this.function = function;
    this.args = args;
    assert function != null && args != null && type != null;
  }

  public Application(Location loc, IType type, IContentExpression function, IContentExpression... args)
  {
    this(loc, type, function, new ConstructorTerm(loc, args));
  }

  public static IContentExpression apply(Location loc, IType type, IContentExpression function,
      IContentExpression... args)
  {
    return new Application(loc, type, function, new ConstructorTerm(loc, args));
  }

  public IContentExpression getFunction()
  {
    return function;
  }

  public IContentExpression getArgs()
  {
    return args;
  }

  public IContentExpression getArg(int ix)
  {
    assert args instanceof ConstructorTerm;
    return ((ConstructorTerm) args).getArg(ix);
  }

  public int arity()
  {
    return TypeUtils.typeArity(getArgs().getType());
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    function.prettyPrint(disp);
    if (!(args instanceof ConstructorTerm || args instanceof RecordTerm))
      disp.append(StandardNames.APPLY);
    args.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitApplication(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformApplication(this, context);
  }
}
