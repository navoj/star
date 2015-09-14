package org.star_lang.star.imports;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

public class ImportTest extends SRTest{
  public ImportTest(){
    super(ImportTest.class);
  }

  @Test
  public void testHelloWorld(){
    runStar("world.star");
  }

  @Test
  public void testAliasImport(){
    runStar("baz.star");
  }

  @Test
  public void testActorImport(){
    runStar("remActor.star");
  }

  @Test
  public void testIndirectImport(){
    runStar("indirect.star");
  }

  @Test
  public void testStaticImport(){
    runStar("commonStatic.star");
  }

  @Test
  public void testSimpleNamedImport(){
    runStar("basicNamedImport.star");
  }

  @Test
  public void testNamedRecord(){
    runStar("importRecords.star");
  }
}
