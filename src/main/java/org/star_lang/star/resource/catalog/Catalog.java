package org.star_lang.star.resource.catalog;

import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;

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
public interface Catalog extends PrettyPrintable
{
  public static final String CATALOG = "catalog"; // Standard name for a catalog

  /**
   * The name of this catalog
   * 
   * @return
   */
  String getName();

  /**
   * The contents version
   * 
   * @return the version if known, otherwise null.
   */
  String getVersion();

  /**
   * Look for a particular named entity.
   * 
   * @param name
   * @return the uri corresponding to the entity
   * @throws CatalogException
   *           if the named entity is not present
   */
  ResourceURI resolve(String name) throws CatalogException;

  /**
   * Look for a particular named entity, but do not use the fallback catalog even if one is given.
   * 
   * @param name
   * @return the uri corresponding to the entity or null if it is not in the catalog
   * @throws ResourceException
   */
  ResourceURI resolveNoFallback(String name) throws ResourceException;

  /**
   * Resolve a relative uri based on the catalog's idea of where everything is
   */
  ResourceURI resolve(ResourceURI uri) throws ResourceException;

  /**
   * If the catalog permits, add a new entry to the catalog
   * 
   * @param name
   * @param uri
   * @throws CatalogException
   *           if additions are not permitted
   */
  void addEntry(String name, ResourceURI uri) throws CatalogException;
}
