package org.star_lang.star.compiler.transform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.DisplayAst;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.DefinitionKind;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
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
public class Definition implements PrettyPrintable
{
  private final Location loc;
  private IAbstract definition;
  private Visibility visibility;
  private final Map<DefinitionKind, String[]> defined = new HashMap<>();

  public Definition(Location loc, IAbstract definition, String defines[], DefinitionKind kind, Visibility visibility)
  {
    this.loc = loc;
    this.visibility = visibility;
    this.definition = definition;
    defined.put(kind, defines);
    assert Utils.noNulls(defines);
    assert definition != null;
  }

  public Definition(Location loc, IAbstract definition, String name, DefinitionKind kind, Visibility visibility)
  {
    this(loc, definition, new String[] { name }, kind, visibility);
  }

  public Definition(Location loc, IAbstract definition, Map<DefinitionKind, String[]> defines, Visibility visibility)
  {
    this.loc = loc;
    this.visibility = visibility;
    this.definition = definition;
    assert definition != null;
    defined.putAll(defines);
  }

  public boolean defines(String name, DefinitionKind kind)
  {
    String[] defs = defined.get(kind);

    if (defs != null) {
      for (String nm : defs)
        if (nm.equals(name))
          return true;
    }
    return false;
  }

  public boolean isKind(DefinitionKind kind)
  {
    String defs[] = defined.get(kind);
    return defs != null && defs.length > 0;
  }

  public IAbstract getDefinition()
  {
    return definition;
  }

  public String[] getDefines(DefinitionKind kind)
  {
    return defined.get(kind);
  }

  public Collection<DefinitionKind> definedKinds()
  {
    return defined.keySet();
  }

  public Map<DefinitionKind, String[]> defined()
  {
    return defined;
  }

  public Location getLoc()
  {
    return loc;
  }

  public boolean isType()
  {
    String[] types = defined.get(DefinitionKind.type);
    return types != null && types.length > 0;
  }

  public Visibility getVisibility()
  {
    return visibility;
  }

  public void setVisibility(Visibility visibility)
  {
    this.visibility = visibility;
  }

  public void addRule(IAbstract el)
  {
    if (definition == null)
      definition = el;
    else
      definition = Abstract.binary(loc, StandardNames.PIPE, definition, el);
  }

  public IAbstract get()
  {
    return definition;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (visibility == Visibility.priVate)
      disp.append("private ");

    for (Entry<DefinitionKind, String[]> entry : defined.entrySet()) {
      DefinitionKind kind = entry.getKey();

      switch (kind) {
      case contract:
      case implementation:
      case type:
      case variable:
        DisplayAst.display(disp, definition);
        break;
      case java:
        disp.append("java ");
        disp.append(entry.getValue());
        return;
      case imports:
        DisplayAst.display(disp, definition);
        break;
      case constructor:
      case unknown:
        // assert false : "cannot display definition";
      }

      String[] defs = entry.getValue();
      if (defs != null && defs.length > 0) {
        disp.append(":[");
        for (int ix = 0; ix < defs.length; ix++) {
          if (ix > 0)
            disp.append(", ");
          disp.append(defs[ix]);
        }
        disp.append("]");
      }
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}