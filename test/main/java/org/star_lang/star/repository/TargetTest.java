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
