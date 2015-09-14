package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;

@SuppressWarnings("serial")
public class VoidExp extends BaseExpression
{
  public VoidExp(Location loc)
  {
    this(loc, new TypeVar());
  }

  public VoidExp(Location loc, IType type)
  {
    super(loc, type);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("()");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitVoidExp(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformVoidExp(this, context);
  }
}
