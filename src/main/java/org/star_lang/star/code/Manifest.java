package org.star_lang.star.code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

/**
 * Collect together the code and type pieces from a Star source package
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
public class Manifest implements CodeTree{
  public static final String MANIFEST = "manifest";
  public static final String EXTENSION = "manifest";

  public static final String entryType = "manifest";
  private final String name;
  private final String pkgFunName;
  private final IType pkgType;
  private final ResourceURI uri;

  private final List<ITypeDescription> types;
  private final List<ITypeAlias> aliases;
  private final Map<String, TypeContract> contracts;

  public Manifest(ResourceURI uri, String name, List<ITypeDescription> types, List<ITypeAlias> aliases,
      Map<String, TypeContract> contracts, IType pkgFunType, String pkgFunName)
  {
    this.uri = uri;
    this.name = name;
    this.pkgFunName = pkgFunName;
    this.pkgType = pkgFunType;
    this.types = types;
    this.aliases = aliases;
    this.contracts = contracts;
  }

  // Constructor for Parse interface
  public Manifest()
  {
    this(null, null, null, null, null, null, null);
  }

  public ResourceURI getUri()
  {
    return uri;
  }

  @Override
  public String getPath()
  {
    return uri.getPath();
  }

  @Override
  public String getExtension()
  {
    return EXTENSION;
  }

  public String getName()
  {
    return name;
  }

  public String getPkgFunName()
  {
    return pkgFunName;
  }

  public IType getPkgType()
  {
    return pkgType;
  }

  public List<ITypeDescription> getTypes()
  {
    return types;
  }

  public List<ITypeAlias> getAliases()
  {
    return aliases;
  }

  public Map<String, TypeContract> getContracts()
  {
    return contracts;
  }

  /**
   * Return an array of names defined by this package. Includes programs, variables and types.
   * 
   * @return
   */
  public String[] defines()
  {
    List<String> defines = new ArrayList<String>();

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
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(getName());
    int mark = disp.markIndent(2);
    disp.append(" is manifest{\n");
    disp.append("uri is \"");
    uri.prettyPrint(disp);
    disp.append("\";\n");
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
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void write(File file) throws IOException
  {
    PrintStream print = new PrintStream(file);
    String manifest = toString();

    try {
      print.append(manifest);
    } finally {
      print.close();
    }
  }

  public void writeBin(File file) throws IOException
  {
    FileOutputStream fos = new FileOutputStream(file);
    ObjectOutputStream oostream = new ObjectOutputStream(fos);
    try {
      oostream.writeObject(this);
    } finally {
      try {
        oostream.close();
      } catch (IOException e) {
      }
    }
  }
}
