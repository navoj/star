package org.star_lang.star.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.star_lang.star.data.value.ResourceURI;

/**
 * The Transducer interface is used when accessing and maintaining resources
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
