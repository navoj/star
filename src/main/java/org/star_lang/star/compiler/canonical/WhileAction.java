package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

/**
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
public class WhileAction extends Action
{
  private final ICondition control;
  private final IContentAction body;

  public WhileAction(Location loc, ICondition control, IContentAction body)
  {
    super(loc, body.getType());
    this.control = control;
    this.body = body;
  }

  public ICondition getControl()
  {
    return control;
  }

  public IContentAction getBody()
  {
    return body;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.WHILE);
    control.prettyPrint(disp);
    disp.appendWord(StandardNames.DO);
    disp.append("\n");
    body.prettyPrint(disp);
    disp.popIndent(mark);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitWhileAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitWhileAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformWhileLoop(this, context);
  }
}
