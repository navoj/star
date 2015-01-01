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
import org.star_lang.star.compiler.format.rules.FmtProgram;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.wff.WffProgram;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;

/**
 * Collect together the meta rules from a Star source package
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
public class MetaRules implements CodeTree, CodeParser
{
  public static final String MANIFEST = "metarules";
  public static final String EXTENSION = "metarules";

  public static final String entryType = "metaRules";
  private final ResourceURI uri;
  private final String name;
  private final Operators operators;
  private final String macroEntry;
  private final WffProgram wffRules;
  private final FmtProgram fmtRules;
  private final List<ResourceURI> imports;

  // Constructor for use as a MetaRulesParser
  public MetaRules()
  {
    this(null, null, null, null, null, null, null);
  }

  public MetaRules(ResourceURI uri, String name, List<ResourceURI> imports, Operators operators, String macroEntry,
      WffProgram wffRules, FmtProgram fmtRules)
  {
    this.uri = uri;
    this.name = name;
    this.macroEntry = macroEntry;
    this.wffRules = wffRules;
    this.fmtRules = fmtRules;
    this.operators = operators;
    this.imports = imports;
  }

  @Override
  public String getPath()
  {
    return uri.getPath();
  }

  public String getName()
  {
    return name;
  }

  public String getMacroEntry()
  {
    return macroEntry;
  }

  public Operators getOperators()
  {
    return operators;
  }

  public WffProgram getWffRules()
  {
    return wffRules;
  }

  public FmtProgram getFmtRules()
  {
    return fmtRules;
  }

  public List<ResourceURI> getImports()
  {
    return imports;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(getName());
    int mark = disp.markIndent(2);
    disp.append(" is meta{\n");

    operators.prettyPrint(disp);
    fmtRules.prettyPrint(disp);
    wffRules.prettyPrint(disp);
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
    try (FileOutputStream fos = new FileOutputStream(file)) {
      try (ObjectOutputStream oostream = new ObjectOutputStream(fos)) {
        oostream.writeObject(this);
      }
    }
  }

  @Override
  public CodeTree parse(ResourceURI uri, ErrorReport errors)
  {
    MetaRules newMetaRules = null;
    try (InputStream fis = Resources.getInputStream(uri)) {
      return parse(uri, fis, errors);
    } catch (ResourceException | IOException e) {
      errors.reportError("Resource problem: " + e.getMessage());
    }
    return newMetaRules;
  }

  @Override
  public CodeTree parse(ResourceURI uri, InputStream input, ErrorReport errors) throws ResourceException
  {
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
  public String getExtension()
  {
    return EXTENSION;
  }
}
