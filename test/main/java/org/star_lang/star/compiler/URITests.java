package org.star_lang.star.compiler;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.data.value.URIAuthority;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;

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
 */
public class URITests extends SRTest
{
  public URITests()
  {
    super(URITests.class);
  }

  @Test
  public void parseURITest()
  {
    runStar("parseURI.star");
  }

  @Test
  public void testFileURIParse()
  {
    try {
      ResourceURI test = URIUtils.parseUri("file:///dir/test");
      Assert.assertEquals(test.getPath(), "/dir/test");
      Assert.assertEquals(test.getAuthority(), URIAuthority.noAuthorityEnum);
      Assert.assertNull(test.getQuery());
      Assert.assertNull(test.getFragment());
      Assert.assertEquals(test.getScheme(), "file");
    } catch (ResourceException e) {
      Assert.fail("unexpected exception");
    }
  }

  @Test
  public void showUri()
  {
    runStar("showUri.star");
  }
}
