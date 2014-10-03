package org.star_lang.star.data.type;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

/**
 * The TypeInterface type expression captures a set of named elements in a type.
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
public class TypeInterfaceType extends AbstractType implements TypeInterface
{
  private final SortedMap<String, IType> fields;
  private final SortedMap<String, IType> types;

  public TypeInterfaceType(SortedMap<String, IType> fields)
  {
    this(new TreeMap<String, IType>(), fields);
  }

  public TypeInterfaceType(SortedMap<String, IType> types, SortedMap<String, IType> fields)
  {
    super(TypeUtils.anonRecordLabel(types, fields), Kind.type);
    this.fields = new TreeMap<>(fields);
    this.types = new TreeMap<>(types);
  }

  public TypeInterfaceType()
  {
    this(new TreeMap<String, IType>());
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
