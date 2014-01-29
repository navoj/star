package org.star_lang.star.code.repository;

import org.star_lang.star.compiler.util.PrettyPrintable;

import com.starview.platform.data.value.ResourceURI;

/**
 * A node in a repository
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

public interface RepositoryNode extends PrettyPrintable
{
  /**
   * Return the repository entry itself. This must be a CodeCatalog -- i.e., a directory of entries
   * 
   * @return
   */
  public CodeCatalog getCode();

  /**
   * Each entry in a repository has a unique hash which is generated from the original source text
   * that the node corresponds to. This is used to decide whether or note a given source needs
   * recompiling.
   * 
   * @return
   */
  public String getHash();

  /**
   * return the uri this repository node is associated with
   */
  public ResourceURI getUri();
}