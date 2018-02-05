package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class RecordSubstitute extends BaseExpression
{
  private final IContentExpression agg;
  private final IContentExpression sub;

  public RecordSubstitute(Location loc, IType type, IContentExpression route, IContentExpression value)
  {
    super(loc, type);
    this.agg = route;
    this.sub = value;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    agg.prettyPrint(disp);
    disp.appendWord(StandardNames.SUBSTITUTE);
    sub.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitRecordSubstitute(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformRecordSubstitute(this, context);
  }

  public IContentExpression getRoute()
  {
    return agg;
  }

  public IContentExpression getReplace()
  {
    return sub;
  }
}
