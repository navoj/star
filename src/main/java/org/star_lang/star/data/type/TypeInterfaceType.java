package org.star_lang.star.data.type;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

/**
 * The TypeInterface type expression captures a set of named elements in a type.
 *
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
public class TypeInterfaceType extends AbstractType implements TypeInterface
{
  private final SortedMap<String, IType> fields;
  private final SortedMap<String, IType> types;

  public TypeInterfaceType(SortedMap<String, IType> fields)
  {
    this(new TreeMap<>(), fields);
  }

  public TypeInterfaceType(SortedMap<String, IType> types, SortedMap<String, IType> fields)
  {
    super(TypeUtils.anonRecordLabel(types, fields), Kind.type);
    this.fields = new TreeMap<>(fields);
    this.types = new TreeMap<>(types);
  }

  public TypeInterfaceType()
  {
    this(new TreeMap<>());
  }

  @Override
  public <C> void accept(ITypeVisitor<C> visitor, C cxt)
  {
    visitor.visitTypeInterface(this, cxt);
  }

  @Override
  public <T, C, X> T transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformTypeInterface(this, cxt);
  }

  @Override
  public int numOfFields()
  {
    return fields.size();
  }

  @Override
  public IType getFieldType(String name)
  {
    return fields.get(name);
  }

  @Override
  public SortedMap<String, IType> getAllFields()
  {
    return fields;
  }

  public int arity()
  {
    return fields.size();
  }

  @Override
  public int numOfTypes()
  {
    return types.size();
  }

  @Override
  public IType getType(String name)
  {
    return types.get(name);
  }

  @Override
  public SortedMap<String, IType> getAllTypes()
  {
    return types;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (fields.size() + types.size() == 0)
      disp.append("{}");
    else {
      DisplayType tpDisp = new DisplayType(disp);

      disp.append("{");
      int mark = disp.markIndent(2);
      String sep = "";

      for (Entry<String, IType> entry : types.entrySet()) {
        disp.append(sep);
        sep = ";\n";
        disp.appendWord(StandardNames.TYPE);
        disp.appendId(entry.getKey());
        disp.appendWord("=");
        entry.getValue().accept(tpDisp, null);
      }

      for (Entry<String, IType> field : fields.entrySet()) {
        disp.append(sep);
        sep = ";\n";
        disp.appendId(field.getKey());
        disp.append(" has type ");
        field.getValue().accept(tpDisp, null);
      }

      disp.popIndent(mark);
      disp.append("}");
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public int hashCode()
  {
    return fields.hashCode() * 37 + types.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final TypeInterfaceType other = (TypeInterfaceType) obj;
    if (fields == null) {
      if (other.fields != null)
        return false;
    } else if (!fields.equals(other.fields))
      return false;
    if (types == null)
      return other.types == null;
    else
      return types.equals(other.types);
  }
}
