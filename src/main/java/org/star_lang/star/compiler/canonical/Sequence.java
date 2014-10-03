package org.star_lang.star.compiler.canonical;

import java.util.Iterator;
import java.util.List;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
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
public class Sequence extends Action implements Iterable<IContentAction>
{
  private final List<IContentAction> actions;

  public Sequence(Location loc, IType type, List<IContentAction> actions)
  {
    super(loc, type);
    this.actions = actions;
  }

  public List<IContentAction> getActions()
  {
    return actions;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("{\n");
    disp.prettyPrint(actions, ";\n");
    disp.append("}");
    disp.popIndent(mark);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitSequence(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitSequence(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformSequence(this, context);
  }

  @Override
  public Iterator<IContentAction> iterator()
  {
    return actions.iterator();
  }

}
