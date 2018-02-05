package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class PatternAbstraction extends ProgramLiteral
{
  private final IContentExpression result;
  private final IContentPattern match;

  public PatternAbstraction(Location loc, String name, IType type, IContentPattern match, IContentExpression result,
      Variable[] freeVars)
  {
    super(loc, type, name, freeVars);

    this.result = result;
    this.match = match;
  }

  public IContentPattern[] getArgs()
  {
    return new IContentPattern[] { match };
  }

  public IContentPattern getMatch()
  {
    return match;
  }

  public IContentExpression getResult()
  {
    return result;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.PTN);
    disp.appendId(name);
    result.prettyPrint(disp);
    disp.appendWord(StandardNames.FROM);
    match.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitPatternAbstraction(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformPatternAbstraction(this, context);
  }
}
