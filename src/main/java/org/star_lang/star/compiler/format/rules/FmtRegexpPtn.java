package org.star_lang.star.compiler.format.rules;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtRegexpPtn implements FmtPtnOp
{

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    if (CompilerUtils.isRegexp(term)) {
      try {
        Pattern.compile(CompilerUtils.regexpExp(term));
        return formatCode.applies;
      } catch (PatternSyntaxException e) {
        return formatCode.notApply;
      }
    } else
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
    disp.append(StandardNames.WFF_REGEXP);
  }

  @Override
  public String toString()
  {
    return StandardNames.WFF_REGEXP;
  }
}
