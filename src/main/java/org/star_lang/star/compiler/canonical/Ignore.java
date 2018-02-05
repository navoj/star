package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

@SuppressWarnings("serial")
public class Ignore extends Action
{
  private final IContentExpression ignored;

  public Ignore(Location loc, IContentExpression ignored)
  {
    super(loc, StandardTypes.unitType);
    this.ignored = ignored;
  }

  public IContentExpression getIgnored()
  {
    return ignored;
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitIgnored(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.IGNORE);
    ignored.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitIgnored(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformIgnored(this, context);
  }

}
