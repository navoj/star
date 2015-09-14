package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class IsTrue extends Condition
{
  private final IContentExpression exp;

  public IsTrue(Location loc, IContentExpression exp)
  {
    super(loc);
    this.exp = exp;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    exp.prettyPrint(disp);
  }

  public IContentExpression getExp()
  {
    return exp;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitIsTrue(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformIsTrue(this, context);
  }
}
