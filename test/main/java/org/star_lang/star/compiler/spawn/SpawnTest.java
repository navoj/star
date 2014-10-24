package org.star_lang.star.compiler.spawn;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

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
public class SpawnTest extends SRTest
{
  public SpawnTest()
  {
    super(SpawnTest.class);
  }

  @Test
  public void testPhil()
  {
    runStar("philosophers.star");
  }

  @Test
  public void testSpawnExp()
  {
    runStar("spawnexp.star");
  }

  @Test
  public void testParSieve()
  {
    runStar("parsieve.star");
  }

  @Test
  public void theSieve()
  {
    runStar("thesieve.star");
  }

  @Test
  public void testActorSieve()
  {
    runStar("actorSieve.star");
  }

  @Test
  public void testLocked()
  {
    runStar("lockedVar.star");
  }

  @Test
  public void testMbox()
  {
    runStar("pingpong.star");
  }

  @Test
  public void testConQuery()
  {
    runStar("conQueryTest.star");
  }
}
