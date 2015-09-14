package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.format.FormatRanges;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtPtnNull implements FmtPtnOp, FmtFormatOp
{

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    return formatCode.applies;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {

  }

  @Override
  public int getSpecificity()
  {
    return 0;
  }

  @Override
  public String toString()
  {
    return "";
  }

  @Override
  public void format(IAbstract term, Location loc, IAbstract[] env, FormatRanges ranges)
  {
  }
}
