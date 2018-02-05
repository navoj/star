package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

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
