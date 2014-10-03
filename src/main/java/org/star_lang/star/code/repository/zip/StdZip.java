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
