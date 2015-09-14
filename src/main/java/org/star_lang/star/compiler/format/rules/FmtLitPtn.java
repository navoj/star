package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.Display;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtLitPtn implements FmtPtnOp
{
  private final IAbstract lit;

  public FmtLitPtn(IAbstract lit)
  {
    this.lit = lit;
  }

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    if (lit.equals(term))
      return formatCode.applies;
    else
      return formatCode.notApply;
  }

  @Override
  public int getSpecificity()
  {
    return 1;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    Display.display(disp, lit);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
