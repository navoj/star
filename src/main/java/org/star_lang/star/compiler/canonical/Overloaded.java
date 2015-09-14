package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class Overloaded extends BaseExpression
{
  private final IContentExpression inner;
  private final IType dictType;

  public Overloaded(Location loc, IType type, IType dictType, IContentExpression inner)
  {
    super(loc, type);
    this.dictType = dictType;
    this.inner = inner;
  }

  public IContentExpression getInner()
  {
    return inner;
  }

  public IType getDictType()
  {
    return dictType;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitOverloaded(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformOverloaded(this, context);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    inner.prettyPrint(disp);
    disp.append("ø");
  }
}
