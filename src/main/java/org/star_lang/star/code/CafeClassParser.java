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
      try (InputStream input = Resources.getInputStream(uri)) {
        if (input != null)
          return parse(uri, input, errors);
      } catch (IOException e) {
        throw new ResourceException("problem in reading " + uri, e);
      }
    }
    return null;
  }

  @Override
  public CodeTree parse(ResourceURI uri, InputStream input, ErrorReport errors) throws ResourceException
  {
    try {
      byte[] codeArray = new byte[input.available()];
      byte[] buffer = new byte[codeArray.length];
      int pos = 0;
      int read;

      do {
        read = input.read(buffer);
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
        input.close();
      } catch (IOException e) {
      }
    }
  }

}
