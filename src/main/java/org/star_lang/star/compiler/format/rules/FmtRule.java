package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.format.FormatRanges;
import org.star_lang.star.compiler.format.rules.FmtPtnOp.formatCode;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.Location;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
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
