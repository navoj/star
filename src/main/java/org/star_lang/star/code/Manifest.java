package org.star_lang.star.code;

import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.value.ResourceURI;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class Manifest implements CodeTree {
  public static final String EXTENSION = "manifest";
  public static final String IMPORTS = "imports";
  public static final String PKGHASH = "pkgHash";

  public static final String entryType = "manifest";
  private final String name;
  private final String pkgFunName;
  private final IType pkgType;
  private final ResourceURI uri;
  private final String pkgHash;

  private final List<ITypeDescription> types;
  private final List<ITypeAlias> aliases;
  private final Map<String, TypeContract> contracts;
  private final List<ResourceURI> imports = new ArrayList<>();

  public Manifest(ResourceURI uri, String name, String pkgHash, List<ITypeDescription> types, List<ITypeAlias> aliases,
                  Map<String, TypeContract> contracts, Iterable<ResourceURI> imports, IType pkgFunType, String pkgFunName) {
    this.uri = uri;
    this.name = name;
    this.pkgHash = pkgHash;
    this.pkgFunName = pkgFunName;
    this.pkgType = pkgFunType;
    this.types = types;
    this.aliases = aliases;
    this.contracts = contracts;
    for (ResourceURI imp : imports)
      this.imports.add(imp);
  }

  // Constructor for Parse interface
  public Manifest() {
    this(null, null, null, null, null, null, null, null, null);
  }

  public ResourceURI getUri() {
    return uri;
  }

  @Override
  public String getPath() {
    return uri.getPath();
  }

  @Override
  public String getExtension() {
    return EXTENSION;
  }

  public String getName() {
    return name;
  }

  public String getPkgFunName() {
    return pkgFunName;
  }

  public IType getPkgType() {
    return pkgType;
  }

  public String getPkgHash() {
    return pkgHash;
  }

  public List<ITypeDescription> getTypes() {
    return types;
  }

  public List<ITypeAlias> getAliases() {
    return aliases;
  }

  public Map<String, TypeContract> getContracts() {
    return contracts;
  }

  public List<ResourceURI> getImports() {
    return imports;
  }

  /**
   * Return an array of names defined by this package. Includes programs, variables and types.
   *
   * @return
   */
  public String[] defines() {
    List<String> defines = new ArrayList<>();

    TypeInterfaceType pkgInterface = (TypeInterfaceType) TypeUtils.deRef(TypeUtils.getFunResultType(TypeUtils
            .unwrap(getPkgType())));

    for (Entry<String, IType> entry : pkgInterface.getAllFields().entrySet())
      if (!defines.contains(entry.getKey()))
        defines.add(entry.getKey());
    for (ITypeDescription desc : types) {
      String name = desc.getName();
      if (!defines.contains(name))
        defines.add(name);
    }
    for (ITypeAlias alias : aliases) {
      String name = alias.getName();
      if (!defines.contains(name))
        defines.add(name);
    }
    return defines.toArray(new String[defines.size()]);
  }

  // generate displayable version of manifest

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.appendId(getName());
    int mark = disp.markIndent(2);
    disp.append(" is manifest{\n");
    disp.append("uri is \"");
    uri.prettyPrint(disp);
    disp.append("\";\n");

    disp.appendWord(PKGHASH);
    disp.append(" is ");
    disp.appendQuoted(pkgHash);
    disp.append("\n");

    disp.appendWord(IMPORTS);
    disp.append(" = [");
    String sep = "";
    for (ResourceURI imp : imports) {
      disp.append(sep);
      sep = ", ";
      disp.appendQuoted(imp.toString());
    }
    disp.append("]\n");

    for (ITypeDescription desc : types) {
      if (!contracts.containsKey(desc.getName())) {
        desc.prettyPrint(disp);
        disp.append(";\n");
      }
    }

    for (ITypeAlias alias : aliases) {
      alias.prettyPrint(disp);
      disp.append(";\n");
    }

    for (Entry<String, TypeContract> con : contracts.entrySet()) {
      con.getValue().prettyPrint(disp);
      disp.append(";\n");
    }

    disp.appendId(getPkgFunName());
    disp.append(" has type ");
    DisplayType.display(disp, getPkgType());
    disp.append(";\n");
    disp.popIndent(mark);
    disp.append("}\n");
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void write(File file) throws IOException {
    try (PrintStream print = new PrintStream(file)) {
      print.append(toString());
    }
  }
}
