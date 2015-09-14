package org.star_lang.star.compiler;

import org.junit.Test;

public class RdfMacroTest extends SRTest
{
  @Test
  public void compileRdf()
  {
    runStar("rdf.star");
  }

  @Test
  public void testSimpleGraph()
  {
    runStar("peopleGraph.star");
  }
}
