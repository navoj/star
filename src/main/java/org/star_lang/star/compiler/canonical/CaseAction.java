package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

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
