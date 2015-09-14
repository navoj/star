package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.Display;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtLiteral implements FmtBuildOp
{
  private final IAbstract lit;

  public FmtLiteral(IAbstract lit)
  {
    this.lit = lit;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc)
  {
    return lit;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    Display.display(disp, lit);
  }
}
