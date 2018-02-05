package org.star_lang.star.operators.string.runtime;

import java.util.Map.Entry;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.*;
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

public class ValueDisplay implements IValueVisitor {
  private final PrettyPrintDisplay disp;
  public static final String EQUAL = "=";

  private ValueDisplay(PrettyPrintDisplay disp) {
    this.disp = disp;
  }

  public static String display(IValue term) {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    ValueDisplay displayer = new ValueDisplay(disp);
    term.accept(displayer);
    return disp.toString();
  }

  public static void display(PrettyPrintDisplay disp, IValue term) {
    ValueDisplay displayer = new ValueDisplay(disp);
    term.accept(displayer);
  }

  @Override
  public void visitList(IList list) {
    disp.appendWord("list of [");
    int mark = disp.markIndent(2);
    String sep = "";
    for (IValue el : list) {
      disp.append(sep);
      sep = ", ";
      visit(el);
    }
    disp.popIndent(mark);
    disp.append("]");
  }

  @Override
  public void visitConstructor(IConstructor con) {
    String label = con.getLabel();

    if (!TypeUtils.isTupleLabel(label) && !TypeUtils.isAnonRecordLabel(label))
      disp.appendId(label);
    if (con.size() > 0 || TypeUtils.isTupleLabel(label)) {
      disp.append("(");
      String sep = "";
      for (int ix = 0; ix < con.size(); ix++) {
        disp.append(sep);
        sep = ", ";
        visit(con.getCell(ix));
      }
      disp.append(")");
    }
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
    disp.append(StandardNames.DICTIONARY);
    disp.appendWord(StandardNames.OF);
    disp.append("{");
    int mark = disp.markIndent(2);
    String sep = "";
    for (Entry<IValue, IValue> entry : map) {
      disp.append(sep);
      sep = ";\n";
      visit(entry.getKey());
      disp.append("->");
      visit(entry.getValue());
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  @Override
  public void visitSet(ISet set) {
    disp.append(StandardNames.SET);
    disp.appendWord(StandardNames.OF);
    disp.append("[");
    int mark = disp.markIndent(2);
    String sep = "";
    for (IValue entry : set) {
      disp.append(sep);
      sep = ",\n";
      visit(entry);
    }
    disp.popIndent(mark);
    disp.append("]");
  }

  @Override
  public void visitRecord(IRecord record) {
    if (!TypeUtils.isAnonRecordLabel(record.getLabel()))
      disp.appendId(record.getLabel());
    disp.append("{");
    int mark = disp.markIndent(2);
    String sep = "";
    String fields[] = record.getMembers();
    for (int ix = 0; ix < record.size(); ix++) {
      disp.append(sep);
      sep = ";\n";
      disp.appendId(fields[ix]);
      disp.appendWord(EQUAL);
      disp.append(" ");
      visit(record.getCell(ix));
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  @Override
  public void visitScalar(IScalar<?> scalar) {
    if (scalar instanceof PrettyPrintable)
      ((PrettyPrintable) scalar).prettyPrint(disp);
    else
      disp.append(scalar.getValue().toString());
  }

  private void visit(IValue term) {
    if (term instanceof PrettyPrintable)
      ((PrettyPrintable) term).prettyPrint(disp);
    else if (term != null)
      term.accept(this);
    else
      disp.append("<null>");
  }
}
