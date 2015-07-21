package org.star_lang.star.compiler.canonical;

import java.util.ArrayList;
import java.util.List;

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
public class LetTerm extends BaseExpression
{
  final private List<IStatement> environment;
  final private IContentExpression bound;

  public LetTerm(Location loc, IContentExpression bound, IStatement... environment)
  {
    super(loc, bound.getType());
    this.bound = bound;
    this.environment = new ArrayList<>();
    for (IStatement stmt : environment)
      this.environment.add(stmt);
  }

  public LetTerm(Location loc, IContentExpression bound, List<IStatement> environment)
  {
    super(loc, bound.getType());
    this.bound = bound;
    this.environment = environment;
  }

  public List<IStatement> getEnvironment()
  {
    return environment;
  }

  public IContentExpression getBoundExp()
  {
    return bound;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int outer = disp.markIndent();
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.LET);
    disp.append("{");

    for (IStatement entry : environment) {
      disp.append("\n");
      entry.prettyPrint(disp);
    }

    disp.popIndent(mark);
    disp.append("\n");
    disp.appendWord("}");
    disp.appendWord(StandardNames.IN);
    bound.prettyPrint(disp);
    disp.popIndent(outer);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitLetTerm(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformLetTerm(this, context);
  }
}
