package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class MatchingPattern extends ContentPattern
{
  private final IContentPattern ptn;
  private final Variable var;

  public MatchingPattern(Location loc, Variable var, IContentPattern ptn)
  {
    super(loc, ptn.getType());
    this.ptn = ptn;
    this.var = var;
  }

  public IContentPattern getPtn()
  {
    return ptn;
  }

  public Variable getVar()
  {
    return var;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    var.prettyPrint(disp);
    disp.appendWord(StandardNames.MATCHING);
    ptn.prettyPrint(disp);
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitMatching(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformMatchingPtn(this, context);
  }
}
