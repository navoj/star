package org.star_lang.star.compiler.canonical;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
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
public class RecordPtn extends ContentPattern
{
  /**
   * The RecordPtn content expression matches a record value
   */

  private final IContentExpression fun;
  private final Map<String, IContentPattern> elements;
  private final Map<String, Integer> index;

  public RecordPtn(Location loc, IType type, IContentExpression rec, Map<String, IContentPattern> arguments,
      Map<String, Integer> index)
  {
    super(loc, type);
    this.elements = arguments;
    this.fun = rec;
    this.index = index;
  }

  public RecordPtn(Location loc, IType type, Map<String, IContentPattern> arguments, Map<String, Integer> index)
  {
    this(loc, type, new Variable(loc, TypeUtils.constructorType(type, type), TypeUtils.anonRecordLabel(type)),
        arguments, index);
  }

  public Map<String, IContentPattern> getElements()
  {
    return elements;
  }

  public IContentExpression getFun()
  {
    return fun;
  }

  public Map<String, Integer> getIndex()
  {
    return index;
  }

  public boolean isAnonRecord()
  {
    return fun instanceof Variable && TypeUtils.isAnonRecordLabel(((Variable) fun).getName());
  }

  public String anonLabel()
  {
    assert isAnonRecord();
    return ((Variable) fun).getName();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (!isAnonRecord())
      fun.prettyPrint(disp);
    int mark = disp.markIndent(2);
    disp.append("{");
    String sep = "";
    for (Entry<String, IContentPattern> entry : elements.entrySet()) {
      disp.append(sep);
      sep = ";\n";
      disp.appendId(entry.getKey());
      disp.append("=");
      entry.getValue().prettyPrint(disp);
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitRecordPtn(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformRecordPtn(this, context);
  }
}
