package org.star_lang.star.compiler.cafe.compile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.star_lang.star.code.repository.CodeParser;
import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;

/*
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
public class CafeManifest implements CodeTree, CodeParser
{
  public static final String EXTENSION = "cmfest";

  private final ResourceURI src;
  private final Map<String, VarInfo> defs;
  private final Map<String, CafeTypeDescription> types;
  private final Set<ResourceURI> imports;
  private final String javaClassName;

  public CafeManifest(ResourceURI src, String javaClassName)
  {
    this(src, javaClassName, new HashMap<String, VarInfo>(), new TreeSet<ResourceURI>(),
        new HashMap<String, CafeTypeDescription>());
  }

  public CafeManifest(ResourceURI src, String javaClassName, Map<String, VarInfo> defs, Set<ResourceURI> imports,
      Map<String, CafeTypeDescription> types)
  {
    this.defs = defs;
    this.javaClassName = javaClassName;
    this.imports = imports;
    this.types = types;
    this.src = src;

    // check defs
    for (Entry<String, VarInfo> entry : defs.entrySet()) {
      assert entry.getValue().validate() : entry.getKey() + " is not valid";
    }
  }

  public CafeManifest()
  {
    // For parsing CafeManifest files
    this(null, null, new HashMap<String, VarInfo>(), null, null);
  }

  public ResourceURI getSrcUri()
  {
    return src;
  }

  @Override
  public String getPath()
  {
    return javaClassName;
  }

  public Map<String, VarInfo> getDefs()
  {
    return defs;
  }

  public Map<String, CafeTypeDescription> getTypes()
  {
    return types;
  }

  public Map<String, CafeTypeDescription> getCleanedTypes()
  {
    Map<String, CafeTypeDescription> cleanTypes = new HashMap<String, CafeTypeDescription>();

    for (Entry<String, CafeTypeDescription> entry : types.entrySet()) {
      cleanTypes.put(entry.getKey(), entry.getValue().cleanCopy());
    }

    return cleanTypes;
  }

  public void addType(CafeTypeDescription type)
  {
    types.put(type.getName(), type);
  }

  public void redefineType(CafeTypeDescription type)
  {
    if (types.containsKey(type.getName()))
      types.put(type.getName(), type);
  }

  public void addImport(ResourceURI pkg)
  {
    imports.add(pkg);
  }

  public Set<ResourceURI> getImports()
  {
    return imports;
  }

  public void addDefinition(VarInfo var)
  {
    defs.put(var.getName(), var);
  }

  public String getJavaClassName()
  {
    return javaClassName;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendQuoted(getJavaClassName());
    int mark = disp.markIndent(2);
    disp.append(" is cafe{\n");
    disp.append("uri is ");
    disp.appendQuoted(src.toString());
    disp.append(";\n");

    String sep = "";

    for (ResourceURI imp : imports) {
      disp.append(sep);
      sep = ";\n";
      disp.append("import ");
      disp.appendQuoted(imp.toString());
    }

    for (Entry<String, CafeTypeDescription> entry : types.entrySet()) {
      CafeTypeDescription type = entry.getValue();
      if (type.getLoc().getUri().equals(src)) {
        disp.append(sep);
        sep = ";\n";
        type.prettyPrint(disp);
      }
    }

    for (Entry<String, VarInfo> entry : defs.entrySet()) {
      disp.append(sep);
      sep = ";\n";
      entry.getValue().display(disp);
    }

    disp.popIndent(mark);
    disp.append("}\n");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  public void writeText(File file) throws IOException
  {
    PrintStream print = new PrintStream(file);
    String manifest = toString();

    try {
      print.append(manifest);
    } finally {
      print.close();
    }
  }

  @Override
  public void write(File file) throws IOException
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

  @Override
  public CafeManifest parse(ResourceURI uri, ErrorReport errors)
  {
    try (InputStream fis = Resources.getInputStream(uri)) {
      try (ObjectInputStream in = new ObjectInputStream(fis)) {
        return (CafeManifest) in.readObject();
      } catch (IOException ex) {
        errors.reportError("IOException: " + ex.getMessage());
      } catch (ClassNotFoundException ex) {
        errors.reportError("ClassNotFoundException: " + ex.getMessage());
      }
    } catch (IOException ex) {
      errors.reportError("IOException: " + ex.getMessage());
    } catch (ResourceException e) {
      errors.reportError("ResourceException: " + e.getMessage());
    }
    return null;
  }

  @Override
  public CodeTree parse(ResourceURI uri, InputStream stream, ErrorReport errors) throws ResourceException
  {
    try (ObjectInputStream in = new ObjectInputStream(stream)) {
      return (CafeManifest) in.readObject();
    } catch (IOException ex) {
      errors.reportError("IOException: " + ex.getMessage());
    } catch (ClassNotFoundException ex) {
      errors.reportError("ClassNotFoundException: " + ex.getMessage());
    }

    return null;
  }

  @Override
  public String getExtension()
  {
    return CafeManifest.EXTENSION;
  }

}
