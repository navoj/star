package org.star_lang.star.compiler.format.rules;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.format.FormatRanges;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

/**
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
public class FmtAttributes implements FmtFormatOp
{
  private final Map<String, IAttribute> attributes;

  public FmtAttributes(Location loc, Map<String, IAttribute> attributes)
  {
    this.attributes = attributes;
  }

  @Override
  public void format(IAbstract term, Location loc, IAbstract[] env, FormatRanges ranges)
  {
    for (Entry<String, IAttribute> entry : attributes.entrySet()) {
      String att = entry.getKey();
      IAttribute attribute = entry.getValue();

      ranges.recordFormat(term.getLoc(), att, attribute);
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("{");
    int mark = disp.markIndent(2);
    String sep = "";
    for (Entry<String, IAttribute> entry : attributes.entrySet()) {
      disp.append(sep);
      sep = ";\n";
      disp.appendWord(entry.getKey());
      disp.append(StandardNames.COLON);
      entry.getValue().prettyPrint(disp);
    }
    disp.popIndent(mark);
    disp.append("}");
  }
}
