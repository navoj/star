package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class PatternApplication extends BasePattern
{
  private final IContentExpression ptn;
  private final IContentPattern arg;

  public PatternApplication(Location loc, IType type, IContentExpression ptn, IContentPattern arg)
  {
    super(loc, type);
    this.ptn = ptn;
    this.arg = arg;
    assert arg != null;
  }

  public PatternApplication(Location loc, IType type, IContentExpression ptn, IContentPattern... args)
  {
    super(loc, type);
    assert ptn!=null && Utils.noNulls(args);
    this.ptn = ptn;
    this.arg = new ConstructorPtn(loc, args);
  }

  public IContentExpression getAbstraction()
  {
    return ptn;
  }

  public IContentPattern getArg()
  {
    return arg;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ptn.prettyPrint(disp);
    arg.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitPatternApplication(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformPatternApplication(this, context);
  }
}
