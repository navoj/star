package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class WherePattern extends ContentPattern
{
  private final IContentPattern ptn;
  private final ICondition cond;

  public WherePattern(Location loc, IContentPattern ptn, ICondition cond)
  {
    super(loc, ptn.getType());
    this.ptn = ptn;
    this.cond = cond;
  }

  public IContentPattern getPtn()
  {
    return ptn;
  }

  public ICondition getCond()
  {
    return cond;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    ptn.prettyPrint(disp);
    disp.appendWord(StandardNames.WHERE);
    cond.prettyPrint(disp);
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitWherePattern(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformWherePattern(this, context);
  }
}
