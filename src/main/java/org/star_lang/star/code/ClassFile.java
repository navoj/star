package org.star_lang.star.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;



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

@SuppressWarnings("serial")
public class ClassFile implements CodeTree, HasCode
{
  private final File file;
  private final String path;

  public ClassFile(File file, String path)
  {
    this.file = file;
    this.path = path;
  }

  @Override
  public void write(File output) throws IOException
  {
    FileUtil.copyFile(file, output);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("code file ").append(file.toString());
  }

  @Override
  public String getPath()
  {
    return path;
  }

  @Override
  public String getExtension()
  {
    return CafeCode.EXTENSION;
  }

  @Override
  public byte[] getCode()
  {
    try (FileInputStream rdr = new FileInputStream(file)) {
      return FileUtil.readFileIntoBytes(rdr);
    } catch (IOException e) {
      return null;
    }
  }
}
