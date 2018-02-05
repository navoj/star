package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class Matches extends Condition
{
  private final IContentExpression exp;
  private final IContentPattern ptn;

  /**
   * The Matches condition encodes a matches predicate
   * 
   * @param lhs
   *          the expression being matched
   * @param rhs
   *          the pattern being used to match
   */
  public Matches(Location loc, IContentExpression lhs, IContentPattern rhs)
  {
    super(loc);
    this.exp = lhs;
    this.ptn = rhs;
  }

  public IContentExpression getExp()
  {
    return exp;
  }

  public IContentPattern getPtn()
  {
    return ptn;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    exp.prettyPrint(disp);
    disp.appendWord(StandardNames.MATCHES);
    ptn.prettyPrint(disp);
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitMatches(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformMatches(this, context);
  }
}
