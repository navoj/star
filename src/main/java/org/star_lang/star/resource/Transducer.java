package org.star_lang.star.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.star_lang.star.data.value.ResourceURI;

/**
 * The Transducer interface is used when accessing and maintaining resources
 *
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

public interface Transducer
{
  /**
   * Access the contents of a resource as a reader
   * 
   * @param uri
   * @return the string contents of the resource -- if possible.
   * @throws ResourceException
   *           if the resource does not exist or cannot be accessed
   */
  Reader getReader(ResourceURI uri) throws ResourceException;

  /**
   * Access the resource as a stream of bytes
   * 
   * @param uri
   * @return
   * @throws ResourceException
   */
  InputStream getInputStream(ResourceURI uri) throws ResourceException;

  /**
   * PUT the identified resource at the location indicated by the uri.
   * 
   * @param uri
   * @param resource
   * @throws ResourceException
   */
  void putResource(ResourceURI uri, String resource) throws ResourceException;

  /**
   * Get a writer that allows us to write to the uri.
   * 
   * @param uri
   * @return
   * @throws ResourceException
   *           especially if you cannot write to the resource
   */
  OutputStream getOutputStream(ResourceURI uri) throws ResourceException;

  /**
   * Try to determine if the uri identifies an accessable resource
   * 
   * @param uri
   *          the uri to verify
   * 
   * @return true if can read, false if you cannot
   * @throws ResourceException
   *           if the uri is not well formed
   */
  boolean exists(ResourceURI uri) throws ResourceException;
}
