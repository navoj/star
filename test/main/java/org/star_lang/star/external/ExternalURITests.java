package org.star_lang.star.external;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.URIUtils;

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
