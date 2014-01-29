package com.starview.platform.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.catalog.Catalog;

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
public class CatalogTransducer implements Transducer
{
  private final Catalog catalog;

  public CatalogTransducer(Catalog catalog)
  {
    this.catalog = catalog;
  }

  @Override
  public Reader getReader(ResourceURI uri) throws ResourceException
  {
    ResourceURI resolve = catalog.resolveNoFallback(uri.getPath());
    if (resolve != null)
      return Resources.getReader(resolve);
    else
      throw new ResourceException("not found");
  }

  @Override
  public InputStream getInputStream(ResourceURI uri) throws ResourceException
  {
    ResourceURI resolve = catalog.resolveNoFallback(uri.getPath());
    if (resolve != null)
      return Resources.getInputStream(resolve);
    else
      throw new ResourceException("not found");
  }

  @Override
  public void putResource(ResourceURI uri, String resource) throws ResourceException
  {
    ResourceURI resolve = catalog.resolveNoFallback(uri.getPath());
    if (resolve != null)
      Resources.putResource(resolve, resource);
    else
      throw new ResourceException("not permitted");
  }

  @Override
  public OutputStream getOutputStream(ResourceURI uri) throws ResourceException
  {
    ResourceURI resolve = catalog.resolveNoFallback(uri.getPath());
    if (resolve != null)
      return Resources.getOutputStream(resolve);
    else
      throw new ResourceException("not permitted");
  }

  @Override
  public boolean exists(ResourceURI uri) throws ResourceException
  {
    ResourceURI resolve = catalog.resolveNoFallback(uri.getPath());
    if (resolve != null)
      return Resources.exists(resolve);
    else
      return false;
  }
}
