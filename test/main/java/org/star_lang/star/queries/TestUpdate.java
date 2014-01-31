package org.star_lang.star.queries;

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
public class TestUpdate extends SRTest
{
  public TestUpdate()
  {
    super(TestUpdate.class);
  }

  @Test
  public void testUpdateRels()
  {
    runStar("updateRels.star");
  }

  @Test
  public void testUpdateCons()
  {
    runStar("updateCons.star");
  }

  @Test
  public void testActorCrud()
  {
    runStar("actorCrud.star");
  }
}