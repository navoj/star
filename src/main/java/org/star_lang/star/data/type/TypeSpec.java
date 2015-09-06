package org.star_lang.star.data.type;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
