package org.star_lang.star.compiler.format.rules;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.format.FormatRanges;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
