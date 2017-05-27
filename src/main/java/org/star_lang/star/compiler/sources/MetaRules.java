package org.star_lang.star.compiler.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.star_lang.star.code.repository.CodeParser;
import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.wff.WffProgram;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;

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

/**
 * Collect together the meta rules from a Star source package
 * 
 */

@SuppressWarnings("serial")
public class MetaRules implements CodeTree, CodeParser {
  public static final String MANIFEST = "metarules";
  public static final String EXTENSION = "metarules";

  public static final String entryType = "metaRules";
  private final ResourceURI uri;
  private final String name;
  private final Operators operators;
  private final String macroEntry;
  private final WffProgram wffRules;
  private final List<ResourceURI> imports;

  // Constructor for use as a MetaRulesParser
  public MetaRules() {
    this(null, null, null, null, null, null);
  }

  public MetaRules(ResourceURI uri, String name, List<ResourceURI> imports, Operators operators, String macroEntry,
      WffProgram wffRules) {
    this.uri = uri;
    this.name = name;
    this.macroEntry = macroEntry;
    this.wffRules = wffRules;
    this.operators = operators;
    this.imports = imports;
  }

  @Override
  public String getPath() {
    return uri.getPath();
  }

  public String getName() {
    return name;
  }

  public String getMacroEntry() {
    return macroEntry;
  }

  public Operators getOperators() {
    return operators;
  }

  public WffProgram getWffRules() {
    return wffRules;
  }

  public List<ResourceURI> getImports() {
    return imports;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.appendId(getName());
    int mark = disp.markIndent(2);
    disp.append(" is meta{\n");

    operators.prettyPrint(disp);
    wffRules.prettyPrint(disp);
    disp.popIndent(mark);
    disp.append("}\n");
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void write(File file) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(file)) {
      try (ObjectOutputStream oostream = new ObjectOutputStream(fos)) {
        oostream.writeObject(this);
      }
    }
  }

  @Override
  public CodeTree parse(ResourceURI uri, ErrorReport errors) {
    MetaRules newMetaRules = null;
    try (InputStream fis = Resources.getInputStream(uri)) {
      return parse(uri, fis, errors);
    } catch (ResourceException | IOException e) {
      errors.reportError("Resource problem: " + e.getMessage());
    }
    return newMetaRules;
  }

  @Override
  public CodeTree parse(ResourceURI uri, InputStream input, ErrorReport errors) throws ResourceException {
    try (ObjectInputStream in = new ObjectInputStream(input)) {
      return (MetaRules) in.readObject();
    } catch (IOException ex) {
      errors.reportError("IOException: " + ex.getMessage());
    } catch (ClassNotFoundException ex) {
      errors.reportError("ClassNotFoundException: " + ex.getMessage());
    }

    return null;
  }

  @Override
  public String getExtension() {
    return EXTENSION;
  }
}
