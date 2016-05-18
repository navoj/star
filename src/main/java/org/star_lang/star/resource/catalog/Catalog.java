package org.star_lang.star.resource.catalog;

import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;

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

public interface Catalog extends PrettyPrintable
{
  String CATALOG = "catalog"; // Standard NAME for a catalog

  /**
   * The NAME of this catalog
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
