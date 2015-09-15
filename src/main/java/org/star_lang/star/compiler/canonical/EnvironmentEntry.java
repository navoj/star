/**
 *
 */
package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.*;

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
public abstract class EnvironmentEntry implements IStatement
{
  final Location loc;
  final private Visibility visibility;

  public EnvironmentEntry(Location loc, Visibility visibility)
  {
    this.loc = loc;
    this.visibility = visibility;
  }

  @Override
  public String toString()
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    prettyPrint(disp);
    disp.append("@");
    loc.prettyPrint(disp);
    return disp.toString();
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public Visibility getVisibility()
  {
    return visibility;
  }

  public static EnvironmentEntry createTypeEntry(String name, Location loc, IType type, IAlgebraicType algebraicType,
      Visibility visibility, boolean imported, boolean fromContract)
  {
    return new TypeDefinition(name, loc, type, algebraicType, visibility, imported, fromContract);
  }

  public static EnvironmentEntry createTypeAliasEntry(String name, Location loc, ITypeAlias typeAlias,
      Visibility visibility)
  {
    return new TypeAliasEntry(name, loc, typeAlias, visibility);
  }

  public static ContractEntry createContractEntry(Location loc, String name, TypeContract contract,
      Visibility visibility)
  {
    return new ContractEntry(name, loc, contract, visibility);
  }

}
