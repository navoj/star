package org.star_lang.star.compiler.generate;

import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.type.BindingKind;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.IType;
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

public class DictEntry
{
  private final String name;
  final AccessMode access;
  private final Variable var;
  private final Location loc;
  private final BindingKind where;

  public DictEntry(String name, Variable var, Location loc, AccessMode access, BindingKind where)
  {
    this.name = name;
    this.access = access;
    this.var = var;
    this.where = where;
    this.loc = loc;
  }

  public Variable getVariable()
  {
    return var;
  }

  public String getName()
  {
    return name;
  }

  public AccessMode getAccess()
  {
    return access;
  }

  public BindingKind getBindingKind()
  {
    return where;
  }

  public IType getType()
  {
    return var.getType();
  }

  public Location getLoc()
  {
    return loc;
  }
}