package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.Location;

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
public class ForLoopAction extends Action
{
  private final ICondition control;
  private final IContentAction body;
  private final List<Variable> free;
  private final List<Variable> defined;

  public ForLoopAction(Location loc, ICondition control, List<Variable> free, List<Variable> defined, IContentAction body)
  {
    super(loc, body.getType());
    this.control = control;
    this.body = body;
    this.free = free;
    this.defined = defined;
  }

  public ICondition getControl()
  {
    return control;
  }

  public IContentAction getBody()
  {
    return body;
  }

  public List<Variable> getFree()
  {
    return free;
  }

  public List<Variable> getDefined()
  {
    return defined;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.FOR);
    control.prettyPrint(disp);
    disp.appendWord(StandardNames.DO);
    disp.append("\n");
    body.prettyPrint(disp);
    disp.popIndent(mark);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitForLoopAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitForLoopAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformForLoop(this, context);
  }
}
