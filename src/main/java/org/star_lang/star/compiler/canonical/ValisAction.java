package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class ValisAction extends Action
{
  private final IContentExpression value;

  public ValisAction(Location loc, IContentExpression value)
  {
    super(loc, value.getType());
    this.value = value;
  }

  public IContentExpression getValue()
  {
    return value;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.VALIS);
    value.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitValisAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitValisAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformValisAction(this, context);
  }
}
