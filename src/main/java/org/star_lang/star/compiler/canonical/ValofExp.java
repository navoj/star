package org.star_lang.star.compiler.canonical;

import java.util.Iterator;
import java.util.List;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.SingleIterator;
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
public class ValofExp extends BaseExpression implements Iterable<IContentAction>
{
  private final IContentAction action;

  public ValofExp(Location loc, IType type, IContentAction... action)
  {
    super(loc, type);
    if (action.length != 1)
      this.action = new Sequence(loc, type, FixedList.create(action));
    else
      this.action = action[0];
  }
  
  public ValofExp(Location loc, IType type, List<IContentAction> action)
  {
    super(loc, type);
    if (action.size() != 1)
      this.action = new Sequence(loc, type, action);
    else
      this.action = action.get(0);
  }

  public IContentAction getAction()
  {
    return action;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.VALOF);
    action.prettyPrint(disp);
  }

  @Override
  public Iterator<IContentAction> iterator()
  {
    if (action instanceof Sequence)
      return ((Sequence) action).iterator();
    else
      return new SingleIterator<IContentAction>(action);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitValofExp(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformValofExp(this, context);
  }
}
