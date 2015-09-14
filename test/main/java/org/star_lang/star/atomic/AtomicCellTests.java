package org.star_lang.star.atomic;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

public class AtomicCellTests extends SRTest
{
  public AtomicCellTests()
  {
    super(AtomicCellTests.class);
  }

  @Test
  public void testBasicCell()
  {
    runStar("basicAtomic.star");
  }

  @Test
  public void testCompareAndSwap()
  {
    runStar("casTest.star");
  }
}
