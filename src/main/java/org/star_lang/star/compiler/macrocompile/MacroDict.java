package org.star_lang.star.compiler.macrocompile;

import java.util.HashMap;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.Dict;

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
public class MacroDict extends Dict<MacroDescriptor>
{
  final IAbstract replaceVar;

  protected MacroDict(IAbstract replaceVar, MacroDict outer)
  {
    super(outer, new HashMap<>());
    this.replaceVar = replaceVar;
  }

  public MacroDict(IAbstract replaceVar)
  {
    this(replaceVar, null);
  }

  public IAbstract getReplaceVar()
  {
    return replaceVar;
  }

  public boolean isReplaceVar(IAbstract v)
  {
    if (v.equals(replaceVar))
      return true;
    else if (getOuter() != null)
      return ((MacroDict) getOuter()).isReplaceVar(v);
    else
      return false;
  }

  @Override
  public MacroDict fork()
  {
    return new MacroDict(replaceVar, this);
  }

  public MacroDict fork(IAbstract replaceVar)
  {
    return new MacroDict(replaceVar, this);
  }
}
