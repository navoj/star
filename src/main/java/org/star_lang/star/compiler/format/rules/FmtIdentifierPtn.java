package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtIdentifierPtn implements FmtPtnOp
{

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    if (term instanceof Name && !StandardNames.isKeyword(term))
      return formatCode.applies;
    else
      return formatCode.notApply;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.WFF_IDENTIFIER);
  }

  @Override
  public int getSpecificity()
  {
    return 1;
  }

  @Override
  public String toString()
  {
    return StandardNames.WFF_IDENTIFIER;
  }
}
