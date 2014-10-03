package org.star_lang.star.compiler.sources;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.grammar.Token;
import org.star_lang.star.compiler.grammar.Token.TokenType;
import org.star_lang.star.compiler.grammar.Tokenizer;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.TopologySort;
import org.star_lang.star.compiler.util.TopologySort.IDefinition;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.CatalogUtils;
import org.star_lang.star.resource.catalog.URIBasedCatalog;

/**
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
public class PackageGrapher implements PrettyPrintable
{
  // functions to build a dependency graph of which packages depend on which

  private final Map<ResourceURI, PkgGraph> graph = new HashMap<>();
  private final CodeRepository repository;
  private final ErrorReport errors;

  private PackageGrapher(CodeRepository repository, ErrorReport errors)
  {
    this.repository = repository;
    this.errors = errors;
  }

  public static PackageGrapher buildImportGraph(ResourceURI uri, CodeRepository repository, Catalog catalog,
      ErrorReport errors)
  {
    errors.startTimer("dependency for " + uri);

    PackageGrapher grapher = new PackageGrapher(repository, errors);

    try {
      grapher.processGraph(uri, catalog);
    } catch (ResourceException e) {
      e.printStackTrace();
    }

    errors.recordTime("dependency for " + uri);

    return grapher;
  }

  public static PackageGrapher buildImportGraph(Collection<ResourceURI> pkgs, CodeRepository repository,
      Catalog catalog, ErrorReport errors)
  {
    PackageGrapher grapher = new PackageGrapher(repository, errors);

    for (ResourceURI pkg : pkgs)
      try {
        grapher.processGraph(pkg, catalog);
      } catch (ResourceException e) {
        errors.reportError(StringUtils.msg("problem in accessing ", pkg, "\nbecause ", e.getMessage()));
      }

    return grapher;
  }

  public List<List<PkgSpec>> sortDependencies()
  {
    errors.startTimer("sort dependencies");
    List<List<IDefinition<ResourceURI>>> groups = TopologySort.sort(graph.values());
    List<List<PkgSpec>> sortd = new ArrayList<>();
    for (List<IDefinition<ResourceURI>> entry : groups) {
      List<PkgSpec> group = new ArrayList<>();
      for (IDefinition<ResourceURI> def : entry) {
        PkgGraph pkgGraph = (PkgGraph) def;
        group.add(new PkgSpec(pkgGraph.uri, pkgGraph.getText(), pkgGraph.getHash(), pkgGraph.getCatalog(),
            pkgGraph.links));
      }
      sortd.add(group);
    }
    errors.recordTime("sort dependencies");
    return sortd;
  }

  public Map<ResourceURI, Collection<ResourceURI>> linkMap()
  {
    Map<ResourceURI, Collection<ResourceURI>> links = new HashMap<>();

    for (Entry<ResourceURI, PkgGraph> entry : graph.entrySet()) {
      links.put(entry.getKey(), entry.getValue().getLinks());
    }
    return links;
  }

  private PkgGraph processPkg(ResourceURI uri, Catalog catalog)
  {
    errors.startTimer(uri.toString());
    assert !graph.containsKey(uri);

    try {
      String srcText = Resources.getUriContent(uri);
      if (srcText != null) {
        String hash = Resources.resourceHash(srcText);
        PkgGraph pkg = new PkgGraph(uri, srcText, hash, catalog);

        graph.put(uri, pkg);

        Tokenizer tokenizer = new Tokenizer(new ErrorReport.NullErrorReporter(), new StringReader(srcText), Location
            .location(uri));
        Token tok = tokenizer.nextToken();
        while (tok.getType() != TokenType.terminal) {
          if (tok.isIdentifier(StandardNames.IMPORT)) {
            tok = tokenizer.nextToken();

            try {
              final ResourceURI refUri;

              switch (tok.getType()) {
              case identifier:
                refUri = catalog.resolve(tok.getImage());
                break;
              case string:
                refUri = catalog.resolve(URIUtils.parseUri(tok.getImage()));
                break;
              default:
                refUri = null;
              }
              if (refUri != null) {
                if (!pkg.references.contains(refUri))
                  referToPkg(pkg, refUri);
              }
            } catch (CatalogException e) {
            }
          }
          tok = tokenizer.nextToken();
        }
        return pkg;
      } else
        return null;
    } catch (ResourceException e) {
      return null;
    } finally {
      errors.recordTime(uri.toString());
    }
  }

  private void referToPkg(PkgGraph pkg, final ResourceURI refUri)
  {
    // Handle this carefully, the source may not be accessible
    PkgGraph refPkg = graph.get(refUri);

    if (refPkg == null) {
      Catalog refCatalog = lookForCatalog(refUri, pkg.getCatalog(), repository);
      if (refCatalog != null)
        refPkg = processPkg(refUri, refCatalog);
    }
    if (refPkg != null) {
      refPkg.linkFromReferringPkg(pkg);
      pkg.references.add(refUri);
    }
  }

  private class PkgGraph implements IDefinition<ResourceURI>, PrettyPrintable
  {
    private final ResourceURI uri;
    private final Catalog catalog;
    private final Set<ResourceURI> references = new HashSet<>();
    private final Set<ResourceURI> links = new HashSet<>();
    private final String text;
    private final String hash;

    public PkgGraph(ResourceURI uri, String text, String hash, Catalog catalog)
    {
      this.uri = uri;
      this.catalog = catalog;
      this.text = text;
      this.hash = hash;
    }

    private void linkFromReferringPkg(PkgGraph referring)
    {
      ResourceURI refUri = referring.getUri();
      if (!links.contains(refUri))
        links.add(refUri);
    }

    public Collection<ResourceURI> getLinks()
    {
      return links;
    }

    public Catalog getCatalog()
    {
      return catalog;
    }

    public ResourceURI getUri()
    {
      return uri;
    }

    public String getText()
    {
      return text;
    }

    public String getHash()
    {
      return hash;
    }

    @Override
    public boolean defines(ResourceURI uri)
    {
      return this.uri.equals(uri);
    }

    @Override
    public Collection<ResourceURI> definitions()
    {
      return FixedList.create(uri);
    }

    @Override
    public Collection<ResourceURI> references()
    {
      return references;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      uri.prettyPrint(disp);
      if (!references.isEmpty()) {
        disp.append("->");
        disp.prettyPrint(references, ", ");
        disp.append(".");
      }
    }

    @Override
    public String toString()
    {
      return PrettyPrintDisplay.toString(this);
    }
  }

  private void processGraph(ResourceURI pkg, Catalog catalog) throws ResourceException
  {
    if (pkg != null) {
      if (!graph.containsKey(pkg))
        processPkg(pkg, catalog);
    }
  }

  public static Catalog lookForCatalog(ResourceURI uri, Catalog fallback, CodeRepository repository)
  {
    try {
      return CatalogUtils.parseCatalog(uri.resolve(Catalog.CATALOG), fallback);
    } catch (Exception e) {
      return new URIBasedCatalog(uri, fallback);
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    for (Entry<ResourceURI, PkgGraph> entry : graph.entrySet()) {
      entry.getKey().prettyPrint(disp);
      disp.append(": ");
      disp.prettyPrint(entry.getValue().references(), ", ");
      disp.append("\n");
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
