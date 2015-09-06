package org.star_lang.star.compiler.macrocompile;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
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
  }

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