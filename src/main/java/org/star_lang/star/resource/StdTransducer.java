package org.star_lang.star.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.star_lang.star.StarRules;
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

public class StdTransducer implements Transducer
{
  @Override
  public Reader getReader(ResourceURI uri) throws ResourceException
  {
    String fullResourceName = uri.getPath();
    InputStream istream = StarRules.class.getResourceAsStream(fullResourceName);

    if (istream == null && !fullResourceName.startsWith("/")) {
      fullResourceName = "/" + fullResourceName;
      istream = StarRules.class.getResourceAsStream(fullResourceName);
    }
    if (istream != null)
      return new InputStreamReader(istream);
    else
      throw new ResourceException(uri + " not accessable");
  }

  @Override
  public InputStream getInputStream(ResourceURI uri) throws ResourceException
  {
    String fullResourceName = uri.getPath();
    InputStream istream = StarRules.class.getResourceAsStream(fullResourceName);

    if (istream == null && !fullResourceName.startsWith("/")) {
      fullResourceName = "/" + fullResourceName;
      istream = StarRules.class.getResourceAsStream(fullResourceName);
    }
    if (istream != null)
      return istream;
    else
      throw new ResourceException(uri + " not accessable");
  }

  @Override
  public boolean exists(ResourceURI uri) throws ResourceException
  {
    String path = uri.getPath();
    return StarRules.class.getResource(path) != null;
  }

  @Override
  public void putResource(ResourceURI uri, String resource) throws ResourceException
  {
    throw new ResourceException("not permitted");
  }

  @Override
  public OutputStream getOutputStream(ResourceURI uri) throws ResourceException
  {
    throw new ResourceException("not permitted");
  }
}