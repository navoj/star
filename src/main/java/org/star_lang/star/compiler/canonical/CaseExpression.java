package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.Pair;
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
public class CaseExpression extends BaseExpression
{
  private final IContentExpression selector;
  private final List<Pair<IContentPattern, IContentExpression>> cases;
  private final IContentExpression deflt;

  public CaseExpression(Location loc, IType type, IContentExpression selector,
      List<Pair<IContentPattern, IContentExpression>> cases, IContentExpression deflt)
  {
    super(loc, type);
    this.selector = selector;
    this.cases = cases;
    this.deflt = deflt;
    assert !cases.isEmpty();
  }

  public IContentExpression getSelector()
  {
    return selector;
  }

  public List<Pair<IContentPattern, IContentExpression>> getCases()
  {
    return cases;
  }

  public IContentExpression getDeflt()
  {
    return deflt;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.CASE);
    getSelector().prettyPrint(disp);
    disp.appendWord(StandardNames.IN);
    disp.append("{");
    String sep = "\n";
    for (Pair<IContentPattern, IContentExpression> entry : getCases()) {
      disp.append(sep);
      sep = ";\n";
      entry.getKey().prettyPrint(disp);
      disp.appendWord(StandardNames.IS);
      final IContentExpression target = entry.getValue();
      if (target != null)
        target.prettyPrint(disp);
      else
        disp.appendWord("(null)");
    }
    if (deflt != null) {
      disp.append("\n");
      disp.appendWord(StandardNames.DEFAULT);
      deflt.prettyPrint(disp);
    }
    disp.append("\n}");
    disp.popIndent(mark);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitCaseExpression(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformCaseExpression(this, context);
  }
}
