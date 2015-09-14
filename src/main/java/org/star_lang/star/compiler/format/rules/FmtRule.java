package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.format.FormatRanges;
import org.star_lang.star.compiler.format.rules.FmtPtnOp.formatCode;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FmtRule implements PrettyPrintable
{
  private final int varCount;
  private final FmtPtnOp ptn;
  private final FmtFormatOp body;
  private final String category;
  private final Location loc;
  private final boolean isDefault;

  public FmtRule(Location loc, int varCount, String category, FmtPtnOp ptn, FmtFormatOp rep)
  {
    this(loc, varCount, category, ptn, rep, false);
  }

  public FmtRule(Location loc, int varCount, String category, FmtPtnOp ptn, FmtFormatOp rep, boolean isDefault)
  {
    this.varCount = varCount;
    this.category = category;
    this.ptn = ptn;
    this.body = rep;
    this.loc = loc;
    this.isDefault = isDefault;
  }

  public formatCode applyRule(IAbstract term, FormatRanges formats)
  {
    IAbstract vars[] = new IAbstract[varCount];
    final Location loc = term.getLoc();

    if (term.isCategory(category) && ptn.apply(term, vars, loc) == formatCode.applies) {
      body.format(term, loc, vars, formats);
      return formatCode.applies;
    } else
      return formatCode.notApply;
  }

  public int getVarCount()
  {
    return varCount;
  }

  public String getCategory()
  {
    return category;
  }

  public Location getLoc()
  {
    return loc;
  }

  public boolean isDefault()
  {
    return isDefault;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ptn.prettyPrint(disp);
    disp.append(StandardNames.WFF_DEFINES);
    disp.appendWord(category);

    disp.append(StandardNames.FMT_RULE);
    body.prettyPrint(disp);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
