package org.star_lang.star.compiler.canonical;

import java.util.Map.Entry;
import java.util.SortedMap;

import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
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
public class RecordTerm extends BaseExpression
{
  private final IContentExpression fun;
  private final SortedMap<String, IContentExpression> els;
  private final SortedMap<String, IType> types;

  public RecordTerm(Location loc, IType type, IContentExpression fun, SortedMap<String, IContentExpression> els,
      SortedMap<String, IType> types)
  {
    super(loc, type);
    this.fun = fun;
    this.els = els;
    this.types = types;
  }

  public RecordTerm(Location loc, IType type, SortedMap<String, IContentExpression> args, SortedMap<String, IType> types)
  {
    this(loc, type, new Variable(loc, TypeUtils.constructorType(type, type), TypeUtils.anonRecordLabel(type)), args,
        types);
  }

  public SortedMap<String, IType> getTypes()
  {
    return types;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    if (!isAnonRecord())
      fun.prettyPrint(disp);
    disp.append("{ ");
    String sep = "";
    for (Entry<String, IContentExpression> entry : els.entrySet()) {
      disp.append(sep);
      sep = ";\n";

      disp.appendId(entry.getKey());
      disp.append("=");
      entry.getValue().prettyPrint(disp);
    }

    for (Entry<String, IType> entry : types.entrySet()) {
      disp.append(sep);
      sep = ";\n";
      disp.appendWord(StandardNames.TYPE);
      disp.appendId(entry.getKey());
      disp.append("=");
      DisplayType.display(disp, entry.getValue(), Operators.EQUAL_PRIORITY - 1);
    }
    disp.popIndent(mark);
    disp.append("}");
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
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitRecord(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformRecord(this, context);
  }

  public IContentExpression getFun()
  {
    return fun;
  }

  public SortedMap<String, IContentExpression> getArguments()
  {
    return els;
  }

  public static IContentExpression anonRecord(Location loc, IType face,
      SortedMap<String, IContentExpression> els, SortedMap<String,IType> elTypes)
  {
    return new RecordTerm(loc, face, els, elTypes);
  }
}
