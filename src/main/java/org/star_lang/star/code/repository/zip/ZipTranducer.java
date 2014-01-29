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

import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.Transducer;

/**
 * A zip uri is a file uri with a fragment that encodes the particular sub-element to extract. E.g.,
 * zip:/foo/bar.zip#alpha/beta/gamma.class
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
public class ZipTranducer implements Transducer
{
  public static final String SCHEME = "zip";

  @Override
  public Reader getReader(ResourceURI uri) throws ResourceException
  {
    InputStream stream = getInputStream(uri);
    if (stream != null)
      return new InputStreamReader(stream);
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
      } catch (ZipException e) {
        throw new ResourceException("cannot access zip file " + uri.getPath(), e);
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
      } catch (ZipException e) {
        throw new ResourceException("cannot access zip file " + uri.getPath(), e);
      } catch (IOException e) {
        throw new ResourceException("cannot access zip file " + uri.getPath(), e);
      }
    }
  }
}
