package org.star_lang.star.data.value;

import java.util.Map.Entry;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.StringIterator;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.*;
import org.star_lang.star.data.type.StandardTypes;

/**
 * The QuoteDisplay visitor ensures that the value is displayed in a way that enables re-reading the
 * value.
 * <p>
 * Copyright (c) 2015. Francis G. McCabe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class QuoteDisplay implements IValueVisitor {
  private final PrettyPrintDisplay disp;

  private QuoteDisplay(PrettyPrintDisplay disp) {
    this.disp = disp;
  }

  public static String display(IValue term) {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    QuoteDisplay displayer = new QuoteDisplay(disp);
    term.accept(displayer);
    return disp.toString();
  }

  public static void display(PrettyPrintDisplay disp, IValue term) {
    QuoteDisplay displayer = new QuoteDisplay(disp);
    term.accept(displayer);
  }

  @Override
  public void visitList(IList list) {
    disp.appendId(StandardTypes.LIST);
    disp.appendWord(StandardNames.OF);
    disp.append("{");
    int mark = disp.markIndent(2);
    String sep = "";
    for (IValue el : list) {
      disp.append(sep);
      sep = ";\n";
      el.accept(this);
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  @Override
  public void visitConstructor(IConstructor con) {
    if (!TypeUtils.isTupleLabel(con.getLabel()))
      disp.appendId(con.getLabel());
    disp.append("(");
    String sep = "";
    for (int ix = 0; ix < con.size(); ix++) {
      disp.append(sep);
      sep = ", ";
      con.getCell(ix).accept(this);
    }
    disp.append(")");
  }

  @Override
  public void visitFunction(IFunction fn) {
    disp.append("<<function: ");
    DisplayType.display(disp, fn.getType());
    disp.append(">>");
  }

  @Override
  public void visitPattern(IPattern ptn) {
    disp.append("<<pattern: ");
    DisplayType.display(disp, ptn.getType());
    disp.append(">>");
  }

  @Override
  public void visitMap(IMap map) {
    disp.appendWord(StandardNames.DICTIONARY);
    disp.appendWord(StandardNames.OF);
    disp.append("[");
    int mark = disp.markIndent(2);
    String sep = "";
    for (Entry<IValue, IValue> entry : map) {
      disp.append(sep);
      sep = ",\n";
      entry.getKey().accept(this);
      disp.append("->");
      entry.getValue().accept(this);
    }
    disp.popIndent(mark);
    disp.append("]");
  }

  @Override
  public void visitSet(ISet set) {
    disp.appendWord(StandardNames.SET);
    disp.appendWord(StandardNames.OF);
    disp.append("[");
    int mark = disp.markIndent(2);
    String sep = "";
    for (IValue entry : set) {
      disp.append(sep);
      sep = ", ";
      entry.accept(this);
    }
    disp.popIndent(mark);
    disp.append("]");
  }

  @Override
  public void visitRecord(IRecord agg) {
    disp.appendId(agg.getLabel());
    disp.append("{");
    int mark = disp.markIndent(2);
    String sep = "";
    String fields[] = agg.getMembers();
    for (int ix = 0; ix < agg.size(); ix++) {
      disp.append(sep);
      sep = ";\n";
      disp.appendId(fields[ix]);
      disp.appendWord(StandardNames.EQUAL);
      agg.getCell(ix).accept(this);
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  @Override
  public void visitScalar(IScalar<?> scalar) {
    Object value = scalar.getValue();
    if (value instanceof String) {
      String orig = (String) value;
      disp.append("\"");
      for (StringIterator it = new StringIterator(orig); it.hasNext(); )
        StringUtils.strChr(disp, it.next());
      disp.append("\"");
    } else if (value instanceof Long)
      disp.appendWord((Long) value);
    else if (value instanceof PrettyPrintable)
      ((PrettyPrintable) value).prettyPrint(disp);
    else
      disp.appendWord(value.toString());
  }
}
