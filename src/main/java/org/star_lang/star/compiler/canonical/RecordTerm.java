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
