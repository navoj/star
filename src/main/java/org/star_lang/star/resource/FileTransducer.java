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
        throw new ResourceException("file " + file + " not accessable");
      }
    else
      throw new ResourceException("file " + file + " not readable");
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
        throw new ResourceException("file " + file + " not accessable");
      }
    else
      throw new ResourceException("file " + file + " not readable");
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
        throw new ResourceException("cannot create output to file " + file);
      }
    else
      throw new ResourceException("cannot write to file " + file);
  }

  @Override
  public void putResource(ResourceURI uri, String resource) throws ResourceException
  {
    File file = fileFromURI(uri);
    if (file.canWrite())
      try {
        FileUtil.writeFile(file, resource);
      } catch (FileNotFoundException e) {
        throw new ResourceException("cannot write to resource " + file);
      } catch (IOException e) {
        throw new ResourceException(StringUtils.msg("I/O problem ", e.getMessage(), " in writing to ", file));
      }
    else
      throw new ResourceException("cannot write to resource file" + file);
  }
}