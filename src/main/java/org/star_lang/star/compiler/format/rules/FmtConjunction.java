package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.format.FormatRanges;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtConjunction implements FmtFormatOp
{
  private final FmtFormatOp lhs;
  private final FmtFormatOp rhs;

  public FmtConjunction(Location loc, FmtFormatOp lhs, FmtFormatOp rhs)
  {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public void format(IAbstract term, Location loc, IAbstract[] env, FormatRanges ranges)
  {
    lhs.format(term, loc, env, ranges);
    rhs.format(term, loc, env, ranges);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    lhs.prettyPrint(disp);
    disp.append(StandardNames.WFF_AND);
    rhs.prettyPrint(disp);
  }
}
