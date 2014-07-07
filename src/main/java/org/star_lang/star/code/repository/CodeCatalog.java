package org.star_lang.star.code.repository;

import java.util.Map.Entry;

import org.star_lang.star.resource.catalog.CatalogException;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public interface CodeCatalog extends CodeTree, Iterable<Entry<String, CodeTree>>
{
  public static final String EXTENSION = "class";

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
  boolean isReadOnly();

}
