package org.star_lang.star.code.repository.zip;

import java.io.File;
import java.io.IOException;

import org.star_lang.star.StarMake;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.DirectoryRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.resource.catalog.MemoryCatalog;

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
 *
 *         Create a standard zip file from the StarRules catalog
 * 
 */
public class StdZip
{
  public static void createStdZip(File tgt, ErrorReport errors)
  {
    MemoryCatalog catalog = (MemoryCatalog) StarRules.starCatalog();

    try {
      File tempDir = FileUtil.createTempDir();

      StarMake
          .compile(new DirectoryRepository(tempDir, false, false, errors), catalog.entries(), null, catalog, errors);
      ZipCodeRepository.createZarFromDir(tempDir, tgt);
    } catch (RepositoryException | IOException e) {
      errors.reportError(StringUtils.msg("could not create standard zip archive, \nbecause ", e.getMessage()));
    }
  }

  public static void main(String[] args)
  {
    ErrorReport errors = new ErrorReport();
    File tgt = new File(args[0]);
    createStdZip(tgt, errors);
    if (!errors.isErrorFree())
      System.out.println(errors);
  }
}
