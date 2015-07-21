package org.star_lang.star;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.sources.PackageGrapher;
import org.star_lang.star.compiler.sources.PkgSpec;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;

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
 * Manage the compilation of a star file, together with any files that it depends on.
 *
 * This is achieved by first of all performing an import-level dependency analysis on the source
 * file.
 */

public class StarMake
{
  public static void compile(CodeRepository repository, ResourceURI srcURI, Catalog catalog, ErrorReport report)
  {
    compile(repository, FixedList.create(srcURI), srcURI, catalog, report);
  }

  public static void compile(CodeRepository repository, Collection<ResourceURI> uris, ResourceURI srcURI,
      Catalog catalog, ErrorReport report)
  {
    List<List<PkgSpec>> sorted = PackageGrapher.buildImportGraph(uris, repository, catalog, report).sortDependencies();

    if (StarCompiler.SHOWGRAPH)
      report.reportInfo(showGraph(uris, sorted));

    int errCount = report.errorCount();
    Set<ResourceURI> toCompile = new HashSet<>();

    // First of all, we check that there are no circularities in the graph
    for (List<PkgSpec> group : sorted)
      if (group.size() > 1)
        report.reportError("these packages have a circular dependency: " + group);

    for (List<PkgSpec> group : sorted) {
      for (PkgSpec src : group)
        if (report.noNewErrors(errCount)) {
          try {
            ResourceURI uri = src.getUri();
            String hash = src.getSrcHash();
            String currHash = repository.findHash(uri);
            if (currHash == null || !currHash.equals(hash) || toCompile.contains(uri)) {
              repository.removeRepositoryNode(uri);
              CompileDriver.compilePackage(repository, uri, src.getSrcText(), src.getCatalog(), hash, report);

              addDependants(toCompile, src, sorted);
              toCompile.remove(uri);
            }
          } catch (CatalogException | RepositoryException | ResourceException e) {
            report.reportError("catalog problem: " + e.getMessage() + "\nin compiling " + src);
          }
        }
    }
  }

  private static void addDependants(Set<ResourceURI> toCompile, PkgSpec src, List<List<PkgSpec>> sorted)
  {
    for (ResourceURI uri : src.getDeps()) {
      if (!toCompile.contains(uri)) {
        toCompile.add(uri);
        PkgSpec uriSpec = findPkgSpec(sorted, uri);
        if (uriSpec != null)
          addDependants(toCompile, uriSpec, sorted);
      }
    }
  }

  private static PkgSpec findPkgSpec(List<List<PkgSpec>> sorted, ResourceURI uri)
  {
    for (List<PkgSpec> group : sorted) {
      for (PkgSpec pk : group)
        if (pk.getUri().equals(uri))
          return pk;
    }
    return null;
  }

  private static String showGraph(Collection<ResourceURI> uris, List<List<PkgSpec>> pkgs)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();

    disp.append("Pkg dependency graph for ");
    disp.prettyPrint(uris, ", ");
    disp.append("\n");

    String sep = "";
    for (List<PkgSpec> group : pkgs) {
      disp.append(sep);
      sep = "\n";
      disp.prettyPrint(group, "; ");
    }

    disp.append("\n");
    return disp.toString();
  }

  /*
   * Set up a repository with the standard code
   */
  public static void setupRepository(CodeRepository repository, ErrorReport errors)
  {
    compile(repository, StarCompiler.starRulesURI, StarRules.starCatalog(), errors);
  }
}
