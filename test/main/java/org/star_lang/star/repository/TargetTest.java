package org.star_lang.star.repository;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.DirectoryRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.SRTest;

import java.io.File;

/**
 * Set up a test of generating JVM class files
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

public class TargetTest extends SRTest{
  public TargetTest(){
    super(TargetTest.class, setupTargetRepository());
  }

  @Test
  public void runW(){
    runStar("W.star");

    // Again to ensure that the class files are used, no compilation should happen here
    runStar("W.star");
  }

  private static CodeRepository setupTargetRepository(){
    ErrorReport errors = new ErrorReport();
    try {
      File tgt = createDir();

      return new DirectoryRepository(tgt, false, true, Thread.currentThread().getContextClassLoader(), errors);
    } catch (RepositoryException e) {
      Assert.fail(e.getMessage());
      return null;
    }
  }
}
