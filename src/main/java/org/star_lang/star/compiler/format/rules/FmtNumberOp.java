package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtNumberOp implements FmtPtnOp
{

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    if (term instanceof IntegerLiteral || term instanceof FloatLiteral || term instanceof LongLiteral
        || term instanceof BigDecimalLiteral)
      return formatCode.applies;
    else
      return formatCode.notApply;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.WFF_NUMBER);
  }

  @Override
  public int getSpecificity()
  {
    return 0;
  }

  @Override
  public String toString()
  {
    return StandardNames.WFF_NUMBER;
  }
}
