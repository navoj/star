package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.value.ResourceURI;

import java.util.List;
import java.util.Set;
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
public class PackageTerm implements Canonical {
  private final String name;
  private final String pkgName;
  private final IType pkgType;
  private final List<TypeDefinition> types;
  private final List<ITypeAlias> aliases;
  private final List<TypeContract> contracts;
  private final Set<ResourceURI> imports;
  private final Location loc;
  private final IContentExpression pkgValue;
  private final ResourceURI uri;

  public PackageTerm(Location loc, String name, String pkgName, IType pkgType, IContentExpression pkgValue,
                     List<TypeDefinition> types, List<ITypeAlias> aliases, List<TypeContract> contracts,
                     Set<ResourceURI> imports, ResourceURI uri) {
    this.loc = loc;
    this.name = name;
    this.pkgName = pkgName;
    this.pkgType = pkgType;
    this.pkgValue = pkgValue;
    this.types = types;
    this.aliases = aliases;
    this.contracts = contracts;
    this.imports = imports;
    this.uri = uri;
  }

  @Override
  public Location getLoc() {
    return loc;
  }

  public String getName() {
    return name;
  }

  public String getPkgName() {
    return pkgName;
  }

  public IType getPkgType() {
    return pkgType;
  }

  public List<TypeDefinition> getTypes() {
    return types;
  }

  public List<ITypeAlias> getAliases() {
    return aliases;
  }

  public List<TypeContract> getContracts() {
    return contracts;
  }

  public IContentExpression getPkgValue() {
    return pkgValue;
  }

  public Set<ResourceURI> getImports() {
    return imports;
  }

  public ResourceURI getUri() {
    return uri;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.append("package: ").append(pkgName).append("\n");

    pkgValue.prettyPrint(disp);

    disp.append("\n  ");
    int mark = disp.markIndent();

    for (TypeDefinition entry : types) {
      entry.prettyPrint(disp);
      disp.append("\n");
    }

    for (ITypeAlias entry : aliases) {
      entry.prettyPrint(disp);
      disp.append("\n");
    }

    disp.popIndent(mark);
    disp.append("\n");
    disp.appendWord("}");
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    assert false : "should not have done this";
  }
}
