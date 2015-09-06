package org.star_lang.star;

import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.TemplateString;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.CatalogException;

/**
 * Utility to convert a string representing a query against a schema into a function that can
 * evaluate the query
 *
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
public class ParseQuery
{
  private static final String expTemplate = "$pkg is package{\n  $imprt\n"
      + "fun $fn(#($v has type $schema)#) is let{ open $v } in #($exp)#;\n}\n";

  public static IFunction parseExpression(String query, String imprt, String face, CodeRepository repository)
      throws LanguageException
  {
    /**
     * Construct the following program in a string:
     * 
     * <pre>
     * lbl is package{
     *   <import>
     *   fun(v99) is <query> using (v99 has type <face>);
     * }
     * </pre>
     */

    Map<String, String> vars = new HashMap<>();

    String pkgName = GenSym.genSym("pkg");
    vars.put("pkg", pkgName);

    if (imprt != null)
      vars.put("imprt", "import " + imprt + ";\n");
    else
      vars.put("imprt", "");

    String fnName = GenSym.genSym("fun");
    vars.put("fn", fnName);
    String var = GenSym.genSym("v");
    vars.put("v", var);
    vars.put("exp", query);
    vars.put("schema", face);

    String pkg = TemplateString.stringTemplate(expTemplate, vars);

    try {
      ResourceURI uri = URIUtils.createQuotedURI(pkgName, pkg);
      ErrorReport errors = new ErrorReport();

      IValue fun = StarCompiler.localCompile(repository, uri, StarRules.starCatalog(), errors);

      if (errors.isErrorFree()) {
        if (fun instanceof IRecord)
          return (IFunction) ((IRecord) fun).getMember(fnName);
        else {
          errors.reportError(StringUtils.msg("result of parse ", query, " not valid"), Location.nullLoc);
          throw new LanguageException(errors);
        }
      } else
        throw new LanguageException(errors);
    } catch (EvaluationException | ResourceException | CatalogException | RepositoryException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Construct a query function that can be used against a port.
   * <p/>
   * Takes an expression <exp> and a port schema <schema> and constructs:
   * <p/>
   * 
   * <pre>
   * fun99 is package{
   *   import speechContract;
   * 
   *   fun99(P) is P.Query((function(v99) is Exp using (v99 has type <schema>)),
   *                       (function() is quote(Exp)), (function() is hash{<free>}))
   * }
   * </pre>
   */

  private static final String queryTemplate = "$pkg is package{\n  $imprt fun $fn($freeVars) is (($prt) =>"
      + "  $prt.Query(((#(v has type $schema)#) => let{ open v } in #($exp)#),\n"
      + "             (() => quote(#($exp)#)),\n"
      + "             (() => dictionary of [$freeHash])));"
      + "\n}\n";

  public static IFunction parseQuery(String query, Map<String, IValue> free, String imprt, String face,
      CodeRepository repository) throws LanguageException
  {
    Map<String, String> vars = new HashMap<>();

    StringBuilder freeVars = new StringBuilder();
    StringBuilder freeHash = new StringBuilder();
    IValue freeArgs[] = new IValue[free.size()];
    if (!free.isEmpty()) {
      int ix = 0;
      String sep = "";
      for (Map.Entry<String, IValue> freeVar : free.entrySet()) {
        freeVars.append(sep); // set up the free args
        freeHash.append(sep);
        sep = ",";
        freeVars.append(freeVar.getKey());
        freeArgs[ix++] = freeVar.getValue();
        freeHash.append("      "); // set up the free hash
        freeHash.append(StringUtils.quoteString(freeVar.getKey()));
        freeHash.append(" -> (");
        freeHash.append(freeVar.getKey());
        freeHash.append(" cast any)");
        freeHash.append("\n ");
      }
    }

    String pkgName = GenSym.genSym("pkg");
    vars.put("pkg", pkgName);

    if (imprt != null)
      vars.put("imprt", "import " + imprt + ";\n  ");
    else
      vars.put("imprt", "");

    vars.put("freeVars", freeVars.toString());
    vars.put("freeHash", freeHash.toString());

    String prt = GenSym.genSym("p");
    vars.put("prt", prt);
    String fnName = GenSym.genSym("fun");
    vars.put("fn", fnName);
    String var = GenSym.genSym("v");
    vars.put("v", var);
    vars.put("exp", query);
    vars.put("schema", face);

    String pkg = TemplateString.stringTemplate(queryTemplate, vars);

    try {
      ResourceURI uri = URIUtils.createQuotedURI(pkgName, pkg);
      ErrorReport errors = new ErrorReport();
      IValue compiled = StarCompiler.localCompile(repository, uri, StarRules.starCatalog(), errors);

      if (errors.isErrorFree()) {
        if (compiled instanceof IRecord) {
          IFunction fun = (IFunction) ((IRecord) compiled).getMember(fnName);
          return (IFunction) fun.enter(freeArgs);
        } else {
          errors.reportError(StringUtils.msg("result of parse ", query, " not valid"), Location.nullLoc);
          throw new LanguageException(errors);
        }
      } else
        throw new LanguageException(errors);
    } catch (EvaluationException | ResourceException | CatalogException | RepositoryException e) {
      e.printStackTrace();
    }

    return null;
  }
}
