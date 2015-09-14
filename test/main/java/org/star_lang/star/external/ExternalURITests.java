package org.star_lang.star.external;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;

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

public class ExternalURITests extends SRTest
{
  public File tmpDir;

  public ExternalURITests()
  {
    super(ExternalURITests.class);
  }

  @Before
  public void createTempDir() throws ResourceException
  {
    tmpDir = createDir("catalog", "worldEx.star", "helloAfternoon.star");
  }

  @Test
  public void testFileLoad() throws ResourceException
  {
    runStar(URIUtils.createFileURI(new File(tmpDir,"worldEx.star")));
  }
}
