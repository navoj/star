package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class NullExp extends BaseExpression
{
  public NullExp(Location loc, IType type)
  {
    super(loc, type);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformNullExp(this, context);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitNullExp(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(Names.NULL_PTN);
  }

}
