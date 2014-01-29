package org.star_lang.star.compiler.overload;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

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
public class OverloadTests extends SRTest
{
  public OverloadTests()
  {
    super(OverloadTests.class);
  }

  @Test
  public void testBasicOver()
  {
    runStar("fanpl.star");
  }

  @Test
  public void testActorOver()
  {
    runStar("actorOverload.star");
  }

  @Test
  public void testDoubleOver()
  {
    runStar("reducible.star");
  }

  @Test
  public void testContractInActor()
  {
    runStar("contractInActor.star");
  }

  @Test
  public void testOverloadedDefn()
  {
    runStar("overDefActor.star");
  }

  @Test
  public void testForEach()
  {
    runStar("forEach.star");
  }
}
