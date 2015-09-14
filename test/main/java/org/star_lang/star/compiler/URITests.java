package org.star_lang.star.compiler;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.data.value.URIAuthority;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;

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
