package org.star_lang.star.code.repository;

import org.star_lang.star.compiler.sources.JavaInfo;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.catalog.CatalogException;

/**
 * A CodeRepository should contain all the code of a project. It is not set up as a singleton; but
 * should be treated as such.
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
public interface CodeRepository extends Iterable<RepositoryNode>, ContextLoader
{
  /**
   * Add a repository node with associated code tree. If a code tree for the same source uri exists
   * already, it is overwritten with the new code.
   * 
   * @param uri
   *          the uri of the original source
   * @param hash
   *          an MD5 digest of the contents of the file. This is used in determining whether to
   *          recompile a source that has been previously added to the repository.
   * @param code
   *          the initial code tree to add.
   * @return the newly added node
   * @throws RepositoryException
   *           when it is not possible to add the repository node
   */
  RepositoryNode addRepositoryNode(ResourceURI uri, String hash, CodeCatalog code) throws RepositoryException;

  /**
   * Remove a node from the repository. Attempts to delete all code associated with the uri.
   * 
   * @param uri
   *          to delete. If there is no code associated with the uri, this is a no-operation.
   *          Otherwise, the listeners will be informed that a node has disappeared.
   * @throws CatalogException
   */
  void removeRepositoryNode(ResourceURI uri) throws CatalogException;

  /**
   * Find the code associated with a source.
   * 
   * @param uri
   * @return null if not present. Otherwise, the returned code tree is likely to be a CodeCatalog
   */
  CodeTree findCode(ResourceURI uri);

  /**
   * Find the MD5 of a previously added code. If the associated uri has never been added to the
   * repository, then null will be returned.
   * 
   * @param uri
   * @return the MD5 digest associated with the URI
   */
  String findHash(ResourceURI uri);

  RepositoryNode findNode(ResourceURI uri);

  RepositoryNode findLatestNode(ResourceURI uri);

  JavaInfo locateJava(String className) throws RepositoryException;

  /**
   * The genericCode node contains all the code that is 'generic', not part of one specific package.
   * E.g., all anonymous types and implementations of standard function* interfaces goes in this
   * catalog.
   * 
   * @return
   */
  CodeCatalog synthCodeCatalog();

  /**
   * Get the class loader for this repository
   * 
   * @return
   */
  RepositoryClassLoader classLoader();

  /**
   * Listener pattern
   * 
   * @param listener
   */

  void addListener(RepositoryListener listener);

  void removeListener(RepositoryListener listener);

  public interface RepositoryListener
  {
    /**
     * The nodeUpdated callback is invoked after a node is added or modified within the repository.
     * 
     * @param node
     */
    void nodeUpdated(RepositoryNode node);

    /**
     * The removeNode callback is invoked before the node is actually removed.
     * 
     * If all the registered listeners return true then the node will be removed.
     * 
     * @param node
     * @return true if the callback agrees that the node may be removed.
     */
    boolean removeNode(RepositoryNode node);
  }
}
