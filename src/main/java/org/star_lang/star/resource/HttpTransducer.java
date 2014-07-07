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
public class HttpTransducer implements Transducer
{

  @Override
  public Reader getReader(ResourceURI uri) throws ResourceException
  {
    try {
      URL url = new URL(uri.toString());
      InputStream stream = url.openStream();
      return new InputStreamReader(stream);
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
