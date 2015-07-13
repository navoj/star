package org.star_lang.star.operators.string.runtime;

import java.util.Map.Entry;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IScalar;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;

/**
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author fgm
 */

public class ValueDisplay implements IValueVisitor {
  private final PrettyPrintDisplay disp;
  public static final String LIST = "list";
  public static final String HASH = "map";
  public static final String EQUAL = "=";
  public static final String INDEXED = "indexed";
  public static final String RELATION = "relation";

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
