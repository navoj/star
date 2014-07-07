package org.star_lang.star.code;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.star_lang.star.code.repository.CodeParser;
import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;

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
public class CafeClassParser implements CodeParser
{
  @Override
  public String getExtension()
  {
    return CafeCode.EXTENSION;
  }

  @Override
  public CodeTree parse(ResourceURI uri, ErrorReport errors) throws ResourceException
  {
    if (uri.getScheme().equals(Resources.FILE))
      return new ClassFile(new File(uri.getPath()), uri.getPath());
    else {
      try (InputStream stream = Resources.getInputStream(uri)) {
        if (stream != null)
          return parse(uri, stream, errors);
      } catch (IOException e) {
        throw new ResourceException("problem in reading " + uri, e);
      }
    }
    return null;
  }

  @Override
  public CodeTree parse(ResourceURI uri, InputStream stream, ErrorReport errors) throws ResourceException
  {
    try {
      byte[] codeArray = new byte[stream.available()];
      byte[] buffer = new byte[codeArray.length];
      int pos = 0;
      int read;

      do {
        read = stream.read(buffer);
        if (read >= 0) {
          if (pos + read > codeArray.length)
            codeArray = Arrays.copyOf(codeArray, codeArray.length + read);

          for (int ix = 0; ix < read; ix++)
            codeArray[pos++] = buffer[ix];
        }
      } while (read >= 0);
      if (pos < codeArray.length) // trim the code buffer
        codeArray = Arrays.copyOf(codeArray, pos);

      return new CafeCode(uri.getPath(), codeArray);

    } catch (IOException e) {
      throw new ResourceException("could not access resource", e);
    } finally {
      try {
        stream.close();
      } catch (IOException e) {
      }
    }
  }

}
