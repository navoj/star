package org.star_lang.star.compiler.sources;

import java.util.Collection;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.catalog.Catalog;

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
public class PkgSpec implements PrettyPrintable
{
  private final Collection<ResourceURI> dependants;
  private final ResourceURI uri;
  private final Catalog catalog;
  private final String srcText;
  private final String srcHash;

  public PkgSpec(ResourceURI uri, String srcText, String srcHash, Catalog catalog, Collection<ResourceURI> dependants)
  {
    this.dependants = dependants;
    this.uri = uri;
    this.srcText = srcText;
    this.srcHash = srcHash;
    this.catalog = catalog;
  }

  public Collection<ResourceURI> getDeps()
  {
    return dependants;
  }

  public ResourceURI getUri()
  {
    return uri;
  }

  public String getSrcText()
  {
    return srcText;
  }

  public String getSrcHash()
  {
    return srcHash;
  }

  public Catalog getCatalog()
  {
    return catalog;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    uri.prettyPrint(disp);
    if (!dependants.isEmpty()) {
      disp.append("->[");
      disp.prettyPrint(dependants, ",");
      disp.append("]");
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
