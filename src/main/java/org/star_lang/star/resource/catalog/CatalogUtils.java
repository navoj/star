package org.star_lang.star.resource.catalog;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.URIUtils;

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
public class CatalogUtils
{
  public static final String CATALOG = "catalog";

  public static Catalog parseCatalog(ResourceURI uri, Catalog fallback) throws CatalogException, ResourceException
  {
    if (uri.getScheme().equals(Resources.STDSCHEME) && uri.getPath().equals(CATALOG))
      return StarRules.starCatalog();
    else {
      try (Reader reader = Resources.getReader(uri)) {
        if (reader != null) {
          String src = FileUtil.readFileIntoString(reader);
          return parseCatalog(uri, URIUtils.getPackageName(uri), src, fallback);
        } else
          return fallback;
      } catch (IOException e) {
        throw new CatalogException(e.getMessage());
      }
    }
  }

  public static Catalog parseCatalog(ResourceURI uri, String name, String src, Catalog fallback)
      throws CatalogException
  {
    ErrorReport errors = new ErrorReport();

    IAbstract parse = StarCompiler.parseString(src, Location.location(uri), errors);

    if (!errors.isErrorFree())
      throw new CatalogException("error: " + errors);
    else {
      ResourceURI baseURI = null;
      String version = StarCompiler.VERSION; // Pick up command-line option for version
      Map<String, ResourceURI> entries = new HashMap<>();

      if (CompilerUtils.isEquals(parse))
        parse = CompilerUtils.equalityRhs(parse);

      if (CompilerUtils.isBraceTerm(parse, CATALOG)) {
        for (IAbstract entry : CompilerUtils.unWrap(CompilerUtils.braceArg(parse))) {
          if (Abstract.isBinary(entry, StandardNames.IS) && Abstract.isIdentifier(Abstract.binaryLhs(entry), "baseURI")) {
            try {
              baseURI = ResourceURI.parseURI(Abstract.getString(Abstract.binaryRhs(entry)) + "/catalog");
            } catch (ResourceException e) {
              errors.reportError("invalid base URI\nbecause " + e.getMessage(), entry.getLoc());
            }
          } else if ((Abstract.isBinary(entry, StandardNames.IS) || Abstract.isBinary(entry, StandardNames.EQUAL))
              && Abstract.isIdentifier(Abstract.binaryLhs(entry), "version")) {
            version = Abstract.getString(Abstract.binaryRhs(entry));
          } else if (Abstract.isBinary(entry, StandardNames.IS) || Abstract.isBinary(entry, StandardNames.EQUAL)) {
            if (Abstract.isIdentifier(Abstract.binaryLhs(entry), "content")) {
              IAbstract content = Abstract.binaryRhs(entry);

              if (CompilerUtils.isMapTerm(content)) {
                for (IAbstract row : CompilerUtils.unWrap(CompilerUtils.mapContents(content), StandardNames.TERM)) {
                  if (Abstract.isBinary(row, StandardNames.MAP_ARROW)) {
                    IAbstract key = Abstract.binaryLhs(row);
                    IAbstract val = Abstract.binaryRhs(row);
                    if (key instanceof StringLiteral) {
                      String keyName = ((StringLiteral) key).getLit();
                      try {
                        if (Abstract.isBinary(val, StandardNames.AS)
                            && Abstract.isIdentifier(Abstract.binaryRhs(val), "uri")
                            && Abstract.binaryLhs(val) instanceof StringLiteral) {
                          entries
                              .put(keyName, ResourceURI.parseURI(((StringLiteral) Abstract.binaryLhs(val)).getLit()));
                        } else if (val instanceof StringLiteral) {
                          entries.put(keyName, ResourceURI.parseURI(((StringLiteral) val).getLit()));
                        }
                      } catch (ResourceException e) {
                        errors.reportError("invalid URI\nbecause " + e.getMessage(), row.getLoc());
                      }
                    } else
                      errors.reportError("expecting \"name\", not " + key, key.getLoc());
                  } else
                    errors.reportError("expecting: \"name\"->\"uri\"", row.getLoc());
                }
              } else
                errors.reportError("invalid catalog contents " + content, content.getLoc());
            } else
              errors.reportError("expecting 'content = hash{}'", entry.getLoc());
          }
        }

        if (baseURI == null)
          baseURI = uri;
        return new MemoryCatalog(name, version, baseURI, fallback, entries);
      } else
        throw new CatalogException("expecting catalog definition, not " + parse);
    }
  }

  public static Catalog catalogInDirectory(ResourceURI uri, File dir, Catalog fallback) throws CatalogException
  {
    File catFile = dir;
    if (catFile.isDirectory())
      catFile = new File(catFile, CATALOG);
    if (catFile.canRead()) {
      try {
        String catContent = FileUtil.readFileIntoString(catFile);
        return parseCatalog(uri, dir.getName(), catContent, fallback);
      } catch (IOException e) {
        throw new CatalogException(e.getMessage());
      }
    }

    return new NullCatalog();
  }
}
