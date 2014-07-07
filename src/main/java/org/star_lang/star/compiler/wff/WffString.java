package org.star_lang.star.compiler.wff;

import java.util.List;

import org.star_lang.star.compiler.ast.Display;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
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
public class WffString implements WffBuildOp
{
  private final List<WffBuildOp> elements;

  public WffString(List<WffBuildOp> elements)
  {
    this.elements = elements;
  }

  @Override
  public IAbstract build(IAbstract[] env, Location loc, WffEngine engine)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    for (WffBuildOp el : elements) {
      IAbstract trm = el.build(env, loc, engine);
      if (trm instanceof StringLiteral)
        disp.append(((StringLiteral) trm).getLit());
      else if (trm instanceof Name)
        disp.append(((Name) trm).getId());
      else
        Display.display(disp, trm);
    }
    return new StringLiteral(loc, disp.toString());
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    String sep = "";
    for (WffBuildOp el : elements) {
      disp.append(sep);
      sep = StandardNames.STRING_CATENATE;
      el.prettyPrint(disp);
    }
  }

}
