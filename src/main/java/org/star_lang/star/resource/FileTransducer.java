package org.star_lang.star.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.StringUtils;
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
public class FileTransducer implements Transducer
{
  private static String fileSeparator = File.separator;

  @Override
  public Reader getReader(ResourceURI uri) throws ResourceException
  {
    File file = fileFromURI(uri);
    if (file.canRead())
      try {
        return new FileReader(file);
      } catch (FileNotFoundException e) {
        throw new ResourceException(file + " not accessable");
      }
    else
      throw new ResourceException(file + " not accessable");
  }

  private static File fileFromURI(ResourceURI uri)
  {
    return new File(uri.getPath().replace("/", fileSeparator));
  }

  @Override
  public InputStream getInputStream(ResourceURI uri) throws ResourceException
  {
    File file = fileFromURI(uri);
    if (file.canRead())
      try {
        return new FileInputStream(file);
      } catch (FileNotFoundException e) {
        throw new ResourceException(file + " not accessable");
      }
    else
      throw new ResourceException(file + " not accessable");
  }

  @Override
  public boolean exists(ResourceURI uri) throws ResourceException
  {
    File file = new File(uri.getPath());
    return file.exists();
  }

  @Override
  public OutputStream getOutputStream(ResourceURI uri) throws ResourceException
  {
    File file = fileFromURI(uri);
    if (file.canWrite())
      try {
        return new FileOutputStream(file);
      } catch (FileNotFoundException e) {
        throw new ResourceException(file + " not accessable");
      }
    else
      throw new ResourceException(file + " not accessable");
  }

  @Override
  public void putResource(ResourceURI uri, String resource) throws ResourceException
  {
    File file = fileFromURI(uri);
    if (file.canWrite())
      try {
        FileUtil.writeFile(file, resource);
      } catch (FileNotFoundException e) {
        throw new ResourceException(file + " not accessable");
      } catch (IOException e) {
        throw new ResourceException(StringUtils.msg("I/O problem ", e.getMessage(), " in writing to ", file));
      }
    else
      throw new ResourceException(file + " not accessable");
  }
}