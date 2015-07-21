package org.star_lang.star.code.repository;

import java.util.Map.Entry;

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

public interface CodeCatalog extends CodeTree, Iterable<Entry<String, CodeTree>>
{
  String EXTENSION = "class";

  /**
   * Add a new code entry to the code catalog
   * 
   * @param path
   *          a slash separated class name.
   * @param code
   * @throws CatalogException
   */
  void addCodeEntry(String path, CodeTree code) throws RepositoryException;

  /**
   * Hierarchical nodes can resolve paths.
   * 
   * @param path
   *          a slash-separated string of path segments. The first segment is the name of a child
   *          tree of this CodeCatalog.
   * @param extension
   *          the file extension to use when looking for this resource as a file
   * @return a CodeTree that corresponds to the requested sub-tree. Returns null if not present.
   */
  CodeTree resolve(String path, String extension) throws RepositoryException;

  /**
   * Hierarchical nodes can resolve paths.
   * 
   * @param path
   *          a slash-separated string of path segments. The first segment is the name of a child
   *          tree of this CodeCatalog.
   * @return a CodeTree that corresponds to the requested sub-tree. Returns null if not present.
   */
  CodeCatalog subCatalog(String path) throws RepositoryException;

  /**
   * Create a sub-catalog by extending the path of this CodeCatalog based on new path.
   * 
   * @param path
   * @return a new code catalog rooted at the new path
   * @throws RepositoryException
   */
  CodeTree fork(String path) throws RepositoryException;

  /**
   * Check to see if an entry exists for a given path
   * 
   * @param path
   * @return true if present
   */
  boolean isPresent(String path);

  /**
   * Look for a particular kind of file
   * 
   * @param path
   * @param extension
   * @return true if present
   */
  boolean isPresent(String path, String extension);

  /**
   * merge a code catalog into this one
   * 
   * @param other
   */
  void mergeEntries(CodeCatalog other) throws RepositoryException;

  /**
   * Is it permissible to write to this catalog?
   * 
   * @return
   */
  boolean isReadWrite();

}
