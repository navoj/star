package org.star_lang.star.compiler.canonical;

import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class OverloadedFieldAccess extends FieldAccess
{
  private final IType dictType;

  public OverloadedFieldAccess(Location loc, IType type, IType dictType, IContentExpression record, String field)
  {
    super(loc, type, record, field);
    this.dictType = dictType;
  }

  public IType getDictType()
  {
    return dictType;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitOverloadedFieldAccess(this);
  }
 

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformOverloadedFieldAccess(this, context);
  }
}
