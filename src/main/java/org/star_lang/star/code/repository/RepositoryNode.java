package org.star_lang.star.code.repository;

import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.value.ResourceURI;

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
 * A node in a repository
 */


public interface RepositoryNode extends PrettyPrintable
{
  /**
   * Return the repository entry itself. This must be a CodeCatalog -- i.e., a directory of entries
   * 
   * @return
   */
  CodeCatalog getCode();

  /**
   * Each entry in a repository has a unique hash which is generated from the original source text
   * that the node corresponds to. This is used to decide whether or note a given source needs
   * recompiling.
   * 
   * @return
   */
  String getHash();

  /**
   * return the uri this repository node is associated with
   */
  ResourceURI getUri();
}