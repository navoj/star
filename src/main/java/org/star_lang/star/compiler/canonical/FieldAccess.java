package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FieldAccess extends BaseExpression
{
  private final IContentExpression record;
  private final String field;

  public FieldAccess(Location loc, IType type, IContentExpression record, String field)
  {
    super(loc, type);
    this.field = field;
    this.record = record;
  }

  public IContentExpression getRecord()
  {
    return record;
  }

  public String getField()
  {
    return field;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitFieldAccess(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformFieldAccess(this, context);
  }

  public static IContentExpression create(Location loc, IType type, IContentExpression record, String name)
  {
    if (TypeUtils.hasContractDependencies(type) && !TypeUtils.isConstructorType(TypeUtils.unwrap(type))
        && !(record instanceof Resolved))
      return new OverloadedFieldAccess(loc, type, Over.computeDictionaryType(type, loc, AccessMode.readOnly),
          record, name);
    else
      return new FieldAccess(loc, type, record, name);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    record.prettyPrint(disp);
    disp.append(".");
    disp.appendId(field);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (obj instanceof FieldAccess) {
      FieldAccess other = (FieldAccess) obj;
      return other.getField().equals(getField()) && other.getRecord().equals(getRecord());
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return ((".".hashCode() * 37 + getField().hashCode()) * 43) + getRecord().hashCode();
  }
}
