package org.star_lang.star.compiler.canonical;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
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
