package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

@SuppressWarnings("serial")
public class ContentCondition extends BaseExpression
{
  private final ICondition condition;

  public ContentCondition(Location loc, ICondition condition)
  {
    super(loc, StandardTypes.booleanType);
    this.condition = condition;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    condition.prettyPrint(disp);
  }

  public ICondition getCondition()
  {
    return condition;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitContentCondition(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformContentCondition(this, context);
  }
}
