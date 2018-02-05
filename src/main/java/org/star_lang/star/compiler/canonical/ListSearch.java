package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class ListSearch extends Condition
{
  private final IContentPattern ptn;
  private final IContentPattern ixPtn;
  private final IContentExpression src;

  public ListSearch(Location loc, IContentPattern ptn, IContentPattern ixPtn, IContentExpression src)
  {
    super(loc);
    this.ptn = ptn;
    this.ixPtn = ixPtn;
    this.src = src;
  }

  public IContentPattern getPtn()
  {
    return ptn;
  }

  public IContentPattern getIx()
  {
    return ixPtn;
  }

  public IContentExpression getSource()
  {
    return src;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ptn.prettyPrint(disp);
    disp.append("[");
    ixPtn.prettyPrint(disp);
    disp.append("] ");
    disp.appendWord(StandardNames.IN);
    src.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitListSearch(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformListSearch(this, context);
  }
}
