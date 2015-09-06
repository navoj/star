package org.star_lang.star.resource;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.star_lang.star.compiler.util.StringUtils;
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

public class HttpTransducer implements Transducer
{

  @Override
  public Reader getReader(ResourceURI uri) throws ResourceException
  {
    try {
      URL url = new URL(uri.toString());
      InputStream input = url.openStream();
      return new InputStreamReader(input);
    } catch (Exception e) {
      throw new ResourceException(e.getMessage());
    }
  }

  @Override
  public InputStream getInputStream(ResourceURI uri) throws ResourceException
  {
    try {
      URL url = new URL(uri.toString());
      return url.openStream();
    } catch (Exception e) {
      throw new ResourceException(e.getMessage());
    }
  }

  @Override
  public void putResource(ResourceURI uri, String resource) throws ResourceException
  {
    URL url;
    try {
      url = new URL(uri.toString());

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("PUT");
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Length", Integer.toString(resource.length()));
      try (DataOutputStream wrtr = new DataOutputStream(conn.getOutputStream())) {
        wrtr.writeBytes(resource);
        wrtr.flush();
      }
      int responseCode = conn.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK)
        throw new ResourceException("response from http server: " + responseCode);
    } catch (MalformedURLException e) {
      throw new ResourceException("bad uri " + uri);
    } catch (IOException e) {
      throw new ResourceException(StringUtils.msg("I/O problem ", e.getMessage(), " in writing to http server"));
    }

  }

  @Override
  public OutputStream getOutputStream(ResourceURI uri) throws ResourceException
  {
    throw new ResourceException("not permitted");
  }

  @Override
  public boolean exists(ResourceURI uri) throws ResourceException
  {
    throw new ResourceException("not implemented");
  }
}
