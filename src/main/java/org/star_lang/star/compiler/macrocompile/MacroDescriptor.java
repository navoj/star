package org.star_lang.star.compiler.macrocompile;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
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
public class MacroDescriptor implements PrettyPrintable
{
  private final String invokeName;
  private final String key;
  private final MacroRuleType type;
  private final IAbstract importVar;
  private final int arity;

  public MacroDescriptor(String key, String invokeName, MacroRuleType type, int arity)
  {
    this(key, null, invokeName, type, arity);
  }

  public MacroDescriptor(String key, IAbstract importVar, String invokeName, MacroRuleType type, int arity)
  {
    this.key = key;
    this.invokeName = invokeName;
    this.type = type;
    this.importVar = importVar;
    this.arity = arity;
  }

  public enum MacroRuleType {
    macroRule, macroVar, quotedFun, builtin, macroBuiltin
  };

  public int getArity()
  {
    return arity;
  }

  public IAbstract getImportVar()
  {
    return importVar;
  }

  public IAbstract getInvokeName(Location loc)
  {
    if (importVar == null)
      return new Name(loc, invokeName);
    else
      return CompilerUtils.fieldExp(loc, importVar, new Name(loc, invokeName));
  }

  public String getKey()
  {
    return key;
  }

  public MacroRuleType type()
  {
    return type;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(key).append(":").appendId(invokeName);
    switch (type) {
    case macroRule:
      disp.append("#");
      break;
    case macroVar:
      disp.append("?");
      break;
    default:
    }
  }
}