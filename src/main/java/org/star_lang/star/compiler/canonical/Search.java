package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class Search extends Condition
{
  private final IContentPattern ptn;
  private final IContentExpression source;

  public Search(Location loc, IContentPattern lhs, IContentExpression rhs)
  {
    super(loc);
    this.ptn = lhs;
    this.source = rhs;
  }

  public IContentPattern getPtn()
  {
    return ptn;
  }

  public IContentExpression getSource()
  {
    return source;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ptn.prettyPrint(disp);
    disp.appendWord(StandardNames.IN);
    source.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitPredication(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformSearch(this, context);
  }
}
