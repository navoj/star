package com.starview.platform.data.value;

import java.util.Map.Entry;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.StringIterator;
import org.star_lang.star.compiler.util.StringUtils;

import com.starview.platform.data.IConstructor;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IList;
import com.starview.platform.data.IMap;
import com.starview.platform.data.IPattern;
import com.starview.platform.data.IRecord;
import com.starview.platform.data.IRelation;
import com.starview.platform.data.IScalar;
import com.starview.platform.data.IValue;
import com.starview.platform.data.IValueVisitor;
import com.starview.platform.data.type.StandardTypes;

/**
 * The QuoteDisplay visitor ensures that the value is displayed in a way that enables re-reading the
 * value.
 * 
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
public class QuoteDisplay implements IValueVisitor
{
  private final PrettyPrintDisplay disp;

  private QuoteDisplay(PrettyPrintDisplay disp)
  {
    this.disp = disp;
  }

  public static String display(IValue term)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    QuoteDisplay displayer = new QuoteDisplay(disp);
    term.accept(displayer);
    return disp.toString();
  }

  public static void display(PrettyPrintDisplay disp, IValue term)
  {
    QuoteDisplay displayer = new QuoteDisplay(disp);
    term.accept(displayer);
  }

  @Override
  public void visitList(IList list)
  {
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
  public void visitConstructor(IConstructor con)
  {
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
  public void visitFunction(IFunction fn)
  {
    disp.append("<<function: ");
    DisplayType.display(disp, fn.getType());
    disp.append(">>");
  }

  @Override
  public void visitPattern(IPattern ptn)
  {
    disp.append("<<pattern: ");
    DisplayType.display(disp, ptn.getType());
    disp.append(">>");
  }

  @Override
  public void visitMap(IMap map)
  {
    disp.appendWord(StandardNames.MAP);
    disp.appendWord(StandardNames.OF);
    disp.append("{");
    int mark = disp.markIndent(2);
    String sep = "";
    for (Entry<IValue, IValue> entry : map) {
      disp.append(sep);
      sep = ";\n";
      entry.getKey().accept(this);
      disp.append("->");
      entry.getValue().accept(this);
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  @Override
  public void visitRecord(IRecord agg)
  {
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
  public void visitRelation(IRelation relation)
  {
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.RELATION);
    disp.appendWord("{");
    String sep = "\n";
    for (IValue tuple : relation) {
      disp.append(sep);
      sep = ";\n";
      tuple.accept(this);
    }
    disp.popIndent(mark);
    disp.append("\n");
    disp.appendWord("}");
  }

  @Override
  public void visitScalar(IScalar<?> scalar)
  {
    Object value = scalar.getValue();
    if (value instanceof String) {
      String orig = (String) value;
      disp.append("\"");
      for (StringIterator it = new StringIterator(orig); it.hasNext();)
        StringUtils.strChr(disp, it.next());
      disp.append("\"");
    } else if (value instanceof Long)
      disp.appendWord((Long) value);
    else if (scalar.getType().equals(StandardTypes.charType)) {
      disp.appendChar('\'');
      disp.appendChar((Integer) value);
      disp.appendChar('\'');
    } else if (value instanceof PrettyPrintable)
      ((PrettyPrintable) value).prettyPrint(disp);
    else
      disp.appendWord(value.toString());
  }
}
