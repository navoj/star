package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FalseCondition extends Condition
{
  public FalseCondition(Location loc)
  {
    super(loc);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.FALSE);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitFalseCondition(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformFalseCondition(this, context);
  }
}
