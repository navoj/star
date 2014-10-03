package org.star_lang.star.data.type;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
public class TypeSpec implements TypeSpecification
{
  private final Type typeSpec;

  public TypeSpec(Type spec)
  {
    this.typeSpec = spec;
  }

  @Override
  public String getName()
  {
    return typeSpec.typeLabel();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(getName());
    disp.appendWord(StandardNames.HAS_KIND);
    showKind(disp, typeSpec);
  }

  private static void showKind(PrettyPrintDisplay disp, Type type)
  {
    switch (type.getArity()) {
    case 0:
      disp.appendWord(StandardNames.TYPE);
      return;
    case 1:
      disp.appendWord(StandardNames.TYPE);
      disp.appendWord(StandardNames.OF);
      disp.appendWord(StandardNames.TYPE);
      return;
    default:
      disp.appendWord(StandardNames.TYPE);
      disp.appendWord(StandardNames.OF);
      disp.append("(");
      String sep = "";
      for (int ix = 0; ix < type.getArity(); ix++) {
        disp.append(sep);
        sep = ",";
        disp.appendWord(StandardNames.TYPE);
      }
      disp.append(")");
    }
  }

  @Override
  public Kind kind()
  {
    return typeSpec.kind();
  }

  @Override
  public int typeArity()
  {
    return typeSpec.getArity();
  }

  @Override
  public IType getType()
  {
    return typeSpec;
  }
}
