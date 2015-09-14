package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class Otherwise extends Condition
{
  private final ICondition lhs, rhs;

  public Otherwise(Location loc, ICondition lhs, ICondition rhs)
  {
    super(loc);
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public ICondition getLhs()
  {
    return lhs;
  }

  public ICondition getRhs()
  {
    return rhs;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    lhs.prettyPrint(disp);
    disp.appendWord(StandardNames.OTHERWISE);
    rhs.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitOtherwise(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformOtherwise(this, context);
  }
}
