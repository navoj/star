package org.star_lang.star.code.repository;

import java.io.File;
import java.io.IOException;

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
public class CodeHash implements CodeTree
{
  private final String hash;
  public static final String HASH = "source$hash";

  public CodeHash(String hash)
  {
    this.hash = hash;
  }

  public String getHash()
  {
    return hash;
  }

  @Override
  public void write(File output) throws IOException
  {
    FileUtil.writeFile(output, hash);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendQuoted(hash);
  }

  @Override
  public String getPath()
  {
    return HASH;
  }

  @Override
  public String getExtension()
  {
    return null;
  }

}
