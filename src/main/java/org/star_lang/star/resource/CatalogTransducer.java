package org.star_lang.star.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.catalog.Catalog;
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
