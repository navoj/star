package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class CastExpression extends BaseExpression
{
  private final IContentExpression inner;

  public CastExpression(Location loc, IType type, IContentExpression inner)
  {
    super(loc, type);
    this.inner = inner;
  }

  public IContentExpression getInner()
  {
    return inner;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    inner.prettyPrint(disp);
    disp.appendWord(StandardNames.CAST);
    DisplayType.display(disp, getType());
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitCastExpression(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformCastExpression(this, context);
  }
}
