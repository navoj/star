package org.star_lang.star.code.repository.zip;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Transducer;

/**
 * A zip uri is a file uri with a fragment that encodes the particular sub-element to extract. E.g.,
 * zip:/foo/bar.zip#alpha/beta/gamma.class
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
public class ZipTransducer implements Transducer
{
  public static final String SCHEME = "zip";

  @Override
  public Reader getReader(ResourceURI uri) throws ResourceException
  {
    InputStream input = getInputStream(uri);
    if (input != null)
      return new InputStreamReader(input);
    else
      return null;
  }

  @Override
  public InputStream getInputStream(ResourceURI uri) throws ResourceException
  {
    File zFile = new File(uri.getPath());
    if (!zFile.canRead())
      throw new ResourceException("cannot access zip file " + uri.getPath());
    else {
      try (ZipFile zip = new ZipFile(zFile)) {
        ZipEntry entry = zip.getEntry(uri.getFragment());
        if (entry != null) {

          InputStream zipStream = zip.getInputStream(entry);
          byte[] contents = FileUtil.readFileIntoBytes(zipStream);
          return new ByteArrayInputStream(contents);
        } else
          throw new ResourceException("cannot access zip entry " + uri);
      } catch (IOException e) {
        throw new ResourceException("cannot access zip file " + uri.getPath(), e);
      }
    }
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

  @Override
  public boolean exists(ResourceURI uri) throws ResourceException
  {
    File zFile = new File(uri.getPath());
    if (!zFile.canRead())
      throw new ResourceException("cannot access zip file " + uri.getPath());
    else {
      try (ZipFile zip = new ZipFile(zFile)) {
        return zip.getEntry(uri.getFragment()) != null;
      } catch (IOException e) {
        throw new ResourceException("cannot access zip file " + uri.getPath(), e);
      }
    }
  }
}
