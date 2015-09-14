package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class MemoExp extends BaseExpression
{
  private final IContentExpression memo;
  private final IContentExpression[] freeVars;

  public MemoExp(Location loc, IContentExpression memo, IContentExpression[] freeVars)
  {
    super(loc, TypeUtils.functionType(memo.getType()));
    this.memo = memo;
    this.freeVars = freeVars;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.MEMO);
    memo.prettyPrint(disp);
  }

  public IContentExpression getMemo()
  {
    return memo;
  }

  public IContentExpression[] getFreeVars()
  {
    return freeVars;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitMemo(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformMemo(this, context);
  }
}
