package org.star_lang.star.compiler.transform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.DisplayAst;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DefinitionKind;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

import com.starview.platform.data.type.Location;

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
public class Definition implements PrettyPrintable
{
  private final Location loc;
  private IAbstract definition;
  private Visibility visibility;
  private final Map<DefinitionKind, String[]> defined = new HashMap<DefinitionKind, String[]>();

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
      definition = Abstract.binary(loc, StandardNames.FATBAR, definition, el);
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