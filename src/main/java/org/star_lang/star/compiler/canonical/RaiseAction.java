package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

@SuppressWarnings("serial")
public class RaiseAction extends Action
{
  private final IContentExpression raise;

  public RaiseAction(Location loc, IContentExpression raise)
  {
    super(loc, StandardTypes.unitType);
    this.raise = raise;
  }

  public IContentExpression getRaised()
  {
    return raise;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.RAISE);
    raise.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitRaiseAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitRaiseAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformRaiseAction(this, context);
  }
}
