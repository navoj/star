package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.IType;
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
public class ConditionalAction extends Action
{
  private final ICondition cond;
  private final IContentAction thPart;
  private final IContentAction elPart;

  public ConditionalAction(Location loc, ICondition cond, IContentAction thPart, IContentAction elPart)
  {
    super(loc, computeResultType(thPart.getType(), elPart.getType()));
    this.cond = cond;
    this.thPart = thPart;
    this.elPart = elPart;
  }

  private static IType computeResultType(IType lhs, IType rhs)
  {
    lhs = TypeUtils.deRef(lhs);
    rhs = TypeUtils.deRef(rhs);
    if (lhs.equals(StandardTypes.unitType))
      return rhs;
    else
      return lhs;
  }

  public ICondition getCond()
  {
    return cond;
  }

  public IContentAction getThPart()
  {
    return thPart;
  }

  public IContentAction getElPart()
  {
    return elPart;
  }

  @Override
  public IType getType()
  {
    return super.getType();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.IF);
    cond.prettyPrint(disp);
    disp.appendWord(StandardNames.THEN);
    disp.append("\n  ");
    int mark = disp.markIndent();
    thPart.prettyPrint(disp);
    disp.popIndent(mark);
    if (!CompilerUtils.isTrivial(elPart)) {
      disp.append("\n");
      disp.appendWord(StandardNames.ELSE);
      disp.append("\n  ");
      mark = disp.markIndent();
      elPart.prettyPrint(disp);
      disp.popIndent(mark);
    }
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitConditionalAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitConditionalAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformConditionalAction(this, context);
  }
}
