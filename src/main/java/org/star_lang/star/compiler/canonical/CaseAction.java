package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

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
public class CaseAction extends Action
{
  private final IContentExpression selector;
  private final List<Pair<IContentPattern, IContentAction>> cases;
  private final IContentAction deflt;

  public CaseAction(Location loc, IContentExpression selector, List<Pair<IContentPattern, IContentAction>> cases,
      IContentAction deflt)
  {
    super(loc, computeResultType(cases, deflt));
    this.selector = selector;
    this.cases = cases;
    this.deflt = deflt;
    assert !cases.isEmpty();
  }

  public IContentExpression getSelector()
  {
    return selector;
  }

  public List<Pair<IContentPattern, IContentAction>> getCases()
  {
    return cases;
  }

  public IContentAction getDeflt()
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
    for (Pair<IContentPattern, IContentAction> entry : getCases()) {
      disp.append(sep);
      sep = ";\n";
      entry.getKey().prettyPrint(disp);
      disp.append(StandardNames.MAP_ARROW);
      final IContentAction target = entry.getValue();
      if (target != null)
        target.prettyPrint(disp);
      else
        disp.appendWord("(null)");
    }
    if (deflt != null) {
      disp.append("\n");
      disp.append(StandardNames.DEFAULT);
      deflt.prettyPrint(disp);
    }
    disp.append("\n}");
    disp.popIndent(mark);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitCaseAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitCaseAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformCaseAction(this, context);
  }

  private static IType computeResultType(List<Pair<IContentPattern, IContentAction>> cases, IContentAction deflt)
  {
    for (Pair<IContentPattern, IContentAction> p : cases) {
      if (!p.right().getType().equals(StandardTypes.unitType)) {
        return p.right().getType();
      }
    }
    return deflt.getType();
  }
}
