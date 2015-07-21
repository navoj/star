package org.star_lang.star.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.star_lang.star.StarRules;
import org.star_lang.star.data.value.ResourceURI;

/**
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