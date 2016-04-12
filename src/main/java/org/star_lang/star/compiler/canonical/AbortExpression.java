package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class AbortExpression extends BaseExpression
{
  IContentExpression raised;

  public AbortExpression(Location loc, IType type, IContentExpression raised)
  {
    super(loc, type);
    this.raised = raised;
  }

  public IContentExpression getAbort()
  {
    return raised;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.RAISE);
    raised.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitRaiseExpression(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformRaiseExpression(this, context);
  }
}
