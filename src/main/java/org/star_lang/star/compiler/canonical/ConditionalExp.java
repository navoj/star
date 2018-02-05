package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class ConditionalExp extends BaseExpression
{
  private final ICondition cnd;
  private final IContentExpression thExp;
  private final IContentExpression elExp;

  public ConditionalExp(Location loc, IType type, ICondition cnd, IContentExpression thExp, IContentExpression elExp)
  {
    super(loc, type);
    this.cnd = cnd;
    this.thExp = thExp;
    this.elExp = elExp;
  }

  public ICondition getCnd()
  {
    return cnd;
  }

  public IContentExpression getThExp()
  {
    return thExp;
  }

  public IContentExpression getElExp()
  {
    return elExp;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("(");
    showCond(this, disp);
    disp.append(")");
    disp.popIndent(mark);
  }

  private static void showCond(IContentExpression exp, PrettyPrintDisplay disp)
  {
    while (exp instanceof ConditionalExp) {
      ConditionalExp cond = (ConditionalExp) exp;
      cond.getCnd().prettyPrint(disp);
      disp.append(StandardNames.QUESTION);
      int mark = disp.markIndent(2);
      disp.append("\n");
      cond.getThExp().prettyPrint(disp);
      disp.append(StandardNames.COLON);
      disp.popIndent(mark);
      disp.append("\n");
      exp = cond.getElExp();
    }
    exp.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitConditionalExp(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformConditionalExp(this, context);
  }
}
